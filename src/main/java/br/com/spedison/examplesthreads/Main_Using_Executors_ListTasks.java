package br.com.spedison.examplesthreads;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Main_Using_Executors_ListTasks {

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
            System.out.println((new Date()).toString() + " - End of Task " + Thread.currentThread().getName() + " - Execution " + countExecution.incrementAndGet());
        }
    }

    public static void main(String[] args) {

        Callable<Object> mySimpleTask = Executors.callable(new MySimpleTask(0, 12_000));
        Callable<Object> mySecondTask = Executors.callable(new MySimpleTask(0, 240_000_000));
        Callable<Object> mySimpleTask17 = Executors.callable(new MySimpleTask(0, 120_000_000));

        List<Callable<Object>> listOfTasks = List.of(
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17,
                mySimpleTask, mySecondTask, mySimpleTask17);

        ExecutorService executorService = Executors.newCachedThreadPool();
        try (executorService) {
            var retTask = executorService.invokeAll(listOfTasks);
            while (retTask.stream().map(b -> b.isDone()).filter(b -> b).count() != retTask.size()) {
                if (System.currentTimeMillis() % 950 == 0) {
                    System.out.println("Waiting...");
                    Thread.sleep(10);
                }
                Thread.yield();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("End of Task! - Main");
    }
}
