/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

import java.util.HashMap;
import java.util.Map;

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Stefan Perkovic
 */
public class TextCompressor {

    private static void compress() {
        String text = BinaryStdIn.readString();
        int index = 0;
        TST tst = new TST();

        for (int i = 0; i < 256; i++){
            tst.insert("" + (char)i, i);
        }
        int code = 256;
        int maxCode = 4096;

        while (!text.isEmpty()){
            String prefix = tst.getLongestPrefix(text);
            int codeword = tst.lookup(prefix);

            BinaryStdOut.write(codeword, 12);

            int prefixLength = prefix.length();
            if (prefixLength < text.length() && code < maxCode){
                tst.insert(text.substring(0, prefixLength + 1), code++);
            }

            text = text.substring(prefixLength);

        }




        BinaryStdOut.write(256, 12);
        BinaryStdOut.close();
    }

    private static void expand() {

        // TODO: Complete the expand() method




        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
