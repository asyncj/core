package com.asyncj.core.api.article.eightstations;

/**
 *
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class RailwayTest {

    public static void main(String[] args) {
        new RailwayTest().testRailWay();
    }

    public void testRailWay() {
        final int stationCount = 8;
        final int trainCount = 32;
        final int trainCapacity = 256;

        final Railway railway = new Railway(trainCount, trainCapacity, stationCount);

        final long n = 20000000000l;

        for (int i = 1; i < stationCount; i++) {

            final int stationNo = i;
            new Thread() {
                long lastValue = 0;

                @Override
                public void run() {
                    int trainIndex = 0;
                    while (lastValue < n) {
                        final int trainNo = trainIndex % trainCount;

                        Train train = railway.waitTrainOnStation(trainNo, stationNo);
                        int count = train.getCapacity() / (stationCount - 1);
                        for (int i = 0; i < count; i++) {
                            lastValue = train.getGoods(i);
                        }
                        railway.sendTrain(trainNo);

                        trainIndex++;
                    }
                }
            }.start();

        }
        final long start = System.nanoTime();

        long i = 0;
        int trainIndex = 0;
        while (i < n) {
            final int trainNo = trainIndex % trainCount;

            Train train = railway.waitTrainOnStation(trainNo, 0);
            int capacity = train.getCapacity() - train.goodsCount();

            for (int j = 0; j < capacity; j++) {
                train.addGoods((int)i++);
            }
            railway.sendTrain(trainNo);

            trainIndex++;

            if (i % 10000000 == 0) {
                final long duration = System.nanoTime() - start;

                final long ops = (i * 1000L * 1000L * 1000L) / duration;
                System.out.format("ops/sec       = %,d\n", ops);
                System.out.format("trains/sec    = %,d\n", ops / trainCapacity);
                System.out.format("latency nanos = %.1f%n\n", duration / (float)(i) * (float) trainCapacity);
            }
        }
    }
}
