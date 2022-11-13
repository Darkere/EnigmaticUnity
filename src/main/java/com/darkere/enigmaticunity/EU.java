package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

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
                return new ItemStack(Registry.manaGeneratorItems.get(Type.MANA).get());
            }
        };
    }
}

