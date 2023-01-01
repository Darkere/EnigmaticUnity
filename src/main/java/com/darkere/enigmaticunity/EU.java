package com.darkere.enigmaticunity;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EU.MODID)
public class EU {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "enigmaticunity";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Config SERVER_CONFIG = new Config();
    public EU() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,SERVER_CONFIG.spec);
        Registry.register();
        // Register the commonSetup method for modloading
        // Register the Deferred Register to the mod event bus so blocks get registered

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class EUCreativeTab {
        public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab(EU.MODID) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(Registry.sourceGeneratorItems.get(Type.DULL).get());
            }
        };
    }
}

