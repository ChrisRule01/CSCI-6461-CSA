package assembler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Assembler {

    // Converts octal to binary
    private static String octalToBinary(String octal) {
        int decimal = Integer.parseInt(octal, 8);
        return Integer.toBinaryString(decimal);
    }

    // Converts decimal to octal
    private static String decimalToOctal(String dec) {
        int decimal = Integer.parseInt(dec);
        return Integer.toOctalString(decimal);
    }

    // Increments the current memory location
    private static void incrementCurrentLocation() {
        int decimalLocation = Integer.parseInt(currentLocation, 8);
        decimalLocation++;
        currentLocation = Integer.toOctalString(decimalLocation);
        currentLocation = String.format("%6s", currentLocation).replace(
            ' ',
            '0'
        );
    }

    // Appends a binary argument to the instruction
    private static void appendBinaryArgument(
        StringBuilder instructionBinary,
        String arg,
        int bits
    ) {
        String argBinary = Integer.toBinaryString(Integer.parseInt(arg));
        argBinary = String.format("%" + bits + "s", argBinary).replace(
            ' ',
            '0'
        );
        instructionBinary.append(argBinary);
    }

    public static String currentLocation = ""; // Current memory location

    public static void main(String[] args) {
        // Input and output file paths
        String inputFile = "input.txt";
        String outputFile = "output.txt";
        String listingFile = "listing.txt";
        List<String> lines = new ArrayList<>(); // Lines to write to the output file
        List<String> listingLines = new ArrayList<>(); // Lines for the listing file

        try (
            Scanner scanner = new Scanner(new File(inputFile));
            PrintWriter writer = new PrintWriter(new File(outputFile));
            PrintWriter listingWriter = new PrintWriter(new File(listingFile))
        ) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine(); // Read a line
                String noComment = line.split(";")[0].trim(); // Remove comments
                String[] words = noComment.split("\\s+"); // Split by whitespace

                List<String> finalWords = new ArrayList<>();
                for (String word : words) {
                    if (word.contains(",")) {
                        finalWords.addAll(Arrays.asList(word.split(","))); // Split by comma
                    } else {
                        finalWords.add(word); // Add word as is
                    }
                }

                String[] finalArray = finalWords.toArray(new String[0]); // Convert to array

                if (finalArray.length > 0) {
                    String opcode = finalArray[0].toUpperCase(); // Normalize opcode

                    String outputLine = ""; // Prepare output line

                    if (opcode.equals("LOC")) {
                        currentLocation = String.format(
                            "%6s",
                            decimalToOctal(finalArray[1])
                        ).replace(' ', '0');
                        outputLine = currentLocation + "  000000"; // Format for LOC
                    } else {
                        if (opcode.equals("DATA")) {
                            String dataValue = finalArray[1].toUpperCase()
                                    .equals("END")
                                ? "1024"
                                : finalArray[1];
                            dataValue = String.format(
                                "%6s",
                                decimalToOctal(dataValue)
                            ).replace(' ', '0');
                            outputLine = currentLocation + "  " + dataValue; // Format for DATA
                        } else if (
                            InstructionsMap.opcodeToOctalMap.containsKey(opcode)
                        ) {
                            String opcodeBinary = octalToBinary(
                                InstructionsMap.opcodeToOctalMap.get(opcode)
                            );
                            InstructionsMap.InstructionMetadata metadata =
                                InstructionsMap.metadataMap.get(opcode);
                            StringBuilder instructionBinary = new StringBuilder(
                                opcodeBinary
                            );

                            if (opcode.equals("SRC") || opcode.equals("RRC")) {
                                // Handle special SRC and RRC
                                String registerBinary = String.format(
                                    "%2s",
                                    Integer.toBinaryString(
                                        Integer.parseInt(finalArray[1])
                                    )
                                ).replace(' ', '0');
                                String countBinary = String.format(
                                    "%4s",
                                    Integer.toBinaryString(
                                        Integer.parseInt(finalArray[2])
                                    )
                                ).replace(' ', '0');
                                String lrBinary = String.format(
                                    "%1s",
                                    Integer.toBinaryString(
                                        Integer.parseInt(finalArray[3])
                                    )
                                ).replace(' ', '0');
                                String alBinary = String.format(
                                    "%1s",
                                    Integer.toBinaryString(
                                        Integer.parseInt(finalArray[4])
                                    )
                                ).replace(' ', '0');
                                String ignoredBinary = "00";

                                instructionBinary
                                    .append(registerBinary)
                                    .append(lrBinary)
                                    .append(alBinary)
                                    .append(ignoredBinary)
                                    .append(countBinary);
                            } else {
                                // Process standard instruction arguments
                                int requiredArgsCount = 0;
                                for (InstructionsMap.ArgumentDefinition argDef : metadata.getArgumentDefinitions()) {
                                    if (
                                        argDef.type ==
                                        InstructionsMap.ArgumentType.REQUIRED
                                    ) {
                                        requiredArgsCount++;
                                    }
                                }

                                int argIndex = 1; // Start after opcode
                                for (InstructionsMap.ArgumentDefinition argDef : metadata.getArgumentDefinitions()) {
                                    switch (argDef.type) {
                                        case REQUIRED:
                                            if (argIndex < finalArray.length) {
                                                appendBinaryArgument(
                                                    instructionBinary,
                                                    finalArray[argIndex],
                                                    argDef.bits
                                                );
                                                argIndex++;
                                            } else {
                                                // Error: missing required argument
                                            }
                                            break;
                                        case OPTIONAL:
                                            if (
                                                finalArray.length - 1 >
                                                requiredArgsCount
                                            ) {
                                                appendBinaryArgument(
                                                    instructionBinary,
                                                    finalArray[finalArray.length -
                                                        1],
                                                    argDef.bits
                                                );
                                            } else {
                                                instructionBinary.append(
                                                    "0".repeat(argDef.bits)
                                                ); // No optional argument provided
                                            }
                                            break;
                                        case IGNORED:
                                            instructionBinary.append(
                                                "0".repeat(argDef.bits)
                                            ); // Ignore this argument
                                            break;
                                    }
                                }
                            }

                            // Convert instruction binary to octal and format
                            int instructionDecimal = Integer.parseInt(
                                instructionBinary.toString(),
                                2
                            );
                            String instructionOctal = String.format(
                                "%6s",
                                Integer.toOctalString(instructionDecimal)
                            ).replace(' ', '0');
                            outputLine = currentLocation +
                            "  " +
                            instructionOctal; // Final output line
                        } else {
                            System.err.println("Unknown opcode: " + opcode); // Handle unknown opcode
                        }

                        incrementCurrentLocation(); // Move to next location
                    }

                    lines.add(outputLine); // Store output line
                    listingLines.add(outputLine + "    " + line); // Store output and input on the same line
                    System.out.println(outputLine); // Output to terminal
                }
            }
            // Write all output lines to the output file
            for (String line : lines) {
                writer.println(line);
            }
            // Write input-output mappings to the listing file
            for (String listingLine : listingLines) {
                listingWriter.println(listingLine);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle file I/O errors
        }
    }
}
