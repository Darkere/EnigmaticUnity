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
        private EnumMap<Type, ForgeConfigSpec.IntValue> tickInterval = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> range = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.DoubleValue> conversionRatio = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.DoubleValue> auraBonus = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> amountPerOperation = new EnumMap<>(Type.class);
        private EnumMap<Type, ForgeConfigSpec.IntValue> auraChange = new EnumMap<>(Type.class);

        public SourceGenerator(ForgeConfigSpec.Builder builder) {
            for (Type value : Type.values()) {
                builder.push(value + " Source Generator");
                powerCapacity.put(value, builder.comment("Power Buffer size  of the " + value + " Source Generator").defineInRange(value + "_powerBuffer", value.getDefaultPowerBuffer(), 0, Integer.MAX_VALUE));
                range.put(value, builder.comment("Range the " + value + " Source Generator can get Source from").defineInRange(value + "_range", value.getDefaultRange(), 0, Integer.MAX_VALUE));
                tickInterval.put(value, builder.comment("Ticks to wait for next operation " + value + " Source Generator").defineInRange(value + "_tickinterval", value.getDefaultConversionPerTick(), 0, Integer.MAX_VALUE));
                conversionRatio.put(value, builder.comment("Conversion ratio from Source to FE for the  " + value + " Source Generator").defineInRange(value + "_conversionratio", value.getDefaultConversionRatio(), 0, Double.MAX_VALUE));
                auraBonus.put(value, builder.comment("Multiplier for the power if aura is present with the " + value + " Source Generator").defineInRange(value + "_aurabonus", value.getDefaultAuraBonus(), 0, Double.MAX_VALUE));
                amountPerOperation.put(value, builder.comment("Amount of Source to convert per Operation  " + value + " Source Generator").defineInRange(value + "_amountperopeation", value.getDefaultAmountPerOperation(), 0, Integer.MAX_VALUE));
                auraChange.put(value, builder.comment("Amount of Aura to drain/provide when " + value + " Source Generator operates").defineInRange(value + "_drain", value.getDefaultAuraChange(), 0, Integer.MAX_VALUE));
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
    }

}
