package com.darkere.enigmaticunity;

public enum Type {
    DULL(1000,100,2, 10, 10),
    GLOWING(10000,50,5, 100, 10),
    SHINING(100000,20,10, 1000, 10);

    private final int powerBuffer;
    private final int tickInterval;
    private final int range;
    private final int amountPerOperation;
    private final double conversionRatio;

    Type(int powerBuffer, int tickInterval, int range, int amountPerOperation, double conversionRatio) {
        this.powerBuffer = powerBuffer;
        this.tickInterval = tickInterval;
        this.range = range;
        this.amountPerOperation = amountPerOperation;
        this.conversionRatio = conversionRatio;
    }


    public int getDefaultPowerBuffer() {
        return powerBuffer;
    }

    public int getDefaultConversionPerTick() {
        return tickInterval;
    }

    public int getDefaultRange() {
        return range;
    }

    public double getDefaultConversionRatio() {
        return conversionRatio;
    }

    public int getPowerBuffer() {
        return Config.get().sourceGenerator.getManaGeneratorPowerBuffer(this);
    }

    public int getTickInterval() {
        return Config.get().sourceGenerator.getTickInterval(this);
    }

    public int getRange() {
        return Config.get().sourceGenerator.getRange(this);
    }

    public double getConversionRatio() {
        return Config.get().sourceGenerator.getSourceConversionRatio(this);
    }

    public int getDefaultAmountPerOperation() {
        return amountPerOperation;
    }
    public int getAmountPerOperation() {
        return Config.get().sourceGenerator.getAmountPerOperation(this);
    }
}
