import java.util.*;

public class prr6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of memory blocks: ");
        int m = sc.nextInt();
        List<MemoryBlock> blocks = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            System.out.print("Enter size of Block " + (i + 1) + ": ");
            int size = sc.nextInt();
            blocks.add(new MemoryBlock(i + 1, size));
        }

        System.out.print("\nEnter number of processes: ");
        int n = sc.nextInt();
        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.print("Enter size of Process " + (i + 1) + ": ");
            int size = sc.nextInt();
            processes.add(new Process(i + 1, size));
        }

        new FirstFit(deepCopy(blocks), deepCopyProcesses(processes)).allocate();
        new NextFit(deepCopy(blocks), deepCopyProcesses(processes)).allocate();
        new WorstFit(deepCopy(blocks), deepCopyProcesses(processes)).allocate();
    }

    private static List<MemoryBlock> deepCopy(List<MemoryBlock> original) {
        List<MemoryBlock> copy = new ArrayList<>();
        for (MemoryBlock b : original) copy.add(new MemoryBlock(b.id, b.size));
        return copy;
    }

    private static List<Process> deepCopyProcesses(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) copy.add(new Process(p.id, p.size));
        return copy;
    }
}

// ======= Classes =======

class MemoryBlock {
    int id;
    int size;
    boolean allocated;

    public MemoryBlock(int id, int size) {
        this.id = id;
        this.size = size;
        this.allocated = false;
    }
}

class Process {
    int id;
    int size;
    int blockAllocated = -1;
    int unusedSpace = 0;

    public Process(int id, int size) {
        this.id = id;
        this.size = size;
    }
}

abstract class MemoryAllocator {
    List<MemoryBlock> blocks;
    List<Process> processes;

    public MemoryAllocator(List<MemoryBlock> blocks, List<Process> processes) {
        this.blocks = blocks;
        this.processes = processes;
    }

    public abstract void allocate();

    public void display(String name) {
        System.out.println("\n========== " + name + " Allocation ==========");
        System.out.println("Process\tSize(KB)\tBlock No\tBlock Size(KB)\tUnused Space(KB)");
        for (Process p : processes) {
            if (p.blockAllocated != -1) {
                MemoryBlock b = blocks.get(p.blockAllocated - 1);
                System.out.printf("P%d\t%d\t\tB%d\t\t%d\t\t%d\n",
                        p.id, p.size, p.blockAllocated, b.size, p.unusedSpace);
            } else {
                System.out.printf("P%d\t%d\t\tNot Allocated\t-\t\t-\n", p.id, p.size);
            }
        }
    }

    protected void resetBlocks() {
        for (MemoryBlock b : blocks) b.allocated = false;
        for (Process p : processes) {
            p.blockAllocated = -1;
            p.unusedSpace = 0;
        }
    }
}

// ======= First Fit =======
class FirstFit extends MemoryAllocator {
    public FirstFit(List<MemoryBlock> blocks, List<Process> processes) {
        super(blocks, processes);
    }

    @Override
    public void allocate() {
        resetBlocks();
        for (Process p : processes) {
            for (MemoryBlock b : blocks) {
                if (!b.allocated && b.size >= p.size) {
                    p.blockAllocated = b.id;
                    b.allocated = true;
                    p.unusedSpace = b.size - p.size;
                    break;
                }
            }
        }
        display("First Fit");
    }
}

// ======= Next Fit =======
class NextFit extends MemoryAllocator {
    public NextFit(List<MemoryBlock> blocks, List<Process> processes) {
        super(blocks, processes);
    }

    @Override
    public void allocate() {
        resetBlocks();
        int lastIndex = 0;

        for (Process p : processes) {
            int count = 0;
            boolean allocatedFlag = false;

            while (count < blocks.size()) {
                MemoryBlock b = blocks.get(lastIndex);
                if (!b.allocated && b.size >= p.size) {
                    p.blockAllocated = b.id;
                    b.allocated = true;
                    p.unusedSpace = b.size - p.size;
                    allocatedFlag = true;
                    break;
                }
                lastIndex = (lastIndex + 1) % blocks.size();
                count++;
            }

            if (!allocatedFlag) p.blockAllocated = -1;
        }
        display("Next Fit");
    }
}

// ======= Worst Fit =======
class WorstFit extends MemoryAllocator {
    public WorstFit(List<MemoryBlock> blocks, List<Process> processes) {
        super(blocks, processes);
    }

    @Override
    public void allocate() {
        resetBlocks();
        for (Process p : processes) {
            MemoryBlock worstBlock = null;
            for (MemoryBlock b : blocks) {
                if (!b.allocated && b.size >= p.size) {
                    if (worstBlock == null || b.size > worstBlock.size)
                        worstBlock = b;
                }
            }
            if (worstBlock != null) {
                p.blockAllocated = worstBlock.id;
                worstBlock.allocated = true;
                p.unusedSpace = worstBlock.size - p.size;
            }
        }
        display("Worst Fit");
    }
}
