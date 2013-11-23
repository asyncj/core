package com.asyncj.core.api.article.twotrains;

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
        final Railway railway = new Railway();

        final long n = 20000000000l;

        new Thread() {
            long lastValue = 0;

            @Override
            public void run() {
                int trainIndex = 0;
                while (lastValue < n) {
                    final int trainNo = trainIndex % 2;

                    Train train = railway.waitTrainOnStation(trainNo, 1);
                    int count = train.goodsCount();
                    for (int i = 0; i < count; i++) {
                        lastValue = train.getGoods(i);
                    }
                    railway.sendTrain(trainNo);

                    trainIndex++;
                }
            }
        }.start();

        final long start = System.nanoTime();

        long i = 0;
        int trainIndex = 0;
        while (i < n) {
            final int trainNo = trainIndex % 2;

            Train train = railway.waitTrainOnStation(trainNo, 0);
            int capacity = train.getCapacity();
            for (int j = 0; j < capacity; j++) {
                train.addGoods((int)i++);
            }
            railway.sendTrain(trainNo);

            trainIndex++;

            if (i % 10000000 == 0) {
                final long duration = System.nanoTime() - start;

                final long ops = (i * 1000L * 1000L * 1000L) / duration;
                System.out.format("ops/sec       = %,d\n", ops);
                System.out.format("trains/sec    = %,d\n", ops / Train.CAPACITY);
                System.out.format("latency nanos = %.1f%n\n", duration / (float)(i) * (float) Train.CAPACITY);

            }
        }
    }
}
