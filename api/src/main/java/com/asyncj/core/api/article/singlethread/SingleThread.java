package com.asyncj.core.api.article.singlethread;

import java.util.Locale;

/**
 * @author Aliaksei Papou
 * @since 18.11.13
 */
public class SingleThread {

    private static final long ITERATIONS = 1000L * 1000L * 50L;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        new SingleThread();
    }

    public SingleThread() {
        final long start = System.nanoTime();

        long i = 0;
        while (i < ITERATIONS) {
            i++;
        }

        final long duration = System.nanoTime() - start;

        final long ops = (ITERATIONS * 1000L * 1000L * 1000L) / duration;
        System.out.format("ops/sec    = %,d\n", ops);
        System.out.format("latency ns = %.3f%n", duration / (float)(ITERATIONS) );
    }
}
