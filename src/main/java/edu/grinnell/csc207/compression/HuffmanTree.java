package edu.grinnell.csc207.compression;

import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits.  However, we also need to encode a special EOF character to
 * denote the end of a .grin file.  Thus, we need 9 bits to store each
 * byte value.  This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {
    
    /**
     * 
     */
    private class Node implements Comparable<Node>{
        Short value;
        Integer frequency;
        Node left;
        Node right;
        
        public Node(Short value, Integer frequency){
            this.frequency = frequency;
            this.value = value;
        }
        
        boolean isLeaf() {
            return (left == null) && (right == null);
        }

        @Override
        public int compareTo(Node o) {
            return this.frequency - o.frequency;
        }
    }
    
    
    /**
     * Constructs a new HuffmanTree from a frequency map.
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree (Map<Short, Integer> freqs) {
        PriorityQueue<Node> p = new PriorityQueue<>();
        
        for (Map.Entry<Short, Integer> nodeValues : freqs.entrySet()) {
            p.add(new Node(nodeValues.getKey(), nodeValues.getValue()));
        }
        
        while (p.size() >= 2){
            //get the left and right of the head of the queue and remove them, saving the values
            Node left = p.poll();    
            Node right = p.poll();   

            //Make the parent node the combined frequency and its child the left and right 
            Node parent = new Node(null, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            p.add(parent);
        }

    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {
        // TODO: fill me in
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public void serialize (BitOutputStream out) {
        // TODO: fill me in!
    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }
}
