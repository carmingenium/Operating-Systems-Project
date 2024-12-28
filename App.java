import java.util.*;
import java.util.concurrent.Semaphore;
public class App {
    public static void main(String[] args) {
        @SuppressWarnings("unused")
        ReadWriteLock instance = new ReadWriteLock();
        MyThread r1 = new MyThread(instance, "Reader1");
        MyThread r2 = new MyThread(instance, "Reader2");
        MyThread r3 = new MyThread(instance, "Reader3");
        MyThread w1 = new MyThread(instance, "Writer1");
        MyThread w2 = new MyThread(instance, "Writer2");
        MyThread w3 = new MyThread(instance, "Writer3");


        r1.start();
        r2.start();
        w1.start();
        r3.start();
        w2.start();
        w3.start();

        try {
            r1.join();
            r2.join();
            r3.join();
            w1.join();
            w2.join();
            w3.join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println("Completed: reader count: " + ReadWriteLock.reader);
    }
}

class ReadWriteLock{
    @SuppressWarnings("FieldMayBeFinal")

    public static int reader = 0;
    List<String> totalReaderList = new ArrayList<>();
    List<String> currentReaders = new ArrayList<>();

    private Semaphore S=new Semaphore(1);

    public void readLock(String readerName) { // reader access 
        try {
            S.acquire(); // try to access, if there is a writer, wait.
            System.out.println("Access granted to " + readerName);
            S.release();
            reader++;
            totalReaderList.add(readerName);
        } catch (InterruptedException exc) {
            System.out.println(exc);
        }
        // acquire
        // stops 1 permit for readers and all permits for writers
    }



    public void writeLock(String writerName) { // writer access
        try {
            System.out.println(writerName + " is trying to access...");
            S.acquire(); // if another writer is writing, wait. 
            if(reader>0){
                System.out.println("Waiting for readers to finish, locking access.");
                S.acquire();
            }
            // check if namelists are equal. if not, exit.
            Collections.sort(totalReaderList);
            Collections.sort(currentReaders);
            if(!totalReaderList.equals(currentReaders)){
                System.out.println("Not every reader has read yet, locking access.");
                S.acquire();
            }
            System.out.println("Access granted to " + writerName);
        } catch (InterruptedException exc) {
            System.out.println(exc);
        }
        // acquire
        // stops all permits for readers and other writers
    }




    public void readUnLock(String readerName) { // reader release
        if(reader==0 || !totalReaderList.contains(readerName)){
            System.out.println("No readers to release access.");
            return;
        }
        reader--;
        System.out.println("Access released from: " + (readerName));
        currentReaders.add(readerName);
        if(reader==0){
        Collections.sort(totalReaderList);
        Collections.sort(currentReaders);

            if(totalReaderList.equals(currentReaders)){
                System.out.println("Everyone has read, releasing access to writers...");
                S.release();
            }
        }
    }





    public void writeUnLock(String writerName) { // writer release
        currentReaders.clear();
        S.release();
        System.out.println("Access released from " + writerName);
        // reset stated namelist.
        
        // release
        // releases all permits for readers and other writers
    }
}
class MyThread extends Thread 
{
    private ReadWriteLock instance;
    public MyThread(ReadWriteLock instance, String threadName)  
    { 
        super(threadName);
        this.instance = instance;
    }

    @Override
    public void run() { 
       if(this.getName().substring(0,6).equals("Reader")){
            instance.readLock(this.getName());
            try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(50);
            }
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            instance.readUnLock(this.getName());
       }
       else{
            instance.writeLock(this.getName());
            try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(50);
            }
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            instance.writeUnLock(this.getName());
       }
    } 
} 