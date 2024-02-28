package br.com.spedison.examplesthreads;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * The objective this class is calculate 432*555 + 3^210 + 45*127/12
 * each part this process will be done in One Thread com cyclicbarrier.
 */
public class Main_UsingCyclicBarrier {

    AtomicInteger maxRestarts = new AtomicInteger(10);
    BlockingQueue<Double> bq = new LinkedBlockingQueue<>();

    Runnable run1;
    Runnable run2;
    Runnable run3;

    ExecutorService es2;
    Double accValue = 0d;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Main_UsingCyclicBarrier run = new Main_UsingCyclicBarrier();
        run.run(args);
    }

    public void run(String[] args) throws ExecutionException, InterruptedException {

        Runnable runnableAtEnd = () -> {
            var executions = maxRestarts.decrementAndGet();
            Double value = bq.poll();
            value += bq.poll();
            value += bq.poll();
            accValue += value;
            System.out.println("Sum all itens Acc :: " + accValue + " Execution :: " + executions);
            try {
                Thread.sleep(2000);
                restart();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        final CyclicBarrier cb2 = new CyclicBarrier(3, runnableAtEnd);
        run1 = () -> {
            Double val = 0d;
            for (int i = 0; i < 555; i++) val += 432.0;
            bq.add(val);
            await(cb2);
            System.out.println("The result is ready.");
        };
        run2 = () -> {
            Double val = 1.;
            for (int i = 0; i < 210; i++) val *= 3.0;
            bq.add(val);
            await(cb2);
            System.out.println("The result is ready.");
        };
        run3 = () -> {
            Double val = 0.;
            for (int i = 0; i < 45; i++) val += 127d / 12d;
            bq.add(val);
            await(cb2);
            System.out.println("The result is ready.");
        };

        es2 = Executors.newFixedThreadPool(3);
        es2.execute(run1);
        es2.execute(run2);
        es2.execute(run3);
    }

    private void restart() throws InterruptedException {

        int val = maxRestarts.get();

        if (val > 0) {
            es2.execute(run1);
            es2.execute(run2);
            es2.execute(run3);
        } else {
            es2.shutdown();
            es2.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private static void await(CyclicBarrier cb2) {
        try {
            cb2.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
