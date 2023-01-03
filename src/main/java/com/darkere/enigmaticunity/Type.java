package com.darkere.enigmaticunity;

import com.google.common.base.CaseFormat;

public enum Type {
    DIM(1000, 100, 2, 10, 10, 2, 10, 100),
    BRIGHT(10000, 50, 5, 100, 10, 5, 50, 1000),
    IRIDESCENT(100000, 20, 10, 1000, 10, 10, 100, 10000);

    private final int powerBuffer;
    private final int tickInterval;
    private final int range;
    private final int amountPerOperation;
    private final double conversionRatio;
    private final double auraBonus;
    private final int auraChange;

    private final int maxTransfer;

    Type(int powerBuffer, int tickInterval, int range, int amountPerOperation, double conversionRatio, double auraBonus, int auraChange, int maxTransfer) {
        this.powerBuffer = powerBuffer;
        this.tickInterval = tickInterval;
        this.range = range;
        this.amountPerOperation = amountPerOperation;
        this.conversionRatio = conversionRatio;
        this.auraBonus = auraBonus;
        this.auraChange = auraChange;
        this.maxTransfer = maxTransfer;
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

    public int getDefaultAuraChange() {
        return auraChange;
    }

    public int getAuraChange() {
        return Config.get().sourceGenerator.getAuraChange(this);
    }

    public double getDefaultAuraBonus() {
        return auraBonus;
    }

    public double getAuraBonus() {
        return Config.get().sourceGenerator.getAuraBonus(this);
    }

    public String getFName() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    public int getDefaultMaxTransfer() {
        return maxTransfer;
    }

    public int getMaxTransfer() {
        return Config.get().sourceGenerator.getMaxTransfer(this);
    }
}
