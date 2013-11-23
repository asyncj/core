package com.asyncj.core.api.article.eightstations;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class Train {

    private final long[] goodsArray;
    private int index;
    private int trainCapacity;

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
