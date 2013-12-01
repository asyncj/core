package com.asyncj.core.api.article.mpsclatency;

import java.util.Locale;

/**
 * <pre>
 * This test shows a case with 3 producers and 1 consumer (3P:1C).
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
 * Intel® Core™ i7-3632QM Processor (6M Cache, up to 3.20 GHz) BGA with 4 Cores
 * http://ark.intel.com/products/71670/Intel-Core-i7-3632QM-Processor-6M-Cache-up-to-3_20-GHz-BGA
 *
 * After some warming up of the CPU up to 3.20 GHz the following results achieved:
 *
 * Railway average test results
 * ops/sec       = 206,937,353
 * trains/sec    = 68,979,117
 * latency ns = 14.5
 *
 * The best result achieved:
 * ops/sec       = 296,305,364
 * trains/sec    = 98,768,454
 * latency ns = 10.1
 *
 *
 * To fill the difference the following results on the same hardware show Disruptor test for the similar configuration 3P:1C
 * https://github.com/LMAX-Exchange/disruptor/blob/master/src/perftest/java/com/lmax/disruptor/ThreePublisherToOneProcessorSequencedThroughputTest.java
 *
 * Starting Queue tests
 * Run 0, BlockingQueue=4,353,504 ops/sec
 * Run 1, BlockingQueue=4,353,504 ops/sec
 * Run 2, BlockingQueue=4,328,067 ops/sec
 * Run 3, BlockingQueue=4,316,857 ops/sec
 * Starting Disruptor tests
 * Run 0, Disruptor=11,467,889 ops/sec
 * Run 1, Disruptor=11,280,315 ops/sec
 * Run 2, Disruptor=11,286,681 ops/sec
 * Run 3, Disruptor=11,254,924 ops/sec
 *
 * Below the results of running 3P:1C test with batching (10):
 * https://github.com/LMAX-Exchange/disruptor/blob/master/src/perftest/java/com/lmax/disruptor/ThreePublisherToOneProcessorBatchThroughputTest.java
 *
 * Starting Queue tests
 * Run 0, BlockingQueue=4,546,281 ops/sec
 * Run 1, BlockingQueue=4,508,769 ops/sec
 * Run 2, BlockingQueue=4,101,386 ops/sec
 * Run 3, BlockingQueue=4,124,561 ops/sec
 * Starting Disruptor tests
 * Run 0, Disruptor=116,009,280 ops/sec
 * Run 1, Disruptor=128,205,128 ops/sec
 * Run 2, Disruptor=101,317,122 ops/sec
 * Run 3, Disruptor=98,716,683 ops/sec
 *
 * Summary
 *
 * The Best Results:
 * Railway        = 296,305,364 ops/sec
 * Disruptor      = 128,205,128 ops/sec
 * BlockingQueue  =   4,546,281 ops/sec
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
        final short trainCapacity = (stationCount - 1) ;

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

        while (i < n) {
            final int trainNo = (int) (trainIndex & mask);

            Train train = railway.waitTrainOnStation(trainNo, lastStationNo);
            int goodsCount = train.goodsCount();

            long goods;

            for (int j = 0; j < goodsCount; j++) {
                goods = train.getGoods(j);
                i++;
            }
            railway.sendTrain(trainNo);

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
