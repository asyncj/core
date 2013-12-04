package com.asyncj.core.api.article.ultrahighthroughput;

/**
 * User: APOPOV
 * Date: 15.10.13
 */
public class Train {

    public static int CAPACITY = 2*1024;

    private final long[] goodsArray;

    private int index;

    public Train(int capacity) {
        CAPACITY = capacity;
        goodsArray = new long[capacity];
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public void addGoods(long i) {
        goodsArray[index++] = i;
    }

    public int goodsCount() {
        return index;
    }

    public long getGoods(int i) {
        index--;
        return goodsArray[i];
    }

}
