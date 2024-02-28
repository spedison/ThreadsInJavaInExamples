package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main_Problem_Consummer_Producer_With_All_Tools {

    private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
    AtomicBoolean stopProcess = new AtomicBoolean(false);

    public static void main(String[] args) {
        new Main_Problem_Consummer_Producer_With_All_Tools().run(args);
    }

    private void run(String[] args) {
        long start = System.currentTimeMillis();

        Runnable taskProducer = () -> {
            // call thread, but do not produce more data for process.
            if (stopProcess.get())
                return;

            simulateProcess();
            int value = new Random().nextInt(1_000_000);
            try {

                //queue.put(value);
                if (!queue.offer(value, 10_000, TimeUnit.MILLISECONDS)) {
                    Thread.interrupted();
                    return;
                }
                System.out.println("%s - PRODUCER Thread %s produce value = %d".formatted(
                        Instant.now().toString(),
                        Thread.currentThread().getName(),
                        value
                ));
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new RuntimeException(e);
            }
        };

        Runnable taskConsumer = () -> {
            simulateProcess();
            simulateProcess();
            simulateProcess();
            try {
                int value = queue.poll(10_000, TimeUnit.MILLISECONDS);
                System.out.println("%s - CONSUMER In thread %s Consumer value %d".formatted(
                        Instant.now().toString(),
                        Thread.currentThread().getName(),
                        value
                ));
            } catch (InterruptedException e) {
                Thread.interrupted();
                return;
            }
        };

        ScheduledExecutorService es = Executors.newScheduledThreadPool(3);

        es.scheduleAtFixedRate(taskProducer,0,100,TimeUnit.MILLISECONDS);
        es.scheduleAtFixedRate(taskProducer,0,100,TimeUnit.MILLISECONDS);
        //es.scheduleAtFixedRate(taskConsumer,0,100,TimeUnit.MILLISECONDS);
        es.scheduleAtFixedRate(taskConsumer,0,100,TimeUnit.MILLISECONDS);

        while (System.currentTimeMillis()-start < 50_000){
            try {
                Thread.sleep(500);
                System.out.println("%s - CHECKING Run Schedule with Queue Size %d in Thread %s".formatted(
                        Instant.now().toString(),
                        queue.size(),
                        Thread.currentThread().getName()
                ));
            }catch (InterruptedException ie) {}
        }

        // Stop process for add new data in Queue.
        stopProcess.set(true);

        // Waiting the queue is empty.
        while (!queue.isEmpty()){
            try {
                Thread.sleep(500);
                System.out.println("%s - CHECKING Run Schedule with Queue Size %d in Thread %s".formatted(
                        Instant.now().toString(),
                        queue.size(),
                        Thread.currentThread().getName()
                ));
            }catch (InterruptedException ie) {}
        }
        // close all process.
        es.shutdown();
        es.close();

    }

    private void simulateProcess() {
        try {
            Thread.sleep(100 + new Random().nextInt(400));
        } catch (InterruptedException ie) {
        }
    }

}
