package com.asyncj.core.api.article.eightstations;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class Railway {

    private final int stationCount;
    private final Train[] train;
    private final AtomicInteger[] stationIndex;

    public Railway(int trainCount, int trainCapacity, int stationCount) {
        this.stationCount = stationCount;
        stationIndex = new AtomicInteger[trainCount];
        train = new Train[trainCount];
        for (int i = 0; i < trainCount; i++) {
            stationIndex[i] = new AtomicInteger();
            train[i] = new Train(trainCapacity);
        }
    }

    public Train waitTrainOnStation(final int trainNo, final int stationNo) {
        while (stationIndex[trainNo].get() % stationCount != stationNo) {
            Thread.yield();
        }
        return train[trainNo];
    }

    public void sendTrain(final int trainNo) {
        stationIndex[trainNo].getAndIncrement();
    }
}
