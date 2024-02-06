import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Database {
    public DatabaseOperations dbOperations;
    public DatabaseInitializer dbInitializer;
    private ConcurrencyControl cc;
    private int initUserSize;
    private int initOrderSize;
 
    public Database(int orderSize, int userSize) {
        cc = new ConcurrencyControl();
        initUserSize = userSize;
        initOrderSize = orderSize;
        dbInitializer = new DatabaseInitializer(initOrderSize, initUserSize);
        dbOperations = new DatabaseOperations(dbInitializer.getUsers(), dbInitializer.getOrders(), cc);
    }
}

class ConcurrencyControl {
    private final Lock readUserLock = new ReentrantLock();
    private final Lock readOrderLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();
 
    public synchronized void lockUserRead() {
        readUserLock.lock();
    }
 
    public synchronized void unlockUserRead() {
        readUserLock.unlock();
    }

    public synchronized void lockOrderRead() {
        readOrderLock.lock();
    }
 
    public synchronized void unlockOrderRead() {
        readOrderLock.unlock();
    }
 
    public synchronized void lockWrite() {
        writeLock.lock();
    }
 
    public synchronized void unlockWrite() {
        writeLock.unlock();
    }
}

class DatabaseOperations {
    private ConcurrentHashMap<Integer, User> users;
    private ConcurrentHashMap<Integer, Order> orders;
    private ConcurrencyControl cc;
 
    public DatabaseOperations(ConcurrentHashMap<Integer, User> users, ConcurrentHashMap<Integer, Order> orders, ConcurrencyControl cc) {
        this.users = users;
        this.orders = orders;
        this.cc = cc;
    }
 
    public synchronized void readUserDetails(int userID, int threadID) {
        User user = users.get(userID);
        System.out.println(Main.ANSI_RED + "Thread: " + Main.ANSI_RESET + threadID + " " + Main.ANSI_GREEN + "Reading details of user: " + Main.ANSI_RESET + userID);
        System.out.println(Main.ANSI_BLUE + "UserID: " + Main.ANSI_RESET + user.getUserID());
        System.out.println(Main.ANSI_BLUE + "UserName: " + Main.ANSI_RESET + user.getUserName());
        System.out.println(Main.ANSI_BLUE + "UserEmail: " + Main.ANSI_RESET + user.getUserEmail());
    }
 
    public synchronized void readOrderDetails(int orderID, int threadID) {
        Order order = orders.get(orderID);
        System.out.println(Main.ANSI_RED + "Thread: " + Main.ANSI_RESET + threadID + " " + Main.ANSI_GREEN + "Reading details of order: " + Main.ANSI_RESET + orderID);
        System.out.println(Main.ANSI_BLUE + "OrderID: " + Main.ANSI_RESET + order.getOrderID());
        System.out.println(Main.ANSI_BLUE + "UserID: " + Main.ANSI_RESET + order.getUserID());
        System.out.println(Main.ANSI_BLUE + "Product: " + Main.ANSI_RESET + order.getProduct());
        System.out.println(Main.ANSI_BLUE + "Quantity: " + Main.ANSI_RESET + order.getQuantity());
    }

    public synchronized void writeUser(User user, int threadID) {
        users.put(user.getUserID(), user);
        System.out.println(Main.ANSI_RED + "Thread: " + Main.ANSI_RESET + threadID + " " + Main.ANSI_CYAN + "Wrote the following user to the database" + Main.ANSI_RESET);
        System.out.println(Main.ANSI_YELLOW + "UserID: " + Main.ANSI_RESET + user.getUserID());
        System.out.println(Main.ANSI_YELLOW + "UserName: " + Main.ANSI_RESET + user.getUserName());
        System.out.println(Main.ANSI_YELLOW + "UserEmail: " + Main.ANSI_RESET + user.getUserEmail());
    }

    public synchronized void writeOrder(Order order, int threadID) {
        orders.put(order.getOrderID(), order);
        System.out.println(Main.ANSI_RED + "Thread: " + Main.ANSI_RESET + threadID + " " + Main.ANSI_CYAN + "Wrote the following order to the database" + Main.ANSI_RESET);
        System.out.println(Main.ANSI_YELLOW + "OrderID: " + Main.ANSI_RESET + order.getOrderID());
        System.out.println(Main.ANSI_YELLOW + "UserID: " + Main.ANSI_RESET + order.getUserID());
        System.out.println(Main.ANSI_YELLOW + "Product: " + Main.ANSI_RESET + order.getProduct());
        System.out.println(Main.ANSI_YELLOW + "Quantity: " + Main.ANSI_RESET + order.getQuantity());
    }
}

class DatabaseInitializer{
    public ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, Order> orders = new ConcurrentHashMap<>();
    private int initUserSize;
    private int initOrderSize;
 
    public DatabaseInitializer(int orderSize, int userSize) {
        initOrderSize = orderSize;
        initUserSize = userSize;
        initializeUsers();
        initializeOrders();
    }
 
    private void initializeUsers() {
         for(int i = 0; i<initUserSize; i++){
             User user = Main.generateRandomUser(initUserSize);
             users.put(user.getUserID(), user);
             System.out.println(Main.ANSI_BOLD_PURPLE + "Wrote the following user to the database during initialization" + Main.ANSI_RESET);
             System.out.println("UserID: " + user.getUserID());
             System.out.println("UserName: " + user.getUserName());
             System.out.println("UserEmail: " + user.getUserEmail());
         }
    }
 
    private void initializeOrders() {
         for(int i = 0; i<initOrderSize; i++){
             Order order = Main.generateRandomOrder(initOrderSize, initUserSize);
             orders.put(order.getOrderID(), order);
             System.out.println(Main.ANSI_BOLD_PURPLE + "Wrote the following order to the database during initialization" + Main.ANSI_RESET);
             System.out.println("OrderID: " + order.getOrderID());
             System.out.println("UserID: " + order.getUserID());
             System.out.println("Product: " + order.getProduct());
             System.out.println("Quantity: " + order.getQuantity());
         }
    }
 
    public ConcurrentHashMap<Integer, User> getUsers() {
        return users;
    }
 
    public ConcurrentHashMap<Integer, Order> getOrders() {
        return orders;
    }

    public int getUserSize(){
        return users.size();
    }

    public int getOrderSize(){
        return orders.size();
    }
}

class User{
    private int _UserID;
    private String _UserName;
    private String _UserEmail;

    User(int userID, String userName, String userEmail){
        _UserID = userID;
        _UserName = userName;
        _UserEmail = userEmail;
    }

    public int getUserID(){
        return _UserID;
    }

    public String getUserName(){
        return _UserName;
    }

    public String getUserEmail(){
        return _UserEmail;
    }

    public void setUserID(int userID){
        _UserID = userID;
    }

    public void setUserName(String userName){
        _UserName = userName;
    }

    public void setUserEmail(String userEmail){
        _UserEmail = userEmail;
    }
}

class Order{
    private int _OrderID;
    private int _UserID;
    private String _Product;
    private int _Quantity;

    Order(int orderID, int userID, String product, int quantity){
        _OrderID = orderID;
        _UserID = userID;
        _Product = product;
        _Quantity = quantity;
    }

    public int getOrderID(){
        return _OrderID;
    }

    public int getUserID(){
        return _UserID;
    }

    public String getProduct(){
        return _Product;
    }

    public int getQuantity(){
        return _Quantity;
    }

    public void setOrderID(int orderID){
        _OrderID = orderID;
    }

    public void setUserID(int userID){
        _UserID = userID;
    }

    public void setProduct(String product){
        _Product = product;
    }

    public void setQuantity(int quantity){
        _Quantity = quantity;
    }
}

class DatabaseTransaction implements Runnable{
    private int type;
    private Database db;
    private int threadID;
    Thread t;

    DatabaseTransaction(int type, Database db, int threadID){
        this.type = type;
        this.db = db;
        this.threadID = threadID;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run(){
        if(type == 0){
            int userSize = Main.db.dbInitializer.getUserSize();
            int randUserID = userSize > 0 ? Main.rand.nextInt(userSize) : 0;
            if(db.dbInitializer.users.containsKey(randUserID)){
                db.dbOperations.readUserDetails(randUserID, threadID);
            }
            else{
                System.out.println("Thread tried to read userID " + randUserID + " but key is not in the database.");
            }
        }
        else if(type == 1){
            int orderSize = Main.db.dbInitializer.getOrderSize();
            int randOrderID = orderSize > 0 ? Main.rand.nextInt(orderSize) : 0;
            if(db.dbInitializer.orders.containsKey(randOrderID)){
                db.dbOperations.readOrderDetails(randOrderID, threadID);
            }
            else{
                System.out.println("Thread tried to read userID " + randOrderID + " but key is not in the database.");
            }
        }
        else if(type == 2){
            User user = Main.generateRandomUser(Integer.MAX_VALUE);
            db.dbOperations.writeUser(user, threadID);
        }
        else{
            Order order = Main.generateRandomOrder(Integer.MAX_VALUE, Integer.MAX_VALUE);
            db.dbOperations.writeOrder(order, threadID);
        }
    }
}

public class Main{
    // colors
    public static final String ANSI_RESET = "\u001B[0m";
    // private static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_BOLD_PURPLE = "\u001B[1;35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD_GREEN = "\u001B[1;32m";

    public static Random rand = new Random();
    public static String ranString = "abcdefghijklmnopqrstuvwxyz";

    public static Database db;

    public static Order generateRandomOrder(int userLimit, int orderLimit){
        int randOrderID = Main.rand.nextInt(orderLimit);
        int randUserID = Main.rand.nextInt(userLimit);
        int productLength = Main.rand.nextInt(10)+1;
        String randProduct = "";
        int randQuantity = Main.rand.nextInt(Integer.MAX_VALUE);
        for(int i = 0; i<productLength; i++){
            int randChar = Main.rand.nextInt(25);
            randProduct += Main.ranString.substring(randChar, randChar+1);
        }
        Order order = new Order(randOrderID, randUserID, randProduct, randQuantity);
        return order;
    }

    public static User generateRandomUser(int limit){
        int randUserID = Main.rand.nextInt(limit);
        int nameLength = Main.rand.nextInt(10)+1;
        int mailLength = Main.rand.nextInt(20)+1;
        String randUserName = "", randUserEmail = "";
        for(int i = 0; i<nameLength; i++){
            int randChar = Main.rand.nextInt(25);
            randUserName += Main.ranString.substring(randChar, randChar+1);
        }
        for(int i = 0; i<mailLength; i++){
            int randChar = Main.rand.nextInt(25);
            randUserEmail += Main.ranString.substring(randChar, randChar+1);
        }
        User user = new User(randUserID, randUserName, randUserEmail);
        return user;
    }
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int initUserSize, initOrderSize, numberOfThreads;
        System.out.println("Enter the initial size of users size: ");
        initUserSize = scan.nextInt();
        System.out.println("Enter the initial size of orders size: ");
        initOrderSize = scan.nextInt();
        System.out.println("Enter the number of threads: ");
        numberOfThreads = scan.nextInt();
        scan.close();
        db = new Database(initOrderSize, initUserSize);
        DatabaseTransaction[] arr = new DatabaseTransaction[numberOfThreads];
        try{
            Thread.sleep(2000);
        } catch(Exception e){
            System.err.println();
        }
        Thread[] tarr = new Thread[numberOfThreads];
        for(int i = 0; i<numberOfThreads; i++){
            int type = rand.nextInt(4);
            arr[i] = new DatabaseTransaction(type, db, i);
        }
        for(int i = 0; i<numberOfThreads; i++){
            tarr[i] = new Thread(arr[i]);
            tarr[i].start();
        }
        for(int i = 0; i<numberOfThreads; i++){
            try {
                tarr[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}