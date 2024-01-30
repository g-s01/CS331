import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

enum MESIState{
    MODIFIED, EXCLUSIVE, SHARED, INVALID
}

class SharedMemory{
    private int _size;
    public int writeMiss;
    public int writeHitOnExclusive;
    public int writeHitOnShared;
    public int writeHitOnModified;
    public int readHit;
    public int readMiss;
    private CacheBlock[] memory;
    private ReentrantLock[] memoryLocks;
    private MESIState[][] state;
    
    public SharedMemory(int size){
        _size = size;
        memoryLocks = new ReentrantLock[_size];
        memory = new CacheBlock[_size];
        state = new MESIState[_size][4];
        for (int i = 0; i < _size; i++) {
            memory[i] = new CacheBlock(i);
            memoryLocks[i] = new ReentrantLock();
            for(int j = 0; j<4; j++){
                state[i][j] = MESIState.INVALID;
            }
        }
        writeMiss = 0;
        writeHitOnExclusive = 0;
        writeHitOnShared = 0;
        writeHitOnModified = 0;
        readHit = 0;
        readMiss = 0;
    }

    synchronized public void increaseWriteMiss(){
        writeMiss++;
    }

    synchronized public void increaseWriteHitOnExclusive(){
        writeHitOnExclusive++;
    }

    synchronized public void increaseWriteHitOnShared(){
        writeHitOnShared++;
    }

    synchronized public void increaseWriteHitOnModified(){
        writeHitOnModified++;
    }

    synchronized public void increaseReadHit(){
        readHit++;
    }

    synchronized public void increaseReadMiss(){
        readMiss++;
    }

    synchronized public void writeBack(int id, int index, int value){
        memory[index].setData(index, value);
        for(int i = 0; i<4; i++){
            if(i != id){
                state[index][i] = MESIState.INVALID;
            }
        }
    }

    synchronized public MESIState getBlockState(int index){
        return memory[index].getState();
    }

    synchronized public CacheBlock readBlock(int id, int index){
        if(memory[index].getState() == MESIState.INVALID){
            memory[index].setState(MESIState.EXCLUSIVE);
        }
        else{
            memory[index].setState(MESIState.SHARED);
        }
        state[index][id] = MESIState.SHARED;
        return memory[index];
    }

    public ReentrantLock getLock(int address) {
        return memoryLocks[address];
    }

    public int getSize(){
        return _size;
    }

    public MESIState getGlobalState(int id, int index){
        return state[index][id];
    }
}

class CacheBlock{
    private MESIState _state;
    private int _data;
    private int _id;

    public CacheBlock(int id){
        _state = MESIState.INVALID;
        _data = 0;
        _id = id;
    }

    public MESIState getState(){
        return _state;
    }

    public int getData(){
        return _data;
    }

    public void setData(int id, int data){
        _id = id;
        _data = data;
    }

    public void setState(MESIState state){
        _state = state;
    }

    public int getID(){
        return _id;
    }
}

class Cache implements Runnable{
    private int id;
    private int associativity;
    private SharedMemory sharedMemory;
    private CacheBlock[] cacheBlocks;
    private ReentrantLock[] cacheLocks;
    Thread t;

    public Cache(int id, int associativity, SharedMemory sharedMemory) {
        this.id = id;
        this.associativity = associativity;
        this.sharedMemory = sharedMemory;
        this.cacheBlocks = new CacheBlock[associativity];
        this.cacheLocks = new ReentrantLock[associativity];
        for (int i = 0; i < associativity; i++) {
            cacheBlocks[i] = new CacheBlock(i);
            cacheLocks[i] = new ReentrantLock();
        }
        t = new Thread();
        t.start();
    }

    private CacheBlock CacheRead(int address){
        int index = getSetIndex(address);
        // read hit
        if(cacheBlocks[index].getID() == address && (sharedMemory.getGlobalState(id, address) == MESIState.EXCLUSIVE || sharedMemory.getGlobalState(id, address) == MESIState.SHARED)){
            System.out.println(Main.ANSI_BOLD_GREEN + "Read hit of address " + address + " on cache " + id + Main.ANSI_RESET);
            sharedMemory.increaseReadHit();
            return cacheBlocks[index];
        }
        // read miss
        else{
            System.out.println(Main.ANSI_RED + "Read miss of address " + address + " on cache " + id + Main.ANSI_RESET);
            if(cacheBlocks[index].getState() == MESIState.MODIFIED){
                writeToMemory(cacheBlocks[index].getID(), cacheBlocks[index].getData());
            }
            cacheBlocks[index] = sharedMemory.readBlock(id, address);
            sharedMemory.increaseReadMiss();
            return cacheBlocks[index];
        }
    }

    private void CacheWrite(int address, int data){
        int index = getSetIndex(address);
        // write hit -> modified
        if(cacheBlocks[index].getID() == address && (sharedMemory.getBlockState(address) == MESIState.MODIFIED)){
            System.out.println(Main.ANSI_BOLD_GREEN + "Write Hit of address " + address + " on cache " + id + Main.ANSI_RESET);
            System.out.println(Main.ANSI_BOLD_GREEN + "Write Hit of address " + address + " on cache " + id + " is write hit on modified block, hence nothing is being done." + Main.ANSI_RESET);
            sharedMemory.increaseWriteHitOnModified();
        }
        // write hit -> exclusive
        else if(cacheBlocks[index].getID() == address && (sharedMemory.getBlockState(address) == MESIState.EXCLUSIVE)){
            System.out.println(Main.ANSI_BOLD_GREEN + "Write Hit of address " + address + " on cache " + id + Main.ANSI_RESET);
            System.out.println(Main.ANSI_BOLD_GREEN + "Write Hit of address " + address + " on cache " + id + " is write hit on exclusive block." + Main.ANSI_RESET);
            writeToMemory(address, data);
            System.out.println(Main.ANSI_BOLD_GREEN + "Writing address " + address + " on cache " + id + Main.ANSI_RESET);
            cacheBlocks[index] = sharedMemory.readBlock(id, address);
            sharedMemory.increaseWriteHitOnExclusive();
        }
        // write hit -> shared
        else if(cacheBlocks[index].getID() == address && (sharedMemory.getBlockState(address) == MESIState.SHARED)){
            System.out.println(Main.ANSI_BOLD_GREEN + "Write Hit of address " + address + " on cache " + id + Main.ANSI_RESET);
            System.out.println(Main.ANSI_BOLD_GREEN + "Write Hit of address " + address + " on cache " + id + " is write hit on shared block." + Main.ANSI_RESET);
            writeToMemory(address, data);
            System.out.println(Main.ANSI_BOLD_GREEN + "Writing address " + address + " on cache " + id + Main.ANSI_RESET);
            sharedMemory.increaseWriteHitOnShared();
        }
        // write miss
        else{
            System.out.println(Main.ANSI_RED + "Write Miss of address " + address + " on cache " + id + Main.ANSI_RESET);
            writeToMemory(address, data);
            sharedMemory.increaseWriteMiss();
        }
    }

    private void writeToMemory(int address, int data) {
        sharedMemory.getLock(address).lock();
        CacheBlock line = sharedMemory.readBlock(id, address);
        line.setData(address, data);
        line.setState(MESIState.MODIFIED);
        sharedMemory.writeBack(id, address, data);
        sharedMemory.getLock(address).unlock();
    }

    private int getSetIndex(int address) {
        return address % associativity;
    }

    public void run(){
        Random rand = new Random();
        for(int i = 0; i<100000; i++){
            int blockId = rand.nextInt(sharedMemory.getSize());
            boolean readOrWrite = rand.nextBoolean();
            if(readOrWrite == true){
                // do read
                CacheRead(blockId);
            }
            else{
                // do write
                int data = rand.nextInt(1000);
                CacheWrite(blockId, data);
            }
        }
    }
}

public class Main {
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
    public static void main(String[] args){
        // set up shared memory and caches
        SharedMemory sharedMemory = new SharedMemory(1024);
        Cache cache1 = new Cache(0, 2, sharedMemory);
        Cache cache2 = new Cache(1, 4, sharedMemory);
        Cache cache3 = new Cache(2, 8, sharedMemory);
        Cache cache4 = new Cache(3, 1, sharedMemory);

        // start cache threads
        Thread thread1 = new Thread(cache1);
        Thread thread2 = new Thread(cache2);
        Thread thread3 = new Thread(cache3);
        Thread thread4 = new Thread(cache4);
        // start the threads
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        // wait for cache threads to finish
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // printing the statistics
        System.out.println("# of Read Hit: " + sharedMemory.readHit);
        System.out.println("# of Read Miss: " + sharedMemory.readMiss);
        System.out.println("# of Write Hit on Modified: " + sharedMemory.writeHitOnModified);
        System.out.println("# of Write Hit on Exclusive: " + sharedMemory.writeHitOnExclusive);
        System.out.println("# of Write Hit on Shared: " + sharedMemory.writeHitOnShared);
        System.out.println("# of Write Hit Miss: " + sharedMemory.writeMiss);
    }
}
