package edu.grinnell.csc207.compression;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally take
 * 8 bits. However, we also need to encode a special EOF character to denote the
 * end of a .grin file. Thus, we need 9 bits to store each byte value. This is
 * fine for file writing (modulo the need to write in byte chunks to the file),
 * but Java does not have a 9-bit data type. Instead, we use the next larger
 * primitive integral type, short, to store our byte values.
 */
public class HuffmanTree {

    /**
     * Node class for the HuffmanTree.
     */
    private class Node implements Comparable<Node> {

        Short value;
        Integer frequency;
        Node left;
        Node right;

        // for leafs
        public Node(Short value, Integer frequency) {
            this.frequency = frequency;
            this.value = value;
        }

        // for non-leafs
        public Node(Node left, Node right) {
            this.value = -1; // non-leaf
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return (left == null) && (right == null);
        }

        @Override
        public int compareTo(Node o) {
            return this.frequency - o.frequency;
        }
    }

    // initialize the root node and the huffman code map
    private Node root;
    Map<Short, String> encodingMap;

    /**
     * Constructs a new HuffmanTree from a frequency map.
     *
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        freqs.put((short) 256, 1);

        PriorityQueue<Node> p = new PriorityQueue<>();

        for (Map.Entry<Short, Integer> nodeValues : freqs.entrySet()) {
            p.add(new Node(nodeValues.getKey(), nodeValues.getValue()));
        }

        while (p.size() >= 2) {
            Node left = p.poll();
            Node right = p.poll();

            Node parent = new Node(left, right);
            p.add(parent);
        }

        this.root = p.poll();
        encodingMap = new HashMap<>();
        buildEncodingMap(root, "");
    }

    /**
     * Function to create a map for moving through the huffman tree.
     * Recursively traverse until reaching a leaf and maps the
     * value of the leaf as well as the path.
     * 0 is left, 1 is right.
     *
     * @param node the current node
     * @param path the current path
     */
    private void buildEncodingMap(Node node, String path) {
        if (node.isLeaf()) {
            encodingMap.put(node.value, path);
            return;
        }
        buildEncodingMap(node.left, path + "0");
        buildEncodingMap(node.right, path + "1");
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     *
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) throws IOException {
        root = readTree(in);
        encodingMap = new HashMap<>();
        buildEncodingMap(root, "");
    }

    /**
     * Reads in the serialized tree and makes it into a Huffman tree.
     *
     * @param in the bit input stream
     * @return the root node
     * @throws IOException if input is invalid
     */
    private Node readTree(BitInputStream in) throws IOException {
        int bit = in.readBits(1);
        if (bit == -1) {
            throw new EOFException("Unexpected end of stream while reading tree");
        }
        if (bit == 0) {
            int value = in.readBits(9);
            return new Node((short) value, 0);
        } else if (bit == 1) {
            Node left = readTree(in);
            Node right = readTree(in);
            return new Node(left, right);
        } else {
            throw new EOFException("Unexpected character, must be binary");
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     *
     * @param out the output file as a BitOutputStream
     */
    public void serialize(BitOutputStream out) {
        serializeRec(out, this.root);
    }

    /**
     * Recursive function for writing out a HuffmanTree
     * in a serialized form.
     *
     * @param out the output stream
     * @param cur the current node
     */
    public void serializeRec(BitOutputStream out, Node cur) {
        if (cur.isLeaf()) {
            out.writeBit(0);
            out.writeBits(cur.value, 9);
        } else {
            out.writeBit(1);
            serializeRec(out, cur.left);
            serializeRec(out, cur.right);
        }
    }

    /**
     * Encodes the file given as a stream of bits into a compressed format using
     * this Huffman tree. The encoded values are written, bit-by-bit to the
     * given BitOuputStream.
     *
     * @param in the file to compress
     * @param out the file to write the compressed output to
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        int val;
        while ((val = in.readBits(8)) != -1) {
            String code = encodingMap.get((short) val);
            for (char bit : code.toCharArray()) {
                if (bit == '1') {
                    out.writeBit(1);
                } else {
                    out.writeBit(0);
                }
            }
        }

        String eofCode = encodingMap.get((short) 256);
        for (char bit : eofCode.toCharArray()) {
            if (bit == '1') {
                out.writeBit(1);
            } else {
                out.writeBit(0);
            }
        }
    }

    /**
     * Decodes a stream of Huffman codes from a file given as a stream of bits
     * into their uncompressed form, saving the results to the given output
     * stream. Note that the EOF character is not written to out because it is
     * not a valid 8-bit chunk (it is 9 bits).
     *
     * @param in the file to decompress
     * @param out the file to write the decompressed output to
     * @throws EOFException if stream ends unexpectedly
     */
    public void decode(BitInputStream in, BitOutputStream out) throws EOFException {
        Node current = root;
        while (true) {
            int bit = in.readBits(1);
            if (bit == -1) {
                throw new EOFException("Unexpected end of stream");
            }

            if (bit == 0) {
                current = current.left;
            } else if (bit == 1) {
                current = current.right;
            }

            if (current.isLeaf()) {
                if (current.value == 256) {
                    break;
                }
                out.writeBits(current.value, 8);
                current = root;
            }
        }
    }
}
