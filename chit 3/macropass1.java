import java.io.*;
import java.util.*;

public class macropass1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("macro_input.asm"));
        FileWriter mnt = new FileWriter("mnt.txt");
        FileWriter mdt = new FileWriter("mdt.txt");
        FileWriter ir = new FileWriter("intermediate.txt");

        LinkedHashMap<String, Integer> pntab = new LinkedHashMap<>();
        String line;
        String macroName = null;
        int mdtp = 1, paramNo = 1, pp = 0;
        boolean insideMacro = false;

        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");

            if (parts[0].equalsIgnoreCase("MACRO")) {
                insideMacro = true;
                line = br.readLine();
                parts = line.trim().split("\\s+");
                macroName = parts[0];
                pp = 0;
                paramNo = 1;
                pntab.clear();

                // Count and store positional parameters
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        parts[i] = parts[i].replaceAll("[&,]", "");
                        pntab.put(parts[i], paramNo++);
                        pp++;
                    }
                }

                // Write MNT entry → MacroName, PP, MDT pointer
                mnt.write(macroName + "\t" + pp + "\t" + mdtp + "\n");

            } else if (parts[0].equalsIgnoreCase("MEND")) {
                mdt.write("MEND\n");
                mdtp++;
                insideMacro = false;

            } else if (insideMacro) {
                // Write macro body to MDT
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].contains("&")) {
                        String param = parts[i].replaceAll("[&,]", "");
                        mdt.write("(P," + pntab.get(param) + ")\t");
                    } else {
                        mdt.write(parts[i] + "\t");
                    }
                }
                mdt.write("\n");
                mdtp++;

            } else {
                // Outside macro definition → Intermediate code
                ir.write(line + "\n");
            }
        }

        br.close();
        mnt.close();
        mdt.close();
        ir.close();

        System.out.println("Macro Pass-I complete.");
        System.out.println("Generated: mnt.txt, mdt.txt, intermediate.txt");
    }
}

