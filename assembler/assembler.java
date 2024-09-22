package assembler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Assembler {

    // Function that converts octal numbers to their binary representation (requires octal-to-dec, then decimal-to-binary)
    private static String octalToBinary(String octal) {
        int decimal = Integer.parseInt(octal, 8); // Convert octal to decimal
        return Integer.toBinaryString(decimal); // Convert decimal to binary
    }

    // Function that converts octal numbers to their binary representation (requires octal-to-dec, then decimal-to-binary)
    private static String decimalToOctal(String dec) {
        int decimal = Integer.parseInt(dec); // Convert string decimal into integer
        return Integer.toOctalString(decimal); // Convert decimal to octal
    }

    // This function allows us to increment the current location of memory
    private static void incrementCurrentLocation() {
        int decimalLocation = Integer.parseInt(currentLocation, 8); // Convert octal to decimal
        decimalLocation++; // Increment the location
        currentLocation = Integer.toOctalString(decimalLocation); // Convert back to octal
        currentLocation = String.format("%6s", currentLocation).replace(
            ' ',
            '0'
        ); // Pad with zeros if necessary
    }

    public static String currentLocation = ""; // Tracks the current memory location

    public static void main(String[] args) {
        // Create the variables that hold our input and output file locations
        String inputFilename = "input.txt";
        String outputFilename = "output.txt";
        List<String> lines = new ArrayList<>(); // To store lines to be written to the file

        try (
            Scanner scanner = new Scanner(new File(inputFilename));
            PrintWriter writer = new PrintWriter(new File(outputFilename))
        ) {
            while (scanner.hasNextLine()) {
                // Step #1: Grab the current line
                String line = scanner.nextLine();

                // Step #2: Ensure that we remove any comments from the lines that we scan
                String noComment = line.split(";")[0].trim();

                // Step #3: Split at whitespace
                String[] words = noComment.split("\\s+");

                // Step #4: Check each word, if it contains a comma, split it further
                List<String> finalWords = new ArrayList<>();
                for (String word : words) {
                    if (word.contains(",")) {
                        // If the word contains a comma, split it and add all parts
                        finalWords.addAll(Arrays.asList(word.split(",")));
                    } else {
                        // If no comma, just add the word
                        finalWords.add(word);
                    }
                }

                // Convert the list back to an array if needed
                String[] finalArray = finalWords.toArray(new String[0]);

                // Handling different types of lines: LOC, DATA, or instruction
                if (finalArray.length > 0) {
                    String opcode = finalArray[0].toUpperCase(); // Convert to uppercase to match keys in the map

                    // Build the output string for each line
                    String outputLine = "";

                    // If the OpCode is LOC, then we handle it here
                    // The L column receives the arg passed along with LOC
                    if (opcode.equals("LOC")) {
                        currentLocation = String.format(
                            "%6s",
                            decimalToOctal(finalArray[1])
                        ).replace(' ', '0');
                        outputLine = currentLocation + "  000000";
                    }
                    // Else if the OpCode is anything else...
                    else {
                        // If the OpCode is DATA, determine which case is occurring
                        // If the arg is "End", then 1024 is the data passed in, else it's the data passed in as an arg
                        if (opcode.equals("DATA")) {
                            String dataValue = (finalArray[1].toUpperCase()
                                        .equals("END"))
                                ? "1024"
                                : finalArray[1];
                            dataValue = String.format(
                                "%6s",
                                decimalToOctal(dataValue)
                            ).replace(' ', '0');
                            outputLine = currentLocation + "  " + dataValue;
                        }
                        // Handle normal instruction lines...
                        else if (
                            InstructionsMap.opcodeToOctalMap.containsKey(opcode)
                        ) {
                            String opcodeBinary = octalToBinary(
                                InstructionsMap.opcodeToOctalMap.get(opcode)
                            );
                            InstructionsMap.InstructionMetadata metadata =
                                InstructionsMap.metadataMap.get(opcode);

                            // Assuming the metadata provides info on how many args to expect and their order
                            StringBuilder instructionBinary = new StringBuilder(
                                opcodeBinary
                            );

                            // Assuming 'instruction' is a String containing the mnemonic of the instruction, e.g., "SRC" or "RRC".
                            if (opcode.equals("SRC") || opcode.equals("RRC")) {
                                // Special handling for SRC and RRC
                                String registerBinary = Integer.toBinaryString(
                                    Integer.parseInt(finalArray[1])
                                );
                                registerBinary = String.format(
                                    "%2s",
                                    registerBinary
                                ).replace(' ', '0');

                                String countBinary = Integer.toBinaryString(
                                    Integer.parseInt(finalArray[2])
                                );
                                countBinary = String.format(
                                    "%4s",
                                    countBinary
                                ).replace(' ', '0');

                                String lrBinary = Integer.toBinaryString(
                                    Integer.parseInt(finalArray[3])
                                );
                                lrBinary = String.format(
                                    "%1s",
                                    lrBinary
                                ).replace(' ', '0');

                                String alBinary = Integer.toBinaryString(
                                    Integer.parseInt(finalArray[4])
                                );
                                alBinary = String.format(
                                    "%1s",
                                    alBinary
                                ).replace(' ', '0');

                                // Ignored bits
                                String ignoredBinary = "00";

                                // Assemble the binary string in the correct order
                                instructionBinary
                                    .append(registerBinary)
                                    .append(lrBinary)
                                    .append(alBinary)
                                    .append(ignoredBinary)
                                    .append(countBinary);
                            } else {
                                // Process each argument based on its defined bit length
                                // Find out the number of required arguments
                                int requiredArgsCount = 0;
                                for (InstructionsMap.ArgumentDefinition argDef : metadata.getArgumentDefinitions()) {
                                    if (
                                        argDef.type ==
                                        InstructionsMap.ArgumentType.REQUIRED
                                    ) {
                                        requiredArgsCount++;
                                    }
                                }

                                int argIndex = 1; // Start after the opcode
                                for (InstructionsMap.ArgumentDefinition argDef : metadata.getArgumentDefinitions()) {
                                    switch (argDef.type) {
                                        case REQUIRED:
                                            if (argIndex < finalArray.length) {
                                                String argBinary =
                                                    Integer.toBinaryString(
                                                        Integer.parseInt(
                                                            finalArray[argIndex]
                                                        )
                                                    );
                                                argBinary = String.format(
                                                    "%" + argDef.bits + "s",
                                                    argBinary
                                                ).replace(' ', '0');
                                                instructionBinary.append(
                                                    argBinary
                                                );
                                                argIndex++;
                                            } else {
                                                // Handle the error - required argument is missing
                                            }
                                            break;
                                        case OPTIONAL:
                                            // Check if the optional argument is actually provided
                                            if (
                                                finalArray.length - 1 >
                                                requiredArgsCount
                                            ) {
                                                // The optional argument is the last one in the finalArray
                                                String argBinary =
                                                    Integer.toBinaryString(
                                                        Integer.parseInt(
                                                            finalArray[finalArray.length -
                                                                1]
                                                        )
                                                    );
                                                argBinary = String.format(
                                                    "%" + argDef.bits + "s",
                                                    argBinary
                                                ).replace(' ', '0');
                                                instructionBinary.append(
                                                    argBinary
                                                );
                                            } else {
                                                instructionBinary.append(
                                                    "0".repeat(argDef.bits)
                                                );
                                            }
                                            break;
                                        case IGNORED:
                                            instructionBinary.append(
                                                "0".repeat(argDef.bits)
                                            );
                                            break;
                                    }
                                }
                            }
                            // Convert the full binary instruction to octal, pad it, and add to lines
                            int instructionDecimal = Integer.parseInt(
                                instructionBinary.toString(),
                                2
                            );
                            String instructionOctal = Integer.toOctalString(
                                instructionDecimal
                            );
                            instructionOctal = String.format(
                                "%6s",
                                instructionOctal
                            ).replace(' ', '0');
                            outputLine = currentLocation +
                            "  " +
                            instructionOctal;
                        } else {
                            System.err.println("Unknown opcode: " + opcode);
                        }

                        incrementCurrentLocation();
                    }
                    // Add the output line, that we've been ^ building, to our list of lines to output, later
                    lines.add(outputLine);
                    System.out.println(outputLine); // send output to terminal, as well
                }
            }
            // After the loop, write all lines to the file
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
