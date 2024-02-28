package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class TaskToRunWithLockWithReadWriteLocks implements Runnable {

    static public volatile int counter = 0;
    static public AtomicInteger terminates = new AtomicInteger(0);

    static public ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(10) + 15);
        } catch (InterruptedException ie) {
        }
        Lock l = lock.writeLock();

        l.lock(); // It's can use one or more locks.

        String name = Thread.currentThread().getName() + "[[" + counter + "]]";

        System.out.println("%s - Writing :: Count %d in thread %s".formatted(Instant.now().toString(), counter, name));
        int localCounter = ++counter;
        try {
            Thread.sleep(new Random().nextInt(10) + 30);
        } catch (InterruptedException ie) {
        }
        System.out.println("%s - Was Write :: Count %d in thread %s".formatted(Instant.now().toString(), localCounter, name));

        l.unlock(); // For each lock there are one unlock.

        terminates.incrementAndGet();
    }
}

public class Main_Using_Locks_V2_WithReadWriteLocks {
    public static void main(String[] args) {
        new Main_Using_Locks_V2_WithReadWriteLocks().run(args);
    }

    private void run(String[] args) {
        try (ExecutorService es = Executors.newCachedThreadPool()) {
            var task = new TaskToRunWithLockWithReadWriteLocks();
            final int MAX_PROCESS = 60;
            for (int i = 0; i < MAX_PROCESS; i++) {
                // Task for write
                es.execute(task);
                // Task for read
                es.execute(() -> {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ie) {
                    }
                    Lock l = TaskToRunWithLockWithReadWriteLocks.lock.readLock();
                    l.lock();
                    String name = Thread.currentThread().getName() + "[[" + TaskToRunWithLockWithReadWriteLocks.counter + "]]";
                    System.out.println(Instant.now() + " - Reading counter = " + TaskToRunWithLockWithReadWriteLocks.counter + " in " + name);
                    System.out.println(Instant.now() + " - Was Read counter = " + TaskToRunWithLockWithReadWriteLocks.counter + " in " + name);
                    l.unlock();
                });
            }
        }
    }
}