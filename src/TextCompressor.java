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


    // Compress input text and outputs codes corresponding to prefix's
    private static void compress() {
        String text = BinaryStdIn.readString();
        TST tst = new TST();

        // Initialize the TST with the base codes
        for (int i = 0; i < 256; i++){
            tst.insert("" + (char)i, i);
        }
        int code = 257;
        int maxCode = 4096;

        int index = 0;

        // Process the input text one prefix at a time
        while (index < text.length()){
            // Find, retrieve, and write code for longest prefix
            String prefix = tst.getLongestPrefix(text, index);
            int codeword = tst.lookup(prefix);
            BinaryStdOut.write(codeword, 12);

            // If within bounds add a code the next unique substring
            int prefixLength = prefix.length();
            if (index + prefixLength < text.length() && code < maxCode){
                tst.insert(prefix + text.charAt(index + prefixLength), code++);
            }

            index+= prefixLength;
        }

        BinaryStdOut.write(256, 12);
        BinaryStdOut.close();
    }

    // Expand compressed input returning text to original state
    private static void expand() {
        // Creates a reverse lookup table for codes to strings
        String[] codeTable = new String[4096];

        // Initialize the table with single-character strings
        for (int i = 0; i < 256; i++){
            codeTable[i] = "" + (char) i;
        }

        codeTable[256] = "";
        int highestCode = 257;

        // Outputs first string outside loop in order to be able to handle immediate edge case
        int currentCode = BinaryStdIn.readInt(12);
        if (currentCode == 256) {
            BinaryStdOut.close();
            return;
        }
        String currentString = codeTable[currentCode];
        BinaryStdOut.write(currentString);

        String newString = "";
        // Goes until the file ends and outputs each code's string
        while(currentCode != 256){
            currentCode = BinaryStdIn.readInt(12);

            if (currentCode < highestCode){
                newString = codeTable[currentCode];
            }
            // Edge-case where code is for current string being defined
            else if(currentCode == highestCode){
                newString = currentString + currentString.charAt(0);

            }

            BinaryStdOut.write(newString);

            // Add new string to table if there's room
            if (highestCode < 4096) {
                codeTable[highestCode++] = currentString + newString.charAt(0);
            }

            currentString = newString;

        }

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
