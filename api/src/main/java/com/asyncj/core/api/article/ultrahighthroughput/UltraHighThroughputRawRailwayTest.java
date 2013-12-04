package com.asyncj.core.api.article.ultrahighthroughput;

import java.util.Locale;

/**
 * User: APOPOV
 * Date: 15.10.13
 */
public class UltraHighThroughputRawRailwayTest {

    long k = 0;

    public static void main(String[] args) {
        new UltraHighThroughputRawRailwayTest();
    }

    public UltraHighThroughputRawRailwayTest() {
        Locale.setDefault(Locale.US);
        final int trainCapacity = 2 * 1024;
        final int trainCount = findNextPositivePowerOfTwo(2);
        final int stationCount = 2;
        final RailWay railWay = new RailWay(stationCount, trainCapacity, trainCount);

        final long n = 20L * 1000 * 1000 * 1000;

        new Thread() {
            @Override
            public void run() {

                long trainIndex = 0;
                final int mask = trainCount - 1;
                while (true) {
                    final int trainNo = (int) (trainIndex & mask);
                    final Train train = railWay.waitTrainOnStation(trainNo, 1);

                    int i = 0;
                    final int count = train.getCapacity();
                    while (i < count) {
                        long j = train.getGoods(i);
                        i++;
                    }
                    railWay.sendTrain(trainNo);

                    trainIndex++;
                }
            }
        }.start();


        int trainIndex = 0;
        long i = 0;
        final int mask = trainCount - 1;
        final long start = System.nanoTime();

        while (i < n) {
            final int trainNo = (trainIndex) & mask;
            final Train train = railWay.waitTrainOnStation(trainNo, 0);
            final int capacity = train.getCapacity();
            int j = 0;
            while (j < capacity) {
                train.addGoods(i++);
                j++;
            }

            railWay.sendTrain(trainNo);

            trainIndex++;

            if (trainIndex % 1000000 == 0) {
                final long duration = System.nanoTime() - start;
                final long ops = (i * 1000L * 1000L * 1000L) / duration;
                System.out.format("ops/sec    = %,d\n", ops);
                System.out.format("latency ns = %.3f%n", duration / (float) (i) * (float) trainCapacity);
            }
        }
    }


    public static int findNextPositivePowerOfTwo(final int value) {
        return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }

}
