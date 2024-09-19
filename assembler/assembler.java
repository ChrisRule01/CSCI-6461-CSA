// Written by Preston Byk
//
//
//      ** INSTRUCTIONS **
// 1. use "chmod +x scripts.sh" to allow the scripts to run.
//
// 2. use "./scripts.sh" to see script options
//
// 3. use the format "./scripts.sh <option>" to use the scripts

package assembler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class assembler {

    public static void main(String[] args) {
        // Check if the filename argument is provided
        if (args.length != 1) {
            System.out.println("Usage: java FilePrinter <filename>");
            return;
        }

        String inputFile = args[0];
        String loadFile = "assembler/loadFile";

        Map<String, Integer> labels = new HashMap<>();
        int locationCounter = 0;
        int labelCounter = 0;
        try (
            BufferedReader reader = new BufferedReader(
                new FileReader(inputFile)
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.split("//")[0].trim(); // Remove comments

                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    if (parts[0].endsWith(":")) {
                        String label =
                            parts[0].substring(0, parts[0].length() - 1);
                        labelCounter = labelCounter + 1024;
                        labels.put(label, labelCounter);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Second pass: Generate the output file
        locationCounter = 0; // Reset location counter
        try (
            BufferedReader reader = new BufferedReader(
                new FileReader(inputFile)
            );
            BufferedWriter writer = new BufferedWriter(new FileWriter(loadFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.split("//")[0].trim(); // Remove comments

                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    if (parts[0].equals("LOC")) {
                        locationCounter = Integer.parseInt(parts[1], 16);
                    } else if (parts[0].equals("Data")) {
                        if (labels.containsKey(parts[1])) {
                            int value = labels.get(parts[1]);
                            writer.write(
                                String.format(
                                    "%04X %04X \n",
                                    locationCounter,
                                    value
                                )
                            );
                        } else {
                            int value = Integer.parseInt(parts[1], 10);
                            writer.write(
                                String.format(
                                    "%04X %04X \n",
                                    locationCounter,
                                    value
                                )
                            );
                        }
                        locationCounter++;
                    } else {
                        if (parts[0].endsWith(":")) {
                            String label =
                                parts[0].substring(0, parts[0].length() - 1);
                            int value = labels.get(label);
                            int opcodeValue = getOpcodeValue(parts[1]);
                            writer.write(
                                String.format(
                                    "%04X %04X \n",
                                    value,
                                    opcodeValue
                                )
                            );
                        } else {
                            String opcode = parts[0];
                            String[] operands = parts[1].split(",");
                            int opcodeValue = getOpcodeValue(opcode);

                            if (operands.length == 2) {
                                int r = 0;
                                int ix = Integer.parseInt(operands[0], 10);
                                int i = 0;
                                int mem = Integer.parseInt(operands[1], 10);
                                int hexInstruction =
                                    (opcodeValue << 10) |
                                    (r << 8) |
                                    (ix << 6) |
                                    (i << 5) |
                                    mem;
                                writer.write(
                                    String.format(
                                        "%04X %04X \n",
                                        locationCounter,
                                        hexInstruction
                                    )
                                );
                                locationCounter++;
                            } else if (operands.length == 3) {
                                int r = Integer.parseInt(operands[0], 10);
                                int ix = Integer.parseInt(operands[1], 10);
                                int i = 0;
                                int mem = Integer.parseInt(operands[2], 10);
                                int hexInstruction =
                                    (opcodeValue << 10) |
                                    (r << 8) |
                                    (ix << 6) |
                                    (i << 5) |
                                    mem;
                                writer.write(
                                    String.format(
                                        "%04X %04X \n",
                                        locationCounter,
                                        hexInstruction
                                    )
                                );
                                locationCounter++;
                            } else {
                                int r = Integer.parseInt(operands[0], 10);
                                int ix = Integer.parseInt(operands[1], 10);
                                int i = 1;
                                int mem = Integer.parseInt(operands[2], 10);
                                int hexInstruction =
                                    (opcodeValue << 10) |
                                    (r << 8) |
                                    (ix << 6) |
                                    (i << 5) |
                                    mem;
                                writer.write(
                                    String.format(
                                        "%04X %04X \n",
                                        locationCounter,
                                        hexInstruction
                                    )
                                );
                                locationCounter++;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getOpcodeValue(String opcode) {
        Map<String, Integer> opcodes = new HashMap<>();
        opcodes.put("LDX", 0x21);
        opcodes.put("LDR", 0x1);
        opcodes.put("LDA", 0x3);
        opcodes.put("JZ", 0x8);
        opcodes.put("HLT", 0x0);

        return opcodes.getOrDefault(opcode, 0);
    }
}
