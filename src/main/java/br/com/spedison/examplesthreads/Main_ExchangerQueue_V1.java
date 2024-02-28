package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.*;

public class Main_ExchangerQueue_V1 {
    public static void main(String[] args) {
        new Main_ExchangerQueue_V1().run(args);
    }

    Exchanger<String> exchanger = new Exchanger<>();

    private Runnable task1 = () -> {
        var name = Thread.currentThread().getName();
        try {
            //Thread.sleep(500+new Random().nextInt(1000));
            String messageSent = "Send mesage to other Thread from " + name;
            String messageReceive = exchanger.exchange(messageSent);
            System.out.println("%s - In thread %s on Task1 :: Send = <<%s>> and Receive <<%s>>".
                    formatted(
                            Instant.now().toString(),
                            name,
                            messageSent, messageReceive));
            // You can use offer too using timeout parameters
            // queue.offer("Send mesage to other Thread from " + name, 1500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
        }
        System.out.println(Instant.now() + " - Send message to other Thread in " + name + ".");
    };

    private Runnable task2 = () -> {
        var name = Thread.currentThread().getName();
        try {
            String messageSent = "This message is build by thread " + name ;
            String receive = exchanger.exchange(messageSent);
            System.out.println("%s - The thread %s on task2 Sent <<%s>> and receive <<%s>>".formatted(
                    Instant.now().toString(),
                    name,
                    messageSent,
                    receive
            ));
        } catch (InterruptedException ie) {
        }
    };

    private void run(String[] args) {

        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 6; i++) {
                executor.execute(task2);
                executor.execute(task1);
            }
        }

    }


}
