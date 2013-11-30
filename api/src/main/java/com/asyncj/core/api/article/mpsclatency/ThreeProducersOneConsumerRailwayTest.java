package com.asyncj.core.api.article.mpsclatency;

import java.util.Locale;

/**
 * <pre>
 * This test shows a case with 3 producers and 1 consumer (3p:1c).
 *
 * The railway uses 65536 trains with capacity 3.
 * Trains go sequentially from one station (thread) to another.
 *
 * +----+
 * | P1 |------+
 * +----+      |
 *             v
 * +----+    +----+
 * | P2 |--->| C1 |
 * +----+    +----+
 *             ^
 * +----+      |
 * | P3 |------+
 * +----+
 *
 * For tests the following processor has been used:
 * Intel® Core™ i7-3632QM Processor (6M Cache, up to 3.20 GHz) BGA
 * with 4 Cores
 * http://ark.intel.com/products/71670/Intel-Core-i7-3632QM-Processor-6M-Cache-up-to-3_20-GHz-BGA
 *
 * After some warming up of the CPU up to 3.20 GHz the following results achieved:
 *
 * Railway average test results
 * ops/sec       = 84,317,721
 * trains/sec    = 28,105,907
 * latency ns = 35.6
 *
 * To fill the difference the following results show Disruptor test for the similar configuration 3p:1c
 * https://github.com/LMAX-Exchange/disruptor/blob/master/src/perftest/java/com/lmax/disruptor/ThreePublisherToOneProcessorSequencedThroughputTest.java
 *
 * Starting Queue tests
 * Run 0, BlockingQueue=4,353,504 ops/sec, avg latency=229.7 ns
 * Run 1, BlockingQueue=4,353,504 ops/sec, avg latency=229.7 ns
 * Run 2, BlockingQueue=4,328,067 ops/sec, avg latency=231.1 ns
 * Run 3, BlockingQueue=4,316,857 ops/sec, avg latency=231.7 ns
 * Starting Disruptor tests
 * Run 0, Disruptor=11,467,889 ops/sec, avg latency=87.2 ns
 * Run 1, Disruptor=11,280,315 ops/sec, avg latency=88.7 ns
 * Run 2, Disruptor=11,286,681 ops/sec, avg latency=88.6 ns
 * Run 3, Disruptor=11,254,924 ops/sec, avg latency=88.8 ns
 *
 *
 * </pre>
 *
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class ThreeProducersOneConsumerRailwayTest {

    public static void main(String[] args) {
        new ThreeProducersOneConsumerRailwayTest().testRailWay();
    }

    public void testRailWay() {
        Locale.setDefault(Locale.US);
        final int stationCount = 4;
        final int trainCount = 64 * 1024;
        final int mask = trainCount - 1;
        final int lastStationNo = stationCount - 1;
        final short trainCapacity = stationCount - 1;

        final Railway railway = new Railway(trainCount, trainCapacity, stationCount);

        final long n = 20l * 1000 * 1000 * 1000;

        // starting producers
        for (int i = 0; i < lastStationNo; i++) {

            final int stationNo = i;
            new Thread() {
                long lastValue = 0;

                @Override
                public void run() {
                    long trainIndex = 0;
                    while (lastValue < n) {
                        final int trainNo = (int) (trainIndex & mask);

                        Train train = railway.waitTrainOnStation(trainNo, stationNo);
                        int count = train.getCapacity() / trainCapacity;
                        for (int i = 0; i < count; i++) {
                            train.addGoods(trainIndex);
                        }

                        railway.sendTrain(trainNo);

                        trainIndex++;
                    }
                }
            }.start();

        }
        final long start = System.nanoTime();

        long i = 0;
        long trainIndex = 0;
        long goods;

        while (i < n) {
            final int trainNo = (int) (trainIndex & mask);

            Train train = railway.waitTrainOnStation(trainNo, lastStationNo);
            int goodsCount = train.goodsCount();

            for (int j = 0; j < goodsCount; j++) {
                goods = train.getGoods(j);
            }
            railway.sendTrain(trainNo);

            i++;

            trainIndex++;

            if ((i % 100000000) == 0) {
                final long duration = System.nanoTime() - start;

                final long ops = (i * 1000L * 1000L * 1000L) / duration;
                System.out.format("ops/sec       = %,d\n", ops);
                System.out.format("trains/sec    = %,d\n", ops / trainCapacity);
                System.out.format("latency ns = %.1f%n\n", duration / (float) (i) * (float) trainCapacity);
            }
        }

    }
}
