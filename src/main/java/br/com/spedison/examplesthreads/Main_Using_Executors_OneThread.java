package br.com.spedison.examplesthreads;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main_Using_Executors_OneThread {

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
            System.out.println("End of Task " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {

        Runnable mySimpleTask = new MySimpleTask(0, 12_000);
        Runnable mySecondTask = new MySimpleTask(0, 12_000);
        Runnable mySimpleTask17 = new MySimpleTask(0, 12_000);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // This Casting is work only JDK more then 19.
        // AutoCloseable autoCloseable = (AutoCloseable) executorService;
        try (executorService) {
            executorService.execute(mySimpleTask);
            Future<?> retTask = executorService.submit(mySecondTask);
            while (!retTask.isDone()){
                System.out.println("Aguardando...");
                Thread.yield();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // For version 17 and les you can use.
        ExecutorService executorService17 = null;
        try {
            executorService17 = Executors.newSingleThreadExecutor();
            executorService17.execute(mySimpleTask17);
            executorService17.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!Objects.isNull(executorService17))
                executorService17.shutdown();
        }

        System.out.println("End of Task! - Main");

    }
}
