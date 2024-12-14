import java.util.concurrent.Semaphore;
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}

class ReadWriteLock{
    private Semaphore S=new Semaphore(1);
    public void readLock() {
    }
    public void writeLock() {
    }
    public void readUnLock() {
    }
    public void writeUnLock() {
    }
}