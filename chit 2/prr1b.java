import java.util.*;

class prr1b {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Step 1: Symbol Table Input
        System.out.print("Enter number of symbols: ");
        int n = sc.nextInt();
        String[] symbols = new String[n];
        int[] addresses = new int[n];

        System.out.println("Enter symbols and their addresses:");
        for (int i = 0; i < n; i++) {
            symbols[i] = sc.next();
            addresses[i] = sc.nextInt();
        }

        // Step 2: Intermediate Code Input
        System.out.print("Enter number of intermediate code lines: ");
        int m = sc.nextInt();
        sc.nextLine();
        String[] IC = new String[m];
        System.out.println("Enter intermediate code lines:");
        for (int i = 0; i < m; i++) {
            IC[i] = sc.nextLine().trim();
        }

        System.out.println("\n--- Object Code ---");

        // Step 3: Process each line
        for (String line : IC) {
            if (line.contains("AD")) continue; // skip directives

            String[] parts = line.replace(")", "").split("\\(");
            // parts[1] = IS,xx | parts[2] = reg | parts[3] = S,index or C,value

            String opcode = parts[1].split(",")[1];
            String reg = "0", addr = "000";

            if (parts.length > 2)
                reg = parts[2];

            if (parts.length > 3) {
                String[] operand = parts[3].split(",");
                if (operand[0].equals("S")) {
                    int symIndex = Integer.parseInt(operand[1]) - 1;
                    addr = String.valueOf(addresses[symIndex]);
                } else if (operand[0].equals("C")) {
                    addr = operand[1];
                }
            }

            System.out.println(opcode + " " + reg + " " + addr);
        }

        sc.close();
    }
}
