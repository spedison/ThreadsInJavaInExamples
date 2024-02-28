package br.com.spedison.examplesthreads;


import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MySimpleTask implements Runnable {

    static private AtomicInteger countExecution = new AtomicInteger(0);
    private final String message;

    private long start;
    private long end;

    public MySimpleTask(long start, long end, String message) {
        this.start = start;
        this.end = end;
        this.message = message;
    }

    @Override
    public void run() {
        long pos = start;
        while (pos < end) {
            pos++;
        }
        System.out.println((new Date()).toString() + " - End of Task " + Thread.currentThread().getName() + " - Execution " + countExecution.incrementAndGet() + " MyMessage = " + this.message);
    }
}

public class MainScheduleThreads {

    public static void main(String[] args) throws InterruptedException {

        Runnable myTask = new MySimpleTask(0,120_000_000,"Message after 10 seconds.");
        Runnable myTask1 = new MySimpleTask(0,1_000,"Message for each 1 second.");

        ScheduledExecutorService simpleScheduler = Executors.newScheduledThreadPool(2);
        simpleScheduler.schedule(myTask,10, TimeUnit.SECONDS);
        simpleScheduler.scheduleAtFixedRate(myTask1,0,1,TimeUnit.SECONDS);
        Thread.sleep(10_000);
        simpleScheduler.shutdown();
        simpleScheduler.awaitTermination(40,TimeUnit.SECONDS);

        ScheduledExecutorService schedulerUsingCallable = Executors.newScheduledThreadPool(3);
        Callable<Object> myTaskCallable = Executors.callable(myTask);
        ScheduledFuture<Object> future = schedulerUsingCallable.schedule(myTaskCallable,10,TimeUnit.SECONDS);
        while (!future.isDone()){
            Thread.yield();
            if(System.currentTimeMillis() % 950 == 0){
                System.out.println((new Date())+" - Waiting ...");
                Thread.sleep(10);
            }
        }
        schedulerUsingCallable.shutdown();


    }



}
