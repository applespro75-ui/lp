import java.util.*;

public class pass1 {
    // Opcode tables
    static Map<String, String> IS = new HashMap<>();
    static Map<String, String> AD = new HashMap<>();
    static Map<String, String> DL = new HashMap<>();
    static Map<String, String> REG = new HashMap<>();

    // Symbol and literal tables
    static List<String> symtab = new ArrayList<>();
    static Map<String, Integer> symtabAddr = new HashMap<>();

    static List<String> littab = new ArrayList<>();
    static Map<String, Integer> littabAddr = new HashMap<>();

    // Intermediate code list
    static List<String> intermediate = new ArrayList<>();

    public static void main(String[] args) {
        initializeTables();

        Scanner sc = new Scanner(System.in);
        List<String> programLines = new ArrayList<>();

        System.out.println("Enter Assembly Code:");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            programLines.add(line);
            if (line.contains("END")) break;
        }
        sc.close();

        pass1(programLines);

        // --- Print Results ---
        System.out.println("\nSYMBOL TABLE:");
        for (int i = 0; i < symtab.size(); i++) {
            String s = symtab.get(i);
            System.out.println(i + "  " + s + "  " + symtabAddr.get(s));
        }

        System.out.println("\nLITERAL TABLE:");
        for (int i = 0; i < littab.size(); i++) {
            String l = littab.get(i);
            System.out.println(i + "  " + l + "  " + littabAddr.get(l));
        }

        System.out.println("\nINTERMEDIATE CODE:");
        for (String line : intermediate) {
            System.out.println(line);
        }
    }

    static void initializeTables() {
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
    }

    static void pass1(List<String> program) {
        int lc = 0; // Location Counter

        for (String line : program) {
            String[] parts = line.trim().split("[ ,]+");
            if (parts.length == 0) continue;

            // --- START ---
            if (parts[0].equals("START")) {
                lc = Integer.parseInt(parts[1]);
                intermediate.add("(AD,01) (C," + lc + ")");
            }

            // --- END ---
            else if (parts[0].equals("END")) {
                intermediate.add("(AD,02)");
                // Assign literal addresses after END
                int addr = lc;
                for (String lit : littab) {
                    littabAddr.put(lit, addr++);
                }
            }

            // --- Instruction without label ---
            else if (IS.containsKey(parts[0])) {
                String ic = "(IS," + IS.get(parts[0]) + ")";
                int i = 1;

                // Register
                if (i < parts.length && REG.containsKey(parts[i])) {
                    ic += " (R," + REG.get(parts[i]) + ")";
                    i++;
                }

                // Operand (symbol or literal)
                if (i < parts.length) {
                    String operand = parts[i];
                    if (operand.startsWith("=")) {
                        // literal
                        if (!littab.contains(operand)) littab.add(operand);
                        int idx = littab.indexOf(operand);
                        ic += " (L," + idx + ")";
                    } else {
                        // symbol
                        if (!symtab.contains(operand)) symtab.add(operand);
                        int idx = symtab.indexOf(operand);
                        ic += " (S," + idx + ")";
                    }
                }

                intermediate.add(ic);
                lc++;
            }

            // --- Label present ---
            else {
                String label = parts[0];
                if (!symtab.contains(label)) {
                    symtab.add(label);
                }
                // Always update address of the label
                symtabAddr.put(label, lc);

                if (DL.containsKey(parts[1])) {
                    if (parts[1].equals("DS")) {
                        intermediate.add("(DL,01) (C," + parts[2] + ")");
                        lc += Integer.parseInt(parts[2]);
                    } else if (parts[1].equals("DC")) {
                        intermediate.add("(DL,02) (C," + parts[2] + ")");
                        lc++;
                    }
                } else if (IS.containsKey(parts[1])) {
                    // label + instruction (rare in this PS)
                    String ic = "(IS," + IS.get(parts[1]) + ")";
                    int i = 2;

                    if (i < parts.length && REG.containsKey(parts[i])) {
                        ic += " (R," + REG.get(parts[i]) + ")";
                        i++;
                    }

                    if (i < parts.length) {
                        String operand = parts[i];
                        if (operand.startsWith("=")) {
                            if (!littab.contains(operand)) littab.add(operand);
                            int idx = littab.indexOf(operand);
                            ic += " (L," + idx + ")";
                        } else {
                            if (!symtab.contains(operand)) symtab.add(operand);
                            int idx = symtab.indexOf(operand);
                            ic += " (S," + idx + ")";
                        }
                    }

                    intermediate.add(ic);
                    lc++;
                }
            }
        }
    }
}
