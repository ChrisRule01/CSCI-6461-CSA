// Written by Preston Byk
//
//
//      ** INSTRUCTIONS **
// 1. to complile the assembler go to the root and run the following command
//      javac assembler/assembler.java
// 2. then run the following line:
//          java assembler/assembler assembler/inputTest.txt
//          ("assembler/inputTest.txt" is the file being passed into the program)
//
//  note: I think it has to be done at the root so that the "inputTest.txt" gets included.
//        I think it packages the whole folder. If i try doing it inside the folder it does not work
//        More  testing on this should be done. Also I would like to automate this with a script

package assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class assembler {

    public static void main(String[] args) {
        // Check if the filename argument is provided
        if (args.length != 1) {
            System.out.println("Usage: java FilePrinter <filename>");
            return;
        }

        String filename = args[0];

        // Try-with-resources to ensure resources are closed
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read and print each line of the file
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
