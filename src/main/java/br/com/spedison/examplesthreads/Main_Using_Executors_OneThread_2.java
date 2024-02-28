package br.com.spedison.examplesthreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;

public class Main_Using_Executors_OneThread_2 {

    static class MySimpleTask implements Callable<String> {

        private long start;
        private long end;

        public MySimpleTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String call() throws Exception {
            long pos = this.start;
            while (pos < this.end) {
                pos++;
            }
            long randLong = (new Random()).nextLong(10_000);
            String ret = randLong + " - End of Task " + Thread.currentThread().getName();
            System.out.println(ret);
            return ret;
        }
    }

    public static void main(String[] args) {

        Callable<String> mySimpleTask = new MySimpleTask(0, 12_000);
        Callable<String> mySecondTask = new MySimpleTask(0, 12_000);
        Callable<String> mySimpleTask17 = new MySimpleTask(0, 1_900_200_000);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // This Casting is work only JDK more then 19.
        // AutoCloseable autoCloseable = (AutoCloseable) executorService;
        try (executorService) {
            Future<String> retTask = executorService.submit(mySecondTask);
            while (!retTask.isDone()){
                if (System.currentTimeMillis() % 500 == 0)
                    System.out.println("Waiting...");
                Thread.yield();
            }
            System.out.println("Returned ::: "+ retTask.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // For version 17 and les you can use.
        ExecutorService executorService17 = null;
        try {
            executorService17 = Executors.newSingleThreadExecutor();
            List<Future<String>> listRet = new ArrayList<>(3);
            listRet.add(executorService17.submit(mySimpleTask17));
            listRet.add(executorService17.submit(mySimpleTask17));
            listRet.add(executorService17.submit(mySimpleTask17));
            listRet.add(executorService17.submit(mySimpleTask17));
            listRet.add(executorService17.submit(mySimpleTask17));

            while (listRet.stream().map(b->b.isDone()).filter(b->b).count() != listRet.stream().count()){
                    //!executorService17.isTerminated()) {
                if (System.currentTimeMillis() % 500 == 0)
                    System.out.println("Waiting...");
                Thread.yield();
            }

            for (Future<String> stringFuture : listRet) {
                System.out.println("Returned ::: "+stringFuture.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!Objects.isNull(executorService17))
                executorService17.shutdown();
        }

        System.out.println("End of Task! - Main");

    }
}
