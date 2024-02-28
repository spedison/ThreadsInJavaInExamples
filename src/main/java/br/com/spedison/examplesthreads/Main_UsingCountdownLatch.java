package br.com.spedison.examplesthreads;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main_UsingCountdownLatch {

    private static volatile int j = 0;
    private static AtomicInteger maxExecution = new AtomicInteger(3);

    static private CountDownLatch countDownLatch = new CountDownLatch(2);
    static private Runnable task = () -> {
        int i = (new Random()).nextInt();
        System.out.println("%d x %d = %d".formatted(i, j, i * j));
        countDownLatch.countDown();
    };

    public static void main(String[] args) throws InterruptedException {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        ScheduledFuture<?> scheduledFuture =
            executor.scheduleAtFixedRate(task,0,1, TimeUnit.SECONDS);

        while (true){
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            j = (new Random()).nextInt();
            countDownLatch = new CountDownLatch(2);
            var maxExec = maxExecution.decrementAndGet();
            if (maxExec==0) {
                scheduledFuture.cancel(false);
                executor.shutdown();
                executor.awaitTermination(10,TimeUnit.SECONDS);
                System.out.println("Programa terminado");
                System.exit(0);
            }
        }

    }

}
