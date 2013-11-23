package com.asyncj.core.api.article.onetrain;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class Railway {

    private final int stationCount = 2;
    private final Train train = new Train();
    private final AtomicInteger stationIndex = new AtomicInteger();

    public Train waitTrainOnStation(final int stationNo) {
        while (stationIndex.get() % stationCount != stationNo) {
            Thread.yield();
        }
        return train;
    }
    public void sendTrain() {
        stationIndex.getAndIncrement();
    }
}
