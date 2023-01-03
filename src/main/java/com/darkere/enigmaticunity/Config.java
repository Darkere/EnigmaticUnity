package com.darkere.enigmaticunity;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;

public class Config {

    public ForgeConfigSpec spec;
    public SourceGenerator sourceGenerator;

    public Config() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        sourceGenerator = new SourceGenerator(builder);
        spec = builder.build();
    }

    static Config get() {
        return EU.SERVER_CONFIG;
    }

    static class SourceGenerator {


        private EnumMap<Type, ForgeConfigSpec.IntValue> powerCapacity = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> maxTransfer = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> tickInterval = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> range = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.DoubleValue> conversionRatio = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.DoubleValue> auraBonus = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> amountPerOperation = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> auraChange = new EnumMap<>(Type.class);

        public SourceGenerator(ForgeConfigSpec.Builder builder) {
            for (Type value : Type.values()) {
                builder.push(value.getFName() + " type");
                powerCapacity.put(value, builder.comment("Power Buffer size  of the " + value + " type").defineInRange("powerBuffer", value.getDefaultPowerBuffer(), 0, Integer.MAX_VALUE));
                maxTransfer.put(value, builder.comment("Max amount of power transfer for the " + value + " type per tick").defineInRange("maxPowertransfer", value.getDefaultMaxTransfer(), 0, Integer.MAX_VALUE));
                range.put(value, builder.comment("Range the " + value + " type can connect to Source containers").defineInRange("range", value.getDefaultRange(), 0, Integer.MAX_VALUE));
                tickInterval.put(value, builder.comment("Ticks to wait for next operation for the " + value + " type").defineInRange("tickinterval", value.getDefaultConversionPerTick(), 0, Integer.MAX_VALUE));
                conversionRatio.put(value, builder.comment("Conversion ratio from Source to FE for the  " + value + " Source Generator").defineInRange("conversionratio", value.getDefaultConversionRatio(), 0, Double.MAX_VALUE));
                auraBonus.put(value, builder.comment("Aura multiplier for the " + value + " type").defineInRange("aurabonus", value.getDefaultAuraBonus(), 0, Double.MAX_VALUE));
                amountPerOperation.put(value, builder.comment("Amount of Source to convert per Operation with the " + value + " type").defineInRange("amountperoperation", value.getDefaultAmountPerOperation(), 0, Integer.MAX_VALUE));
                auraChange.put(value, builder.comment("Amount of Aura to drain/provide when the" + value + " type operates").defineInRange("change", value.getDefaultAuraChange(), 0, Integer.MAX_VALUE));
                builder.pop();
            }
        }

        public int getManaGeneratorPowerBuffer(Type type) {
            return powerCapacity.get(type).get();
        }

        public double getSourceConversionRatio(Type type) {
            return conversionRatio.get(type).get();
        }

        public int getTickInterval(Type type) {
            return tickInterval.get(type).get();
        }

        public int getRange(Type type) {
            return range.get(type).get();
        }

        public int getAmountPerOperation(Type type) {
            return amountPerOperation.get(type).get();
        }

        public int getAuraChange(Type type) {
            return auraChange.get(type).get();
        }

        public double getAuraBonus(Type type) {
            return auraBonus.get(type).get();
        }

        public int getMaxTransfer(Type type) {
            return maxTransfer.get(type).get();
        }
    }

}
