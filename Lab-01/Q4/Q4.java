package Q4;
// imports which were required to solve the problem
import java.util.Arrays;
import java.util.Scanner;
// java class that implements Q4
public class Q4 {
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
    public static void main(String[] args){
        // cin is the scanner used to take input from the user
        Scanner cin = new Scanner(System.in);
        System.out.println("Enter the size of the array:");
        // taking in size of the array -> n
        String N = cin.next();
        int n;
        // try-catch block for ensuring that the size of array is an integer
        try {
            n = Integer.parseInt(N);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Invalid input for the size of the array. Make sure that the size of the array is an integer." + ANSI_RESET);
            cin.close();
            return;
        }
        System.out.println("Enter the contents of the array: ");
        String[] ARR = new String[n];
        int[] arr = new int[n];
        // taking in the contents of the array -> `arr`
        for(int i = 0; i<n; i++) ARR[i] = cin.next();
        cin.close();
        // try-catch block for ensuring that the contents of the array are integers
        try {
            for(int i = 0; i<n; i++) arr[i] = Integer.parseInt(ARR[i]);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Invalid input for the contents of the array. Make sure that the contents of the array are integers." + ANSI_RESET);
            return;
        }
        System.out.println(ANSI_YELLOW + "Initial array: " + Arrays.toString(arr) + ANSI_RESET);
        Q4 sorter = new Q4();
        // calling the `sort` method to sort the array `arr`
        sorter.sort(arr);
        // printing the sorted array
        System.out.println(ANSI_GREEN + "Sorted array: " + Arrays.toString(arr) + ANSI_RESET);
    }
    // method to sort an array
    private void sort(int[] arr){
        // applying merge sort to sort the array `arr`
        if(arr.length <= 1) return;
        int mid = arr.length/2;
        int[] left_array  = new int[mid];
        int[] right_array = new int[arr.length-mid];
        // copying the left and right part of the array
        for(int i = 0; i<mid; i++) left_array[i] = arr[i];
        for(int i = mid; i<arr.length; i++) right_array[i-mid] = arr[i];
        // spawning threads for sorting the left part and the right part of the array individually
        sorter lft = new sorter(left_array);
        sorter rgt = new sorter(right_array);
        // waiting for the individual sorting of the left anf right part of the array
        try {
            System.out.println(ANSI_CYAN + "Spawned left and right threads for: " + Arrays.toString(arr) + ANSI_RESET);
            lft.th.join();
            rgt.th.join();
        } catch (InterruptedException e) {
            System.out.println("Error in spawning left and right threads for the given array.");
        }
        // merging the `sorted` left and right part of the array
        merge(left_array, right_array, arr);
    }
    // class to spawn threads to sort an array
    private class sorter implements Runnable{
        Thread th;
        int[] arr;
        sorter(int[] arr){
            th = new Thread(this);
            this.arr = arr;
            th.start();
        }
        public void run(){
            sort(arr);
        }
    }
    // method to merge the `sorted` left and right parts of an array
    private void merge(int[] left_array, int[] right_array, int[] arr){
        // i -> keeps track of the elements of the left part
        // j -> keeps track of the elements of the right part
        // k -> keeps track of the elements of the main array `arr`

        int i = 0, j = 0, k = 0;

        while (i < left_array.length && j < right_array.length) {
            if (left_array[i] < right_array[j]) {
                arr[k] = left_array[i];
                k++; i++;
            } else {
                arr[k] = right_array[j];
                k++; j++;
            }
        }

        while (i < left_array.length) {
            arr[k] = left_array[i];
            k++; i++;
        }

        while (j < right_array.length) {
            arr[k] = right_array[j];
            k++; j++;
        }
    }
}