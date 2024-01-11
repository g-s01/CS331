package Q6;
// imports which were required to solve the problem
import java.util.Scanner;
// java class that implements Q6
public class Q6 {
    // constants to be used for coloring the output
    private static final String ANSI_RESET = "\u001B[0m";
    // private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    // private static final String ANSI_BLUE = "\u001B[34m";
    // private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    // private static final String ANSI_WHITE = "\u001B[37m";
    // SUM -> stores sum of all the prime numbers less than the upper-limit
    // SUM is static because all the threads need to access it
    private static int SUM = 0;
    public static void main(String[] args){
        // cin is the scanner used to take input from the user
        Scanner cin = new Scanner(System.in);
        System.out.print("Enter the upper-limit of the primes: ");
        // taking inU upper-limit to calculate primes -> n
        String N = cin.next();
        System.out.print("Enter the number of threads: ");
        String NUMTHREAD = cin.next();
        int n, numThread;
        // try-catch block for ensuring that the size of array is an integer
        try {
            n = Integer.parseInt(N);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Invalid input for the size of the array. Make sure that the size of the array is an integer." + ANSI_RESET);
            cin.close();
            return;
        }
        // try-catch block for ensuring that the number of threads is an integer
        try {
            numThread = Integer.parseInt(NUMTHREAD);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Invalid input for the number of threads. Make sure that the number of threads is an integer." + ANSI_RESET);
            cin.close();
            return;
        }
        cin.close();
        // spawning threads to check primes in the range 1 ... n
        prime[] check = new prime[numThread];
        // thn -> thread number
        // prev -> previous value of left hand side of a range
        int prev = 0, thn = 0;
        for(int i = 0; i<numThread-1; i++) 
        {
            System.out.println(ANSI_YELLOW + "Thread " + (thn+1) + " is checking for primes in the range " + (prev+1) + " to " + Math.min(prev+(n/numThread), n) + ANSI_RESET);
            check[i] = new prime(prev+1, prev+(n/numThread));
            prev += (n/numThread);
            thn++;
        }
        System.out.println(ANSI_YELLOW + "Thread " + (thn+1) + " is checking for primes in the range " + (prev+1) + " to " + n + ANSI_RESET);
        check[numThread-1] = new prime(prev+1, n);
        // // waiting for the individual finding out of the primes in the range
        try {
            for(int i = 0; i<numThread; i++) check[i].th.join();
        } catch(InterruptedException e) {
            System.out.println("Interrupted");
        }
        // show_ans -> method to print the static variable SUM
        show_ans();
    }
    // add -> synchronized method to add the resulting sum of each thread to the final answer SUM
    private synchronized static void add(int n){
        SUM += n;
    }

    private static void show_ans(){
        System.out.println(ANSI_GREEN + "Answer is: " + SUM + ANSI_RESET);
    }
    // prime -> thread to find out the sum of primes in a range
    private static class prime implements Runnable{
        Thread th;
        // l -> left edge of the range
        // r -> right edge of the range
        // sum -> sum of primes in the range
        int l, r, sum = 0;
        prime(int l, int r){
            this.l = l;
            this.r = r;
            th = new Thread(this);
            th.start();
        }
        public void run(){
            for(int i = l; i<=r; i++){
                int x = i, u = 0;
                for(int j = 2; j*j<=x; j++){
                    if(x%j == 0) u = 1;
                }
                if(x != 1 && u == 0) {
                    System.out.println(ANSI_CYAN + x + " is prime" + ANSI_RESET);
                    sum += x;
                }
            }
            add(sum);
        }
    }
}