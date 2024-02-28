package br.com.spedison.examplesthreads;

import java.util.concurrent.*;

/***
 * The objective this class is calculate 432*555 + 3^210 + 45*127/12
 * each part this process will be done in One Thread com cyclicbarrier.
 */
public class Main_UsingBarrier {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CyclicBarrier cb = new CyclicBarrier(3);
        Callable<Double> call1 = () -> {
            Double val = 0d;
            for (int i = 0; i < 555; i++) val += 432.0;
            cb.await();
            System.out.println("The result is ready.");
            return val;
        };
        Callable<Double> call2 = () -> {
            Double val = 1.;
            for (int i = 0; i < 210; i++) val *= 3.0;
            cb.await();
            System.out.println("The result is ready.");
            return val;
        };
        Callable<Double> call3 = () -> {
            Double val = 0.;
            for (int i = 0; i < 45; i++) val += 127d / 12d;
            cb.await();
            System.out.println("The result is ready.");
            return val;
        };
        ExecutorService es = Executors.newFixedThreadPool(4);
        Future<Double> fP1 = es.submit(call1);
        Future<Double> fP2 = es.submit(call2);
        Future<Double> fP3 = es.submit(call3);
        double result = fP1.get() + fP2.get() + fP3.get();
        System.out.println("The result is :: " + result);
        es.shutdown();


        /// Using BlockingQueue
        BlockingQueue<Double> bq = new LinkedBlockingQueue<>();
        Runnable runnableAtEnd = ()->{
            Double value = bq.poll();
            value += bq.poll();
            value += bq.poll();
            System.out.println("Sum all itens :: " + value);
        };
        final CyclicBarrier cb2 = new CyclicBarrier(3,runnableAtEnd);
        Runnable run1 = () -> {
            Double val = 0d;
            for (int i = 0; i < 555; i++) val += 432.0;
            bq.add(val);
            await(cb2);
            System.out.println("The result is ready.");
        };
        Runnable run2 = () -> {
            Double val = 1.;
            for (int i = 0; i < 210; i++) val *= 3.0;
            bq.add(val);
            await(cb2);
            System.out.println("The result is ready.");
        };
        Runnable run3 = () -> {
            Double val = 0.;
            for (int i = 0; i < 45; i++) val += 127d / 12d;
            bq.add(val);
            await(cb2);
            System.out.println("The result is ready.");
        };

        ExecutorService es2 = Executors.newFixedThreadPool(5);
        es2.execute(run1);
        es2.execute(run2);
        es2.execute(run3);
        //es2.execute(runnableAtEnd);
        es2.shutdown();
        es2.awaitTermination(10,TimeUnit.SECONDS);
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
