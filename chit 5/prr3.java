import java.util.*;

class Process {
    int pid, arrivalTime, burstTime, priority;
    int completionTime, waitingTime, turnaroundTime;

    public Process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

// ---------- FCFS ----------
class FCFS {
    List<Process> processes;
    public FCFS(List<Process> processes) { this.processes = processes; }

    public void execute() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrivalTime)
                currentTime = p.arrivalTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            currentTime = p.completionTime;
        }
        display("FCFS");
    }

    private void display(String name) {
        System.out.println("\n===== " + name + " Scheduling =====");
        double totalWT = 0, totalTAT = 0;
        System.out.println("PID\tAT\tBT\tWT\tTAT");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t%d\t%d\t%d\n",
                    p.pid, p.arrivalTime, p.burstTime, p.waitingTime, p.turnaroundTime);
            totalWT += p.waitingTime;
            totalTAT += p.turnaroundTime;
        }
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTAT / processes.size());
    }
}

// ---------- Priority (Non-Preemptive) ----------
class PriorityNonPreemptive {
    List<Process> processes;
    public PriorityNonPreemptive(List<Process> processes) { this.processes = processes; }

    public void execute() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0, completed = 0;
        boolean[] done = new boolean[processes.size()];

        while (completed < processes.size()) {
            Process best = null;
            int idx = -1;

            for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (!done[i] && p.arrivalTime <= currentTime) {
                    if (best == null || p.priority < best.priority)
                        { best = p; idx = i; }
                }
            }

            if (best == null) { currentTime++; continue; }

            currentTime += best.burstTime;
            best.completionTime = currentTime;
            best.turnaroundTime = best.completionTime - best.arrivalTime;
            best.waitingTime = best.turnaroundTime - best.burstTime;
            done[idx] = true;
            completed++;
        }
        display("Priority (Non-Preemptive)");
    }

    private void display(String name) {
        System.out.println("\n===== " + name + " Scheduling =====");
        double totalWT = 0, totalTAT = 0;
        System.out.println("PID\tAT\tBT\tPR\tWT\tTAT");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\n",
                    p.pid, p.arrivalTime, p.burstTime, p.priority,
                    p.waitingTime, p.turnaroundTime);
            totalWT += p.waitingTime;
            totalTAT += p.turnaroundTime;
        }
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTAT / processes.size());
    }
}

// ---------- Main ----------
public class prr3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for Process P" + (i + 1) + ":");
            System.out.print("Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Burst Time: ");
            int bt = sc.nextInt();
            System.out.print("Priority: ");
            int pr = sc.nextInt();
            processes.add(new Process(i + 1, at, bt, pr));
        }

        // Make copies
        List<Process> fcfsList = copy(processes);
        List<Process> prioList = copy(processes);

        new FCFS(fcfsList).execute();
        new PriorityNonPreemptive(prioList).execute();
    }

    private static List<Process> copy(List<Process> list) {
        List<Process> newList = new ArrayList<>();
        for (Process p : list)
            newList.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        return newList;
    }
}
