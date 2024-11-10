package com.darkere.enigmaticunity;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.EnumMap;

public class Config {

    public ModConfigSpec spec;
    public SourceGenerator sourceGenerator;
    public ModConfigSpec.DoubleValue auraConversion;
    public ModConfigSpec.DoubleValue sourceConversion;

    public Config() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        sourceGenerator = new SourceGenerator(builder);
        auraConversion = builder.comment("Conversion multiplier from Aura to Power").defineInRange("auraConversion", 10, Double.MIN_VALUE, Double.MAX_VALUE);
        sourceConversion = builder.comment("Conversion multiplier from Source to Power").defineInRange("sourceConversion", 10, Double.MIN_VALUE, Double.MAX_VALUE);
        spec = builder.build();
    }

    static Config get() {
        return EU.SERVER_CONFIG;
    }

    public double getAuraConversion() {
        return auraConversion.get();
    }
    public double getSourceConversion() {
        return sourceConversion.get();
    }
    static class SourceGenerator {


        private EnumMap<EUBlockType, ModConfigSpec.IntValue> powerCapacity = new EnumMap<>(EUBlockType.class);
        private EnumMap<EUBlockType, ModConfigSpec.IntValue> maxTransfer = new EnumMap<>(EUBlockType.class);
        private EnumMap<EUBlockType, ModConfigSpec.IntValue> tickInterval = new EnumMap<>(EUBlockType.class);
        private EnumMap<EUBlockType, ModConfigSpec.IntValue> range = new EnumMap<>(EUBlockType.class);
        private EnumMap<EUBlockType, ModConfigSpec.DoubleValue> conversionRatio = new EnumMap<>(EUBlockType.class);
        private EnumMap<EUBlockType, ModConfigSpec.IntValue> amountPerOperation = new EnumMap<>(EUBlockType.class);
        private EnumMap<EUBlockType, ModConfigSpec.IntValue> auraChange = new EnumMap<>(EUBlockType.class);

        public SourceGenerator(ModConfigSpec.Builder builder) {
            for (EUBlockType value : EUBlockType.values()) {
                builder.push(value.getFName() + " type");
                powerCapacity.put(value, builder.comment("Power Buffer size  of the " + value.getFName() + " type").defineInRange("powerBuffer", value.getDefaultPowerBuffer(), 0, Integer.MAX_VALUE));
                maxTransfer.put(value, builder.comment("Max amount of power transfer for the " + value.getFName() + " type per tick").defineInRange("maxPowertransfer", value.getDefaultMaxTransfer(), 0, Integer.MAX_VALUE));
                range.put(value, builder.comment("Range the " + value.getFName() + " type can connect to Source containers").defineInRange("range", value.getDefaultRange(), 0, Integer.MAX_VALUE));
                tickInterval.put(value, builder.comment("Ticks to wait for next operation for the " + value + " type").defineInRange("tickinterval", value.getDefaultConversionPerTick(), 0, Integer.MAX_VALUE));
                amountPerOperation.put(value, builder.comment("Amount of Source to convert per Operation with the " + value.getFName() + " type").defineInRange("amountperoperation", value.getDefaultAmountPerOperation(), 0, Integer.MAX_VALUE));
                auraChange.put(value, builder.comment("Amount of Aura to drain/provide when the" + value.getFName() + " type operates").defineInRange("change", value.getDefaultAuraChange(), 0, Integer.MAX_VALUE));
                builder.pop();
            }
        }

        public int getManaGeneratorPowerBuffer(EUBlockType EUBlockType) {
            return powerCapacity.get(EUBlockType).get();
        }

        public int getTickInterval(EUBlockType EUBlockType) {
            return tickInterval.get(EUBlockType).get();
        }

        public int getRange(EUBlockType EUBlockType) {
            return range.get(EUBlockType).get();
        }

        public int getAmountPerOperation(EUBlockType EUBlockType) {
            return amountPerOperation.get(EUBlockType).get();
        }

        public int getAuraChange(EUBlockType EUBlockType) {
            return auraChange.get(EUBlockType).get();
        }

        public int getMaxTransfer(EUBlockType EUBlockType) {
            return maxTransfer.get(EUBlockType).get();
        }
    }

}
