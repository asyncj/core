package com.asyncj.core.api.article.mpsclatency;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
@SuppressWarnings("unused")
public class Railway {

    private final int mask;
    private final Train[] train;

    public Railway(int trainCount, int trainCapacity, int stationCount) {
        mask = stationCount - 1;
        train = new Train[trainCount];
        for (int i = 0; i < trainCount; i++) {
            train[i] = new Train(trainCapacity);
        }
    }

    public Train waitTrainOnStation(final int trainNo, final int stationNo) {
        while ((train[trainNo].stationIndex & mask) != stationNo) {
            Thread.yield();
        }
        return train[trainNo];
    }

    public void sendTrain(final int trainNo) {
        train[trainNo].stationIndex++;
    }
}
