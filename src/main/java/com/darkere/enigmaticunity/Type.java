package com.darkere.enigmaticunity;

public enum Type {
    MANA  (10000,1000,10,10),
    NATURA(100000,10000,100,10),
    GAIA  (1000000,100000,1000,10);

    int capacity,powerBuffer, conversionPerTick;
    float conversion;

    Type(int capacity, int powerBuffer, int conversionPerTick, float conversion) {
        this.capacity = capacity;
        this.powerBuffer = powerBuffer;
        this.conversionPerTick = conversionPerTick;
        this.conversion = conversion;
    }
}
