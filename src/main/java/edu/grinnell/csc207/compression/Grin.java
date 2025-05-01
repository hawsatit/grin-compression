package edu.grinnell.csc207.compression;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The driver for the Grin compression program.
 */
public class Grin {

    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     *
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     */
    public static void decode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        // Step 1: Read magic number (first 32 bits)
        int magic = in.readBits(32);
        if (magic != 0x736) {
            throw new IllegalArgumentException("incorrect magic number.");
        }

        HuffmanTree tree = new HuffmanTree(in);
        tree.decode(in, out);

        in.close();
        out.close();
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of those
     * sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     *
     * @param file the file to read
     * @return a freqency map for the given file
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        BitInputStream in = new BitInputStream(file);
        Map<Short, Integer> freqMap = new HashMap<>();
        int val;
        while ((val = in.readBits(8)) != -1) {
            short sVal = (short) val;
            freqMap.put(sVal, freqMap.getOrDefault(sVal, 0) + 1);
        }
        return freqMap;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     *
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);

        Map<Short, Integer> freqMap = createFrequencyMap(infile);
        HuffmanTree tree = new HuffmanTree(freqMap);
        out.writeBits(0x736, 32);
        tree.serialize(out);
        in = new BitInputStream(infile);
        tree.encode(in, out);

        in.close();
        out.close();

        FileInputStream checkOut = new FileInputStream(outfile);
        for (int i = 0; i < 4; i++) {
            int b = checkOut.read();
            if (b == -1) {
                break;
            }
            System.out.printf("%02X ", b);
        }
        checkOut.close();
    }

    /**
     * The entry point to the program.
     *
     * @param args the command-line arguments.
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        if (args.length != 3) {
            throw new IOException("Invalid arguements. "
                    + "\n Please use: java Grin <encode|decode> <infile> <outfile> ");
        }
        if ("encode".equals(args[0])) {
            encode(args[1], args[2]);
        } else if ("decode".equals(args[0])) {
            decode(args[1], args[2]);
        } else {
            throw new IOException("Invalid arguements. "
                    + "\n Please use: java Grin <encode|decode> <infile> <outfile> ");
        }
    }
}
