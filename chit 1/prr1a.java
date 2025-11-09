import java.util.*;

public class prr1a{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, Integer> Symbol_table = new LinkedHashMap<>();
        List<String> Literal_list = new ArrayList<>();
        Map<String, Integer> Literal_table = new LinkedHashMap<>();
        List<Integer> Pool_table = new ArrayList<>();
        List<String> Intermediate_code = new ArrayList<>();

        int lc = 0; // location counter

        System.out.print("Enter number of lines: ");
        int n = sc.nextInt();
        sc.nextLine(); // consume newline

        System.out.println("Enter Assembly code line by line:");
        Pool_table.add(0); // First pool starts at index 0

        for (int i = 0; i < n; i++) {
            String line = sc.nextLine().trim();
            String[] parts = line.split("\\s+");

            // If label present
            if (parts.length == 3) {
                String label = parts[0];
                Symbol_table.put(label, lc);
            }

            // Detect literals
            for (String part : parts) {
                if (part.startsWith("='") && !Literal_list.contains(part)) {
                    Literal_list.add(part);
                    Literal_table.put(part, -1); // placeholder
                }
            }

            // Add line to intermediate code
            Intermediate_code.add(lc + " " + line);

            // Handle LTORG or END
            if (line.contains("LTORG") || line.contains("END")) {
                System.out.println("\nProcessing " + line);
                for (int j = Pool_table.get(Pool_table.size() - 1); j < Literal_list.size(); j++) {
                    String lit = Literal_list.get(j);
                    Literal_table.put(lit, lc);
                    lc++;
                }
                Pool_table.add(Literal_list.size());
            } else {
                lc++;
            }
        }

        // Print results
        System.out.println("\n--- Symbol Table ---");
        for (Map.Entry<String, Integer> e : Symbol_table.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }

        System.out.println("\n--- Literal Table ---");
        for (Map.Entry<String, Integer> e : Literal_table.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }

        System.out.println("\n--- Pool Table ---");
        for (int i = 0; i < Pool_table.size(); i++) {
            System.out.println("#" + (i + 1) + "\t" + Pool_table.get(i));
        }

        System.out.println("\n--- Intermediate Code ---");
        for (String s : Intermediate_code) {
            System.out.println(s);
        }

        sc.close();
    }
}