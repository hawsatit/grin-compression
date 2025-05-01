package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class Tests {

    private Map<Short, Integer> frequencies;
    private HuffmanTree huffmanTree;

    public void setUp() {
        // Sample frequencies for a simple test
        frequencies = new HashMap<>();
        frequencies.put((short) 0, 5);   // 'a'
        frequencies.put((short) 1, 9);   // 'b'
        frequencies.put((short) 2, 12);  // 'c'
        frequencies.put((short) 3, 13);  // 'd'
        frequencies.put((short) 4, 16);  // 'e'
        frequencies.put((short) 256, 1); // EOF

        huffmanTree = new HuffmanTree(frequencies);
    }

    @Test
    public void testHuffmanTreeConstruction() {
        setUp();
        assertNotNull(huffmanTree);
    }

    @Test
    public void testBuildEncodingMap() {
        // Testing that encoding map is created correctly (check if 'e' has a short encoding)
        setUp();
        String codeForE = huffmanTree.encodingMap.get((short) 4);
        assertNotNull(codeForE);
        assertTrue(codeForE.length() > 0, "Encoding for 'e' should not be empty");
    }


    @Test
    public void testSerialize() throws IOException {
        setUp();
        //not a real file just checking that there are no exceptions
        BitOutputStream out = new BitOutputStream("tree_output.txt");
        huffmanTree.serialize(out);
    }

    @Test
    public void testEOFCharacter() {
        setUp();
        assertTrue(huffmanTree.encodingMap.containsKey((short) 256), "EOF should be included in the encoding map");
    }
}
