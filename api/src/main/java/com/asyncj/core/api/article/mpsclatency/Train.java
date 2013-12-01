package com.asyncj.core.api.article.mpsclatency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class Train {

    private static int trainCapacity;
    private final long[] goodsArray;
    public int stationIndex;
    private int index;

    public Train(int trainCapacity) {
        this.trainCapacity = trainCapacity;
        goodsArray = new long[trainCapacity];
    }

    public int goodsCount() {
        return index;
    }

    public void addGoods(long i) {
        goodsArray[index++] = i;
    }

    public long getGoods(int i) {
        index--;
        return goodsArray[i];
    }

    public int getCapacity() {
        return trainCapacity;
    }

}
