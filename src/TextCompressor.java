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

    private final static int BIT_OUTPUT = 12;
    private final static int EOF = 256;
    private final static int MAX_CODE = 4096;

    // Compress input text and outputs codes corresponding to prefix's
    private static void compress() {
        String text = BinaryStdIn.readString();
        TST tst = new TST();

        // Initialize the TST with the base codes
        for (int i = 0; i < EOF; i++){
            tst.insert("" + (char)i, i);
        }
        int code = EOF + 1;

        int index = 0;

        // Process the input text one prefix at a time
        while (index < text.length()){
            // Find, retrieve, and write code for longest prefix
            String prefix = tst.getLongestPrefix(text, index);
            int codeword = tst.lookup(prefix);
            BinaryStdOut.write(codeword, BIT_OUTPUT);

            // If within bounds add a code the next unique substring
            int prefixLength = prefix.length();
            if (index + prefixLength < text.length() && code < MAX_CODE){
                tst.insert(prefix + text.charAt(index + prefixLength), code++);
            }

            index+= prefixLength;
        }

        BinaryStdOut.write(EOF, BIT_OUTPUT);
        BinaryStdOut.close();
    }

    // Expand compressed input returning text to original state
    private static void expand() {
        // Creates a reverse lookup table for codes to strings
        String[] codeTable = new String[MAX_CODE];

        // Initialize the table with single-character strings
        for (int i = 0; i < EOF; i++){
            codeTable[i] = "" + (char) i;
        }

        codeTable[EOF] = "";
        int highestCode = EOF + 1;

        // Outputs first string outside loop in order to be able to handle immediate edge case
        int currentCode = BinaryStdIn.readInt(BIT_OUTPUT);
        if (currentCode == EOF) {
            BinaryStdOut.close();
            return;
        }
        String currentString = codeTable[currentCode];
        BinaryStdOut.write(currentString);

        String newString = "";
        // Goes until the file ends and outputs each code's string
        while(currentCode != EOF){
            currentCode = BinaryStdIn.readInt(BIT_OUTPUT);

            if (currentCode < highestCode){
                newString = codeTable[currentCode];
            }
            // Edge-case where code is for current string being defined
            else if(currentCode == highestCode){
                newString = currentString + currentString.charAt(0);

            }

            BinaryStdOut.write(newString);

            // Add new string to table if there's room
            if (highestCode < MAX_CODE) {
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
