package br.com.spedison.examplesthreads;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Main_Using_Executors_FixedThreads {

    private static AtomicInteger countExecution = new AtomicInteger(0);

    static class MySimpleTask implements Runnable {

        private long start;
        private long end;

        public MySimpleTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            long pos = start;
            while (pos < end) {
                pos++;
            }
            System.out.println((new Date()).toString() +" - End of Task " + Thread.currentThread().getName() + " - Execution " + countExecution.incrementAndGet());
        }
    }

    public static void main(String[] args) {

        Runnable mySimpleTask = new MySimpleTask(0, 12_000);
        Runnable mySecondTask = new MySimpleTask(0, 240_000_000);
        Runnable mySimpleTask17 = new MySimpleTask(0, 120_000_000);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        // This Casting is work only JDK more then 19.
        // AutoCloseable autoCloseable = (AutoCloseable) executorService;
        try (executorService) {
            executorService.execute(mySimpleTask);
            Future<?> retTask = executorService.submit(mySecondTask);
            while (!retTask.isDone()) {
                if(System.currentTimeMillis() % 950 == 0) {
                    System.out.println("Waiting...");
                    Thread.sleep(10);
                }
                Thread.yield();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Execute 2 Tasks...Using 1 Thread");

        // For version 17 and les you can use.
        ExecutorService executorService17 = null;
        try {
            // Create one Pool of thread with fixed length
            //executorService17 = Executors.newFixedThreadPool(5);
            // Cread one Pool of threads re-using threads then was termined.
            executorService17 = Executors.newCachedThreadPool();
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            executorService17.execute(mySimpleTask17);
            System.out.println("Waiting by 1 seconds....");
            Thread.sleep(1000);
            executorService17.shutdown();
            while (!executorService17.isTerminated()) {
                if (System.currentTimeMillis() % 850 == 0)
                    System.out.println((new Date()).toString() + " - Waiting...");
                Thread.yield();
                //executorService17.awaitTermination(20, TimeUnit.SECONDS);
            }
            System.out.println("Execute 15 tasks with x threads.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!Objects.isNull(executorService17))
                executorService17.shutdown();
        }

        System.out.println("End of Task! - Main");

    }
}
