package br.com.spedison.examplesthreads;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Main_Using_CompletableFuture {
    public static void main(String[] args) {
        new Main_Using_CompletableFuture().run(args);
    }

    private void run(String[] args) {

        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException ie) {
            }
            return new Random().nextInt(1_000_000);
        });

        CompletableFuture<String> cfs = cf.thenApply(i -> "Value = %d".formatted(i));

        CompletableFuture<Void> cfv = cfs.thenAccept(s -> System.out.println(Instant.now().toString() + " - " + s));

        while (!cfv.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            System.out.println("Waiting...");
        }

    }
}
