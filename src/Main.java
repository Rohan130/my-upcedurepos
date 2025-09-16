import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Use ArrayList to store workers (flexible size, not fixed at 100)
        ArrayList<Worker> workers = new ArrayList<>();

        // Example: Add some workers
        workers.add(new Worker("Alice", 1200, 300));
        workers.add(new Worker("Bob", 1500, 200));
        workers.add(new Worker("Charlie", 1000, 500));

        // Print all workers
        System.out.println("=== Workers List ===");
        for (Worker w : workers) {
            System.out.println(w);
        }

        // Calculate global total salary
        double globalTotal = 0;
        for (Worker w : workers) {
            globalTotal += w.getBaseSalary();
        }

        System.out.println("\nGlobal Total Salary = " + globalTotal);
    }
}
