package br.com.spedison.examplesthreads;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class Main_Using_Stream {
    public static void main(String[] args) {
        new Main_Using_Stream().run(args);
    }

    private void run(String[] args) {
        Instant start = Instant.now();

        Map<Double, Double> map = new ConcurrentHashMap<>();
        IntStream
                .range(1, 1_000_000_000)
                .parallel()
                .mapToObj(number -> new Double[]{Double.valueOf(number), Double.valueOf(Math.pow(number, 5))})
                .filter(n -> n[1] % 2 == 0)
                .forEach(n -> map.put(n[0], n[1]));
        Instant end = Instant.now();

        System.out.println("Durarion is = " + Duration.between(start,end));

        // Using parallel the time is 0.78 seconds.
        // Without parallel the time is 1.97 seconds.

    }
}
