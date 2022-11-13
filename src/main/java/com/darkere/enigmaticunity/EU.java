package com.darkere.enigmaticunity;

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
import net.minecraftforge.fml.common.Mod;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<ManaGeneratorBlockEntity>> manaGeneratorBlockEntityType;
    public static final EnumMap<Type, RegistryObject<ManaGeneratorBlock>> manaGeneratorBlocks = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>> manaGeneratorItems = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<SourceManaGeneratorBlock>> sourceManaGeneratorBlocks = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>> sourceManaGeneratorItems = new EnumMap<>(Type.class);

    static {
        for (Type type : Type.values()) {
            String name = type.toString().toLowerCase(Locale.ROOT) + "_mana_generator";
            var block = BLOCKS.register(name, () -> new ManaGeneratorBlock(BlockBehaviour.Properties.of(Material.STONE), type));
            var item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(EUCreativeTab.CREATIVE_MODE_TAB)));
            manaGeneratorBlocks.put(type, block);
            manaGeneratorItems.put(type, item);
        }
        manaGeneratorBlockEntityType = BLOCK_ENTITY_TYPES.register("mana_generator",
            () -> BlockEntityType.Builder.of(ManaGeneratorBlockEntity::new, (Block[]) manaGeneratorBlocks.values().stream().map(RegistryObject::get).toArray()).build(null));
        for (Type type : Type.values()) {
            String name = type.toString().toLowerCase(Locale.ROOT) + "_source_mana_generator";
            var block = BLOCKS.register(name, () -> new SourceManaGeneratorBlock(BlockBehaviour.Properties.of(Material.STONE), type));
            var item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(EUCreativeTab.CREATIVE_MODE_TAB)));
            sourceManaGeneratorBlocks.put(type, block);
            sourceManaGeneratorItems.put(type, item);
        }
    }

    public EU() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class EUCreativeTab {
        public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab(EU.MODID) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(manaGeneratorItems.get(Type.MANA).get());
            }
        };
    }
}

