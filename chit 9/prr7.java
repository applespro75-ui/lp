import java.util.*;

public class prr7 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of frames: ");
        int framesCount = sc.nextInt();

        System.out.print("Enter number of pages in reference string: ");
        int n = sc.nextInt();

        int[] refs = new int[n];
        System.out.println("Enter the reference string (space-separated page numbers):");
        for (int i = 0; i < n; i++) refs[i] = sc.nextInt();

        System.out.println("\n=== FIFO Simulation ===");
        simulateFIFO(refs, framesCount);

        System.out.println("\n=== LRU Simulation ===");
        simulateLRU(refs, framesCount);

        sc.close();
    }

    // ---------- FIFO ----------
    static void simulateFIFO(int[] refs, int framesCount) {
        Queue<Integer> fifoQueue = new LinkedList<>();
        List<Integer> frames = new ArrayList<>(Collections.nCopies(framesCount, null));
        Set<Integer> inFrame = new HashSet<>();
        int faults = 0;

        System.out.printf("%-5s %-6s %-25s %-6s\n", "Idx", "Page", "Frames", "Result");
        for (int i = 0; i < refs.length; i++) {
            int page = refs[i];
            String result;
            if (inFrame.contains(page)) {
                result = "HIT";
            } else {
                faults++;
                result = "FAULT";
                if (fifoQueue.size() < framesCount) {
                    int emptyIdx = frames.indexOf(null);
                    frames.set(emptyIdx, page);
                    fifoQueue.add(page);
                    inFrame.add(page);
                } else {
                    int victim = fifoQueue.poll();
                    inFrame.remove(victim);
                    int vidx = frames.indexOf(victim);
                    frames.set(vidx, page);
                    fifoQueue.add(page);
                    inFrame.add(page);
                }
            }
            System.out.printf("%-5d %-6d %-25s %-6s\n", i, page, framesToString(frames), result);
        }
        printSummary(faults, refs.length);
    }

    // ---------- LRU ----------
    static void simulateLRU(int[] refs, int framesCount) {
        List<Integer> frames = new ArrayList<>(Collections.nCopies(framesCount, null));
        Map<Integer, Integer> lastUsed = new HashMap<>();
        int faults = 0;

        System.out.printf("%-5s %-6s %-25s %-6s\n", "Idx", "Page", "Frames", "Result");
        for (int i = 0; i < refs.length; i++) {
            int page = refs[i];
            String result;
            if (frames.contains(page)) {
                result = "HIT";
                lastUsed.put(page, i);
            } else {
                faults++;
                result = "FAULT";
                if (frames.contains(null)) {
                    int emptyIdx = frames.indexOf(null);
                    frames.set(emptyIdx, page);
                    lastUsed.put(page, i);
                } else {
                    int lruPage = -1;
                    int minIndex = Integer.MAX_VALUE;
                    for (int p : frames) {
                        int used = lastUsed.getOrDefault(p, -1);
                        if (used < minIndex) {
                            minIndex = used;
                            lruPage = p;
                        }
                    }
                    int ridx = frames.indexOf(lruPage);
                    frames.set(ridx, page);
                    lastUsed.remove(lruPage);
                    lastUsed.put(page, i);
                }
            }
            System.out.printf("%-5d %-6d %-25s %-6s\n", i, page, framesToString(frames), result);
        }
        printSummary(faults, refs.length);
    }

    // ---------- Helpers ----------
    static String framesToString(List<Integer> frames) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < frames.size(); i++) {
            Integer v = frames.get(i);
            sb.append(v == null ? "-" : v);
            if (i < frames.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    static void printSummary(int faults, int totalRefs) {
        System.out.println("------------------------------------------");
        System.out.println("Total Page Faults: " + faults);
        double rate = (double) faults / totalRefs * 100.0;
        System.out.printf("Page Fault Rate: %.2f%%\n", rate);
    }
}
