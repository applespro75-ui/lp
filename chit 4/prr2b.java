import java.io.*;
import java.util.*;

public class prr2b {
    public static void main(String[] args) throws Exception {
        BufferedReader irb = new BufferedReader(new FileReader("intermediate.txt"));
        BufferedReader mdtb = new BufferedReader(new FileReader("mdt.txt"));
        BufferedReader mntb = new BufferedReader(new FileReader("mnt.txt"));
        FileWriter fr = new FileWriter("pass2.txt");

        Map<String, Integer> mnt = new HashMap<>();
        List<String> mdt = new ArrayList<>();
        Map<Integer, String> aptab = new HashMap<>();

        String line;

        // Read MNT (Macro Name and MDT pointer)
        while ((line = mntb.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 3) {
                String macroName = parts[0];
                int mdtPointer = Integer.parseInt(parts[2]);
                mnt.put(macroName, mdtPointer);
            }
        }

        // Read MDT
        while ((line = mdtb.readLine()) != null) {
            mdt.add(line.trim());
        }

        // Process intermediate code
        while ((line = irb.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 0 || line.trim().isEmpty()) continue;

            if (mnt.containsKey(parts[0])) {
                int mdtp = mnt.get(parts[0]) - 1; // MDT index (0-based)

                // Store actual argument
                if (parts.length > 1)
                    aptab.put(1, parts[1]); // only one positional parameter

                // Expand macro from MDT until MEND
                while (mdtp < mdt.size() && !mdt.get(mdtp).equalsIgnoreCase("MEND")) {
                    String temp = mdt.get(mdtp);
                    if (temp.contains("(P,1)")) {
                        temp = temp.replace("(P,1)", aptab.get(1));
                    }
                    fr.write(temp + "\n");
                    mdtp++;
                }
            } else {
                fr.write(line + "\n");
            }
        }

        fr.close();
        irb.close();
        mdtb.close();
        mntb.close();

        System.out.println("Macro Pass-II executed successfully!");
        System.out.println("Output generated in pass2.txt");
    }
}
