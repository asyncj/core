package com.asyncj.core.api.article.twotrains;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class Railway {

    private final int stationCount = 2;
    private final Train[] train = new Train[]{
            new Train(),
            new Train(),
    };

    private final AtomicInteger[] stationIndex = new AtomicInteger[]{
            new AtomicInteger(0),
            new AtomicInteger(0),
    };

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
