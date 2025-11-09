import java.util.*;

class Process {
    int pid, arrivalTime, burstTime, remainingTime;
    int completionTime, waitingTime, turnaroundTime;

    public Process(int pid, int at, int bt) {
        this.pid = pid;
        this.arrivalTime = at;
        this.burstTime = bt;
        this.remainingTime = bt;
    }
}

public class prr4 {

    // ---------- SJF (Preemptive / SRTF) ----------
    static void sjfPreemptive(List<Process> plist) {
        int n = plist.size(), time = 0, completed = 0;
        Process current = null;

        while (completed < n) {
            Process shortest = null;
            int minRemain = Integer.MAX_VALUE;

            for (Process p : plist) {
                if (p.arrivalTime <= time && p.remainingTime > 0 && p.remainingTime < minRemain) {
                    shortest = p;
                    minRemain = p.remainingTime;
                }
            }

            if (shortest == null) { time++; continue; }

            shortest.remainingTime--;
            time++;

            if (shortest.remainingTime == 0) {
                shortest.completionTime = time;
                shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                completed++;
            }
        }

        display(plist, "SJF (Preemptive)");
    }

    // ---------- Round Robin (Preemptive) ----------
    static void roundRobin(List<Process> plist, int quantum) {
        int n = plist.size();
        Queue<Process> q = new LinkedList<>();
        int time = 0, completed = 0;

        plist.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int idx = 0;

        while (completed < n) {
            while (idx < n && plist.get(idx).arrivalTime <= time)
                q.add(plist.get(idx++));

            if (q.isEmpty()) {
                time++;
                continue;
            }

            Process cur = q.poll();
            int exec = Math.min(cur.remainingTime, quantum);
            cur.remainingTime -= exec;
            time += exec;

            // Add newly arrived processes during this slice
            while (idx < n && plist.get(idx).arrivalTime <= time)
                q.add(plist.get(idx++));

            if (cur.remainingTime > 0) {
                q.add(cur); // put back if not finished
            } else {
                cur.completionTime = time;
                cur.turnaroundTime = cur.completionTime - cur.arrivalTime;
                cur.waitingTime = cur.turnaroundTime - cur.burstTime;
                completed++;
            }
        }

        display(plist, "Round Robin (Preemptive, q=" + quantum + ")");
    }

    // ---------- Display ----------
    static void display(List<Process> plist, String title) {
        System.out.println("\n===== " + title + " =====");
        System.out.println("PID\tAT\tBT\tWT\tTAT");
        double totWT = 0, totTAT = 0;
        for (Process p : plist) {
            System.out.printf("%d\t%d\t%d\t%d\t%d\n",
                    p.pid, p.arrivalTime, p.burstTime, p.waitingTime, p.turnaroundTime);
            totWT += p.waitingTime;
            totTAT += p.turnaroundTime;
        }
        System.out.printf("Average Waiting Time: %.2f\n", totWT / plist.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totTAT / plist.size());
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        List<Process> plist = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for Process P" + (i + 1) + ":");
            System.out.print("Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Burst Time: ");
            int bt = sc.nextInt();
            plist.add(new Process(i + 1, at, bt));
        }

        // Copy lists for each algorithm
        List<Process> sjfList = deepCopy(plist);
        List<Process> rrList = deepCopy(plist);

        sjfPreemptive(sjfList);

        System.out.print("\nEnter Time Quantum for Round Robin: ");
        int q = sc.nextInt();
        roundRobin(rrList, q);
    }

    static List<Process> deepCopy(List<Process> list) {
        List<Process> newList = new ArrayList<>();
        for (Process p : list)
            newList.add(new Process(p.pid, p.arrivalTime, p.burstTime));
        return newList;
    }
}
