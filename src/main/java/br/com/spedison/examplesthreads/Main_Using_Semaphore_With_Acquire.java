package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class TaskToRun implements Runnable {
    public static Semaphore semaphore = new Semaphore(10);

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        int user = (new Random()).nextInt();
        String group =  Thread.currentThread().getThreadGroup().getName();
        acquire();
        System.out.println("%s - The user is %d in Thread %s :: %s - My System use %d Threads".formatted(Instant.now().toString(), user, group, name, Thread.activeCount()));
        release();
    }

    private void release() {
        try {
            Thread.sleep(new Random().nextInt(10) * 500);
        } catch (InterruptedException io) {
        }
        semaphore.release();
    }

    private void acquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main_Using_Semaphore_With_Acquire {

    ExecutorService es;

    public static void main(String[] args) throws InterruptedException {
        Main_Using_Semaphore_With_Acquire mus = new Main_Using_Semaphore_With_Acquire();
        mus.startProcessing();
    }

    public void startProcessing() throws InterruptedException {
        es = Executors.newCachedThreadPool();
        TaskToRun ttr = new TaskToRun();
        for (int i = 0; i < 500; i++)
            es.execute(ttr);
        es.shutdown();
        es.awaitTermination(40, TimeUnit.SECONDS);
    }

}
