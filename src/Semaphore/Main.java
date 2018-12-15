package Semaphore;

public class Main {
    public static void main(String[] args) {
        Semaphore s1 = new Semaphore(2);
        s1.signal(s1);
    }
}

