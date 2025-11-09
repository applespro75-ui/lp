import java.util.*;

public class pass1 {

    // MOT (Machine Opcode Table) for IS, AD, DL
    static Map<String, String> IS = new HashMap<>();
    static Map<String, String> AD = new HashMap<>();
    static Map<String, String> DL = new HashMap<>();
    static Map<String, String> REG = new HashMap<>();

    // SYMTAB and LITTAB
    static List<String> symtab = new ArrayList<>();
    static Map<String, Integer> symtabAddr = new HashMap<>();

    static List<String> littab = new ArrayList<>();
    static Map<String, Integer> littabAddr = new HashMap<>();

    // IC
    static List<String> intermediate = new ArrayList<>();

    public static void main(String[] args) {

        // Initialize MOT
        IS.put("ADD", "01");
        IS.put("SUB", "02");
        IS.put("MULT", "03");
        IS.put("MOVER", "04");
        IS.put("MOVEM", "05");
        IS.put("COMP", "06");
        IS.put("BC", "07");
        IS.put("DIV", "08");
        IS.put("READ", "09");
        IS.put("PRINT", "10");

        AD.put("START", "01");
        AD.put("END", "02");

        DL.put("DS", "01");
        DL.put("DC", "02");

        REG.put("AREG", "1");
        REG.put("BREG", "2");
        REG.put("CREG", "3");
        REG.put("DREG", "4");

        // Example input program
        String[] program = {
            "START 200",
            "MOVER AREG, ='5'",
            "ADD BREG, ONE",
            "MOVEM AREG, TEMP",
            "ONE DC 1",
            "TEMP DS 1",
            "END"
        };

        pass1(program);

        // Print SYMTAB
        System.out.println("\nSYMTAB:");
        for (int i = 0; i < symtab.size(); i++) {
            String sym = symtab.get(i);
            System.out.println(i + "  " + sym + "  " + symtabAddr.get(sym));
        }

        // Print LITTAB
        System.out.println("\nLITTAB:");
        for (int i = 0; i < littab.size(); i++) {
            String lit = littab.get(i);
            System.out.println(i + "  " + lit + "  " + littabAddr.get(lit));
        }

        // Print Intermediate Code
        System.out.println("\nIntermediate Code:");
        for (String line : intermediate) {
            System.out.println(line);
        }
    }

    static void pass1(String[] program) {
        int lc = 0; // location counter
        String value;

        for (String line : program) {
            String[] parts = line.trim().split("[ ,]+"); // split by space or comma

            if (AD.containsKey(parts[0])) {
                // START / END
                if (parts[0].equals("START")) {
                    lc = Integer.parseInt(parts[1]);
                    intermediate.add("AD " + AD.get("START") + " C " + lc);
                } else if (parts[0].equals("END")) {
                    intermediate.add("AD " + AD.get("END"));

                    // Assign addresses to literals
                    for (String lit : littab) {
                        littabAddr.put(lit, lc);
                        value = lit.substring(2, lit.length() - 1); // extract numeric value from ='x'
                        intermediate.add("DL " + DL.get("DC") + " C " + value);
                        lc++;
                    }
                }
            }
            else if (IS.containsKey(parts[0])) {
                // Instruction without label
                String ic = "IS " + IS.get(parts[0]);

                if (parts.length > 1 && REG.containsKey(parts[1])) {
                    ic += " RG " + REG.get(parts[1]);
                }

                if (parts.length > 2) {
                    if (parts[2].startsWith("=")) {
                        // literal
                        if (!littab.contains(parts[2])) littab.add(parts[2]);
                        int idx = littab.indexOf(parts[2]);
                        ic += " L " + idx;
                    } else {
                        // symbol
                        if (!symtab.contains(parts[2])) symtab.add(parts[2]);
                        int idx = symtab.indexOf(parts[2]);
                        ic += " S " + idx;
                    }
                }

                intermediate.add(ic);
                lc++;
            }
            else {
                // Label + DL (DS/DC)
                String label = parts[0];
                if (!symtab.contains(label)) symtab.add(label);
                symtabAddr.put(label, lc);

                if (DL.containsKey(parts[1])) {
                    if (parts[1].equals("DS")) {
                        intermediate.add("DL " + DL.get("DS") + " C " + parts[2]);
                        lc += Integer.parseInt(parts[2]);
                    } else if (parts[1].equals("DC")) {
                        intermediate.add("DL " + DL.get("DC") + " C " + parts[2]);
                        lc++;
                    }
                }
            }
        }
    }
}
