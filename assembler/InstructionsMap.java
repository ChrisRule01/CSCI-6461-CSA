package assembler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionsMap {

    // Maps for storing each data for each instruction
    public static final Map<String, InstructionMetadata> metadataMap =
        new HashMap<>();
    public static final Map<String, String> opcodeToOctalMap = new HashMap<>();

    // ArgumentType Enum
    public enum ArgumentType {
        REQUIRED,
        OPTIONAL,
        IGNORED,
    }

    // InstructionMetadata class
    static class InstructionMetadata {

        private final List<ArgumentDefinition> argumentDefinitions;

        public InstructionMetadata(
            List<ArgumentDefinition> argumentDefinitions
        ) {
            this.argumentDefinitions = argumentDefinitions;
        }

        public List<ArgumentDefinition> getArgumentDefinitions() {
            return argumentDefinitions;
        }
    }

    // ArgumentDefinition class
    static class ArgumentDefinition {

        int bits;
        ArgumentType type;

        public ArgumentDefinition(int bits, ArgumentType type) {
            this.bits = bits;
            this.type = type;
        }
    }

    // Instruction Metadata Entries
    static String[][] instructions = {
        { "HLT", "10:IGNORED" },
        { "TRAP", "5:IGNORED", "5:REQUIRED" },
        { "LDR", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "STR", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "LDA", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "LDX", "2:IGNORED", "2:REQUIRED", "1:IGNORED", "5:REQUIRED" },
        { "STX", "2:IGNORED", "2:REQUIRED", "1:IGNORED", "5:REQUIRED" },
        { "SETCCE", "2:REQUIRED", "8:IGNORED" },
        { "JZ", "2:IGNORED", "2:REQUIRED", "1:IGNORED", "5:REQUIRED" },
        { "JNE", "2:OPTIONAL", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "JCC", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "JMA", "2:IGNORED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "JSR", "2:IGNORED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "RFS", "5:IGNORED", "5:REQUIRED" },
        { "SOB", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "JGE", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "AMR", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "SMR", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "AIR", "2:REQUIRED", "3:IGNORED", "5:REQUIRED" },
        { "SIR", "2:REQUIRED", "3:IGNORED", "5:REQUIRED" },
        { "MLT", "2:REQUIRED", "2:REQUIRED", "6:IGNORED" },
        { "DVD", "2:REQUIRED", "2:REQUIRED", "6:IGNORED" },
        { "TRR", "2:REQUIRED", "2:REQUIRED", "6:IGNORED" },
        { "AND", "2:REQUIRED", "2:REQUIRED", "6:IGNORED" },
        { "ORR", "2:REQUIRED", "2:REQUIRED", "6:IGNORED" },
        { "NOT", "2:REQUIRED", "8:IGNORED" },
        {
            "SRC",
            "2:REQUIRED",
            "1:REQUIRED",
            "1:REQUIRED",
            "2:IGNORED",
            "4:REQUIRED",
        },
        {
            "RRC",
            "2:REQUIRED",
            "1:REQUIRED",
            "1:REQUIRED",
            "2:IGNORED",
            "4:REQUIRED",
        },
        { "IN", "2:REQUIRED", "3:IGNORED", "5:REQUIRED" },
        { "OUT", "2:REQUIRED", "3:IGNORED", "5:REQUIRED" },
        { "CHK", "2:REQUIRED", "3:IGNORED", "5:REQUIRED" },
        { "FADD", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "FSUB", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "VADD", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "VSUB", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "CNVRT", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "LDFR", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
        { "STFR", "2:REQUIRED", "2:REQUIRED", "1:OPTIONAL", "5:REQUIRED" },
    };

    // Populate metadataMap using addMetadata
    static {
        for (String[] instruction : instructions) {
            String name = instruction[0];
            List<ArgumentDefinition> args = new ArrayList<>();
            for (int i = 1; i < instruction.length; i++) {
                String[] parts = instruction[i].split(":");
                int size = Integer.parseInt(parts[0]);
                ArgumentType type = ArgumentType.valueOf(
                    parts[1].toUpperCase()
                );
                args.add(new ArgumentDefinition(size, type));
            }
            addMetadata(name, args.toArray(new ArgumentDefinition[0]));
        }
    }

    // Define OpCode mappings in a two-dimensional array
    static String[][] opcodes = {
        { "LOC", "77" },
        { "DATA", "77" },
        { "HLT", "0" },
        { "TRAP", "45" },
        { "LDR", "1" },
        { "STR", "2" },
        { "LDA", "3" },
        { "LDX", "4" },
        { "STX", "5" },
        { "SETCCE", "44" },
        { "JZ", "6" },
        { "JNE", "7" },
        { "JCC", "10" },
        { "JMA", "11" },
        { "JSR", "12" },
        { "RFS", "13" },
        { "SOB", "14" },
        { "JGE", "15" },
        { "AMR", "16" },
        { "SMR", "17" },
        { "AIR", "20" },
        { "SIR", "21" },
        { "MLT", "22" },
        { "DVD", "23" },
        { "TRR", "24" },
        { "AND", "25" },
        { "ORR", "26" },
        { "NOT", "27" },
        { "SRC", "30" },
        { "RRC", "31" },
        { "IN", "32" },
        { "OUT", "33" },
        { "CHK", "34" },
        { "FADD", "35" },
        { "FSUB", "36" },
        { "VADD", "37" },
        { "VSUB", "40" },
        { "CNVRT", "41" },
        { "LDFR", "42" },
        { "STFR", "43" },
    };

    // Populate the opcodeToOctalMap using addOpcode
    static {
        for (String[] opcode : opcodes) {
            addOpcode(opcode[0], opcode[1]);
        }
    }

    // Add Metadata method
    private static void addMetadata(
        String instruction,
        ArgumentDefinition... args
    ) {
        metadataMap.put(
            instruction,
            new InstructionMetadata(Arrays.asList(args))
        );
    }

    // Add Opcode method
    private static void addOpcode(String instruction, String octal) {
        opcodeToOctalMap.put(instruction, octal);
    }
}
