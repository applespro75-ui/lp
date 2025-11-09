import java.util.*;

public class pass2 {

    public static void main(String[] args) {
        // ---------- Symbol Table (from problem statement) ----------
        Map<Integer, ST> symbolTable = new HashMap<>();
        symbolTable.put(1, new ST(1, "A", 100));
        symbolTable.put(2, new ST(2, "B", 101));
        symbolTable.put(3, new ST(3, "C", 102));

        // ---------- Intermediate Code (tuple-based format) ----------
        String[] intermediateCode = {
            "(AD,01)(C,100)",
            "(IS,04)(1)(S,1)",
            "(IS,01)(2)(S,2)",
            "(IS,02)(1)(S,3)",
            "(AD,02)"
        };

        System.out.println("Machine Code:");
        System.out.println("------------");

        for (String line : intermediateCode) {
            line = line.replaceAll("\\s+", ""); // remove spaces

            // Extract all tuples like (X,Y)
            String[] parts = line.split("\\)");

            String opcodeType = "";
            String opcode = "";
            String reg = "";
            String symbolAddr = "";
            String constant = "";

            for (String part : parts) {
                part = part.replace("(", "").trim();
                if (part.isEmpty()) continue;
                String[] items = part.split(",");

                if (items[0].equals("AD")) {
                    // Assembler Directive — no machine code
                    opcodeType = "AD";
                    opcode = items[1];
                } 
                else if (items[0].equals("IS")) {
                    opcodeType = "IS";
                    opcode = items[1];
                } 
                else if (items[0].equals("DL")) {
                    opcodeType = "DL";
                    opcode = items[1];
                } 
                else if (items[0].equals("RG") || items[0].matches("\\d")) {
                    // Register like (1) or (2)
                    reg = items[0];
                } 
                else if (items[0].equals("S")) {
                    // Symbol like (S,1)
                    int symIndex = Integer.parseInt(items[1]);
                    if (symbolTable.containsKey(symIndex)) {
                        symbolAddr = String.valueOf(symbolTable.get(symIndex).address);
                    }
                } 
                else if (items[0].equals("C")) {
                    // Constant
                    constant = items[1];
                }
            }

            // ---- Generate Object Code ----
            if (opcodeType.equals("IS")) {
                // Format: <opcode> <register> <memory>
                if (reg.equals("")) reg = "0";
                if (symbolAddr.equals("")) symbolAddr = "000";
                System.out.println(opcode + " " + reg + " " + symbolAddr);
            } 
            else if (opcodeType.equals("DL")) {
                // For DL statements (not in this input)
                System.out.println("00 0 " + constant);
            }
            else if (opcodeType.equals("AD")) {
                // Assembler directive → no object code
                continue;
            }
        }

        // ---------- Print Symbol Table ----------
        System.out.println("\nSymbol Table:");
        for (ST s : symbolTable.values()) {
            System.out.println(s.index + "\t" + s.name + "\t" + s.address);
        }
    }

    // ---- Symbol Table Entry ----
    private static class ST {
        int index;
        String name;
        int address;

        ST(int index, String name, int address) {
            this.index = index;
            this.name = name;
            this.address = address;
        }
    }
}
