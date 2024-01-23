// imports which are required to solve the task
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
// SharedCounter class of the task
class SharedCounter {
    // assuming the initial value of the shared counter to be zero
    private static int sharedIntegerCounter_SIC = 0;
    // this method stores which method incremented the present value of the sharedIntegerCounter_SIC
    String[] whichMethod = new String[2 * Main.n + 5];
    // reentrant lock for the class
    ReentrantLock reentrantLock_RL = new ReentrantLock();
    // incrementSynchronized method of the task
    public synchronized void incrementSynchronized() {
        System.out.println(Main.ANSI_YELLOW
                + "Incrementing the sharedIntegerCounter through incrementSynchronized() method" + Main.ANSI_RESET);
        sharedIntegerCounter_SIC++;
        whichMethod[sharedIntegerCounter_SIC] = "incrementSynchronized()";
        System.out.println(
                "Value of sharedIntegerCounter after applying" + Main.ANSI_BOLD_GREEN + " incrementSynchronized()"
                        + Main.ANSI_RESET + " is: " + Main.ANSI_CYAN + sharedIntegerCounter_SIC + Main.ANSI_RESET);
    }
    // incrementWithLock method of the task
    public void incrementWithLock() {
        System.out.println(Main.ANSI_YELLOW
                + "Trying to increment the sharedIntegerCounter through incrementWithLock() method" + Main.ANSI_RESET);
        // trying to obtain the lock
        try {
            System.out.println(Main.ANSI_BLUE + "Waiting to lock the reentrant lock" + Main.ANSI_RESET);
            reentrantLock_RL.lock();
            System.out.println(Main.ANSI_BLUE + "Locked the reentrant lock" + Main.ANSI_RESET);
            sharedIntegerCounter_SIC++;
            whichMethod[sharedIntegerCounter_SIC] = "incrementWithLock()";
            System.out.println(
                    "Value of sharedIntegerCounter after applying" + Main.ANSI_BOLD_GREEN + " incrementWithLock()"
                            + Main.ANSI_RESET + " is: " + Main.ANSI_CYAN + sharedIntegerCounter_SIC + Main.ANSI_RESET);
        } catch(Exception e){
            System.out.println(Main.ANSI_RED + "Error in obtaining the lock, exception thrown." + Main.ANSI_RESET);
            e.printStackTrace();
        }
        finally {
            System.out.println(Main.ANSI_GREEN + "Unlocking the reentrant lock" + Main.ANSI_RESET);
            reentrantLock_RL.unlock();
        }
    }
}
// CounterUpdater class of the task
class CounterUpdater extends Thread {
    // sc -> the SharedCounter on which the threads operate
    // typeOfIncrement -> 0 -> increment the sharedIntegerCounter_SIC through incrementSyncronized() method
    // typeOfIncrement -> 1 -> increment the sharedIntegerCounter_SIC through incrementWithLock() method
    private final SharedCounter sc;
    private final int typeOfIncrement;

    CounterUpdater(SharedCounter sc, int typeOfIncrement) {
        this.sc = sc;
        this.typeOfIncrement = typeOfIncrement;
        start();
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.n; i++) {
            if (typeOfIncrement == 0) {
                sc.incrementSynchronized();
            } else {
                sc.incrementWithLock();
            }
        }
    }
}

public class Main {
    // n -> number of times the each thread would try to increment the sharedIntegerCounter_SIC
    public static int n = 1000;
    // constants to be used for coloring the output
    public static final String ANSI_RESET = "\u001B[0m";
    // private static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_BOLD_PURPLE = "\u001B[1;35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD_GREEN = "\u001B[1;32m";

    public static void main(String[] args) {
        // changeN -> if someone wants to change the number of times the threads try to increment the sharedIntegerCounter_SIC
        int changeN;
        System.out.println("Both the threads will run for n = 1000 times, do you want to change n? if yes enter 1, else enter 0:");
        Scanner in = new Scanner(System.in);
        changeN = in.nextInt();
        if(changeN == 1){
            System.out.println("Enter the desired value of n:");
            n = in.nextInt();
        }
        in.close();
        SharedCounter instance = new SharedCounter();
        // spawning the threads
        CounterUpdater sync = new CounterUpdater(instance, 0);
        CounterUpdater lock = new CounterUpdater(instance, 1);
        // joining the threads
        try {
            sync.join();
            lock.join();
        } catch (InterruptedException e) {
            System.out.println(ANSI_RED
                    + "Error in spawning and joining the threads for doing incrementSynchronized() and incrementWithLock()."
                    + ANSI_RESET);
        }
        // printing 100 '*' to enhance the visibility of the next lines
        for (int i = 0; i < 100; i++)
            System.out.print(ANSI_BOLD_PURPLE + '*' + ANSI_RESET);
        System.out.println();
        // printing which method modified the sharedIntegerCounter_SIC when
        System.out.println("See below which method incremented the sharedCounter when:");
        for (int i = 1; i <= 2 * n; i++) {
            System.out.println(ANSI_BOLD_GREEN + instance.whichMethod[i] + ANSI_RESET
                    + " incremented the sharedCounter to: " + ANSI_BOLD_GREEN + i + ANSI_RESET);
        }
    }
}