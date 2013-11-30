package com.asyncj.core.api.article.mpsclatency;

/**
 * @author Aliaksei Papou
 * @since 23.11.13
 */
public class Train {

    private final long[] goodsArray;
    public short stationIndex;
    private int index;

    public Train(int trainCapacity) {
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
        return goodsArray.length;
    }

}
