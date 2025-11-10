import java.util.*;

public class pass2 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ---------- Symbol Table Input ----------
        Map<Integer, ST> symbolTable = new HashMap<>();

        System.out.println("Enter number of symbols:");
        int n = sc.nextInt();
        sc.nextLine(); // consume newline

        for (int i = 1; i <= n; i++) {
            System.out.println("Enter symbol name and address (e.g., A 100):");
            String line = sc.nextLine().trim();
            String[] parts = line.split("\\s+");

            String name = parts[0];
            int addr = Integer.parseInt(parts[1]);

            symbolTable.put(i, new ST(i, name, addr));
        }

        // ---------- Intermediate Code Input ----------
        List<String> intermediate = new ArrayList<>();

        System.out.println("\nEnter Intermediate Code lines (type END to finish):");

        while (true) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break;
            if (line.length() == 0) continue;
            intermediate.add(line);
        }

        sc.close();

        // ---------- Generate Machine Code ----------
        System.out.println("\nMachine Code:");
        System.out.println("-------------");

        for (String line : intermediate) {

            line = line.replaceAll("\\s+", ""); // remove spaces
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

                switch (items[0]) {
                    case "AD":
                        opcodeType = "AD";
                        opcode = items[1];
                        break;

                    case "IS":
                        opcodeType = "IS";
                        opcode = items[1];
                        break;

                    case "DL":
                        opcodeType = "DL";
                        opcode = items[1];
                        break;

                    case "S":
                        int index = Integer.parseInt(items[1]);
                        if (symbolTable.containsKey(index)) {
                            symbolAddr = String.valueOf(symbolTable.get(index).address);
                        }
                        break;

                    case "C":
                        constant = items[1];
                        break;

                    default:
                        // For (1) or (2) — register
                        if (items[0].matches("\\d+")) {
                            reg = items[0];
                        }
                }
            }

            // ---- Generate Object Code ----
            if (opcodeType.equals("IS")) {
                if (reg.equals("")) reg = "0";
                if (symbolAddr.equals("")) symbolAddr = "000";

                System.out.println(opcode + " " + reg + " " + symbolAddr);
            }
            else if (opcodeType.equals("DL")) {
                System.out.println("00 0 " + constant);
            }
            // AD (Assembler Directive) generates no code
        }

        // ---------- Print Symbol Table ----------
        System.out.println("\nSymbol Table:");
        System.out.println("Index  Name  Address");
        for (ST s : symbolTable.values()) {
            System.out.println(s.index + "      " + s.name + "      " + s.address);
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
