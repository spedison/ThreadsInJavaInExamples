package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class TaskToRunUsingTryAcquire implements Runnable {
    public static Semaphore semaphore = new Semaphore(10);
    public static AtomicInteger currentNumberOfTasks = new AtomicInteger(0);
    public static AtomicInteger quantOfWaitTasks = new AtomicInteger(0);

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        int user = (new Random()).nextInt();
        String group = Thread.currentThread().getThreadGroup().getName();
        quantOfWaitTasks.incrementAndGet();
        boolean contineTask = tryAcquire();
        while (!contineTask) {
            contineTask = tryAcquire();
        }
        quantOfWaitTasks.decrementAndGet();
        System.out.println("%s - The user is %d in Thread (%s :: %s) - My java System have %d Threads -  With %d Tasks Running and %d in Waiting".
                formatted(Instant.now().toString(), user, group, name, Thread.activeCount(), currentNumberOfTasks.get(), quantOfWaitTasks.get()));
        release();
    }

    private void release() {
        try {
            Thread.sleep(new Random().nextInt(10) * 500);
            currentNumberOfTasks.decrementAndGet();
        } catch (InterruptedException io) {
        }
        semaphore.release();
    }

    private boolean tryAcquire() {
        try {
            if (!semaphore.tryAcquire(20, TimeUnit.SECONDS))
                return false;
            currentNumberOfTasks.incrementAndGet();
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

public class Main_Using_Semaphore_With_TryAcquire {

    ExecutorService es;

    public static void main(String[] args) throws InterruptedException {
        Main_Using_Semaphore_With_TryAcquire mus = new Main_Using_Semaphore_With_TryAcquire();
        mus.startProcessing();
    }

    public void startProcessing() throws InterruptedException {
        es = Executors.newCachedThreadPool();
        TaskToRunUsingTryAcquire ttr = new TaskToRunUsingTryAcquire();
        for (int i = 0; i < 500; i++)
            es.execute(ttr);
        es.shutdown();
        es.awaitTermination(40, TimeUnit.SECONDS);
    }

}
