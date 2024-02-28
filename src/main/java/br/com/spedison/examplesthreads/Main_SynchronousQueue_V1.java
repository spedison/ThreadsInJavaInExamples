package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class Main_SynchronousQueue_V1 {
    public static void main(String[] args) {
        new Main_SynchronousQueue_V1().run(args);
    }

    SynchronousQueue<String> queue = new SynchronousQueue<>();

    private Runnable task1 = () -> {
        var name = Thread.currentThread().getName();
        try {
            Thread.sleep(500+new Random().nextInt(1000));
            queue.put("Send mesage to other Thread from " + name);
            // You can use offer too using timeout parameters
            // queue.offer("Send mesage to other Thread from " + name, 1500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
        }
        System.out.println(Instant.now() + " - Send message to other Thread in " + name + ".");
    };

    private Runnable task2 = () -> {
        var name = Thread.currentThread().getName();
        try {
            // String value = queue.take();
            String value = queue.poll(3500, TimeUnit.MILLISECONDS) ;
            System.out.println(Instant.now() + " - Receive message from other Thread :: [[" + value + "]]  :: in " + name);
        } catch (InterruptedException ie) {
        }
    };

    private void run(String[] args) {

        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 600; i++) {
                executor.execute(task2);
                executor.execute(task1);
            }
        }

    }


}
