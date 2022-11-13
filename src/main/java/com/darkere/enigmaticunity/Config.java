package com.darkere.enigmaticunity;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Config {

    public ForgeConfigSpec spec;
    public ManaGeneratorConfig manaGenerator;

    public Config() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        manaGenerator = new ManaGeneratorConfig(builder);
        spec = builder.build();
    }
    static Config get() {
        return EU.SERVER_CONFIG;
    }

    static class ManaGeneratorConfig {


        EnumMap<Type, ForgeConfigSpec.IntValue> manaCapacity = new EnumMap<>(Type.class);
        EnumMap<Type, ForgeConfigSpec.IntValue> powerCapacity = new EnumMap<>(Type.class);
        EnumMap<Type, ForgeConfigSpec.IntValue> conversionPerTick = new EnumMap<>(Type.class);
        EnumMap<Type, ForgeConfigSpec.DoubleValue> conversion = new EnumMap<>(Type.class);

        public ManaGeneratorConfig(ForgeConfigSpec.Builder builder) {
            for (Type value : Type.values()) {
                builder.push(value + " Mana Generator");
                manaCapacity.put(value, builder.comment("Mana Capacity of the " + value + " Mana Generator").defineInRange(value + "_manaCapacity", value.capacity, 0, Integer.MAX_VALUE));
                powerCapacity.put(value, builder.comment("Power Buffer size  of the " + value + " Mana Generator").defineInRange(value + "_powerBuffer", value.powerBuffer, 0, Integer.MAX_VALUE));
                conversion.put(value, builder.comment("Conversion ration from Mana to FE for the  " + value + " Mana Generator").defineInRange(value + "_conversionratio", value.conversion, 0, Integer.MAX_VALUE));
                conversionPerTick.put(value, builder.comment("Amount of Mana to convert per tick for the " + value + " Mana Generator").defineInRange(value + "_conversionpertick", value.conversionPerTick, 0, Integer.MAX_VALUE));
                builder.pop();
            }
        }

        public int getManaGeneratorCapacity(Type type) {
            return manaCapacity.get(type).get();
        }

        public int getManaGeneratorPowerBuffer(Type type) {
            return powerCapacity.get(type).get();
        }
        public double getManaGeneratorConversion(Type type) {
            return conversion.get(type).get();
        }
        public int getManaGeneratorConversionPerTick(Type type) {
            return conversionPerTick.get(type).get();
        }

    }

}
