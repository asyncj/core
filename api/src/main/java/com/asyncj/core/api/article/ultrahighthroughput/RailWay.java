package com.asyncj.core.api.article.ultrahighthroughput;

import java.util.concurrent.atomic.AtomicLong;

/**
 * User: APOPOV
 * Date: 05.12.13
 */
public class RailWay {

    public static int TRAIN_COUNT = 8;
    private  AtomicLong[] trainNoLong;

    private final Train[] train;

    private final int capacity;
    private final int mask;

    public RailWay(final int stationCount, int trainCapacity, int trainCount) {
        TRAIN_COUNT = trainCount;
        trainNoLong = new AtomicLong[trainCount];
        train = new Train[trainCount];
        for (int i = 0; i < trainCount; i++) {
            train[i] = new Train(trainCapacity);
            trainNoLong[i] = new AtomicLong();
        }

        capacity = findNextPositivePowerOfTwo(stationCount);
        mask = capacity - 1;
    }

    public static int findNextPositivePowerOfTwo(final int value) {
        return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }

    public Train waitTrainOnStation(final int trainIndex, final int stationNo) {
        while ((trainNoLong[trainIndex].get() & mask) != stationNo) {
            Thread.yield();
        }
        return train[trainIndex];
    }

    public void sendTrain(final int index) {
        final AtomicLong atomicLong = trainNoLong[index];
        atomicLong.lazySet(atomicLong.get() + 1);
    }

}
