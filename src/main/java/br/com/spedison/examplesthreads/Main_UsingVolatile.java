package br.com.spedison.examplesthreads;

public class Main_UsingVolatile {


    /***
     * Observation:
     * 1) You can execute  more loops
     * 2) There are exception when using any JVMs as Correto 11,17,19
     * 3) I Use VM GraalVM CE 17.0.8+7.1 - Without volatile --- With no Errors
     *          VM GraalVM CE 17.0.9+9.1 - With    volatile --- With no Errors
     *          VM GraalVM CE 17.0.8+7.1 - With    volatile --- With no Errors
     *
     *    I Use VM Corretto-19.0.2.7.1   - Without volatile --- With Errors
     *          VM Corretto-17.0.10.7.1  - Without volatile --- With Errors
     *          VM Corretto-17.0.10.7.1  - with volatile    --- With no Errors
     *
     *    I don´t know all details, but it's verry important: There are diferences between JVMs and if your application is
     *    MultThread, you must test your application with more them one JVMs.
     */

    private static volatile int number = 150;
    private static volatile boolean isReady = false;

    static private Runnable myFunctionInThread = () -> {
        while (!isReady) {
            Thread.yield();
        }
        //System.out.println("The number is <<" + number + ">> in Thread " + Thread.currentThread().getName());
        if (number != 42)
            throw new RuntimeException("Number is not 42");
    };

    public static void main(String[] args) throws InterruptedException {
        int counter = 100_000;
        while (--counter > 0) {

            Thread t0 = new Thread(myFunctionInThread);
            t0.start();
            Thread t1 = new Thread(myFunctionInThread);
            t1.start();
            Thread t2 = new Thread(myFunctionInThread);
            t2.start();
            Thread t3 = new Thread(myFunctionInThread);
            t3.start();
            Thread t4 = new Thread(myFunctionInThread);
            t4.start();
            Thread t5 = new Thread(myFunctionInThread);
            t5.start();

            number = 42;
            isReady = true;

            while (
                    t0.getState() != Thread.State.TERMINATED
                            || t1.getState() != Thread.State.TERMINATED
                            || t2.getState() != Thread.State.TERMINATED
                            || t3.getState() != Thread.State.TERMINATED
                            || t4.getState() != Thread.State.TERMINATED
                            || t5.getState() != Thread.State.TERMINATED
            ) {
                Thread.yield();
                // Do nothing
            }

            number = 0;
            isReady = false;
        }

    }
}
