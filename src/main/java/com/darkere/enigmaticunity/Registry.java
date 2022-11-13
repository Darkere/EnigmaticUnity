package com.darkere.enigmaticunity;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Locale;

public class Registry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EU.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EU.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EU.MODID);

    public static final RegistryObject<BlockEntityType<ManaGeneratorBlockEntity>> manaGeneratorBlockEntityType;
    public static final EnumMap<Type, RegistryObject<ManaGeneratorBlock>> manaGeneratorBlocks = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>> manaGeneratorItems = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<SourceManaGeneratorBlock>> sourceManaGeneratorBlocks = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>> sourceManaGeneratorItems = new EnumMap<>(Type.class);

    static {
        for (Type type : Type.values()) {
            String name = type.toString().toLowerCase(Locale.ROOT) + "_mana_generator";
            var block = BLOCKS.register(name, () -> new ManaGeneratorBlock(BlockBehaviour.Properties.of(Material.STONE), type));
            var item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(EU.EUCreativeTab.CREATIVE_MODE_TAB)));
            manaGeneratorBlocks.put(type, block);
            manaGeneratorItems.put(type, item);
        }
        manaGeneratorBlockEntityType = BLOCK_ENTITY_TYPES.register("mana_generator",
            () -> BlockEntityType.Builder.of(ManaGeneratorBlockEntity::new, manaGeneratorBlocks.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));
        for (Type type : Type.values()) {
            String name = type.toString().toLowerCase(Locale.ROOT) + "_source_mana_generator";
            var block = BLOCKS.register(name, () -> new SourceManaGeneratorBlock(BlockBehaviour.Properties.of(Material.STONE), type));
            var item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(EU.EUCreativeTab.CREATIVE_MODE_TAB)));
            sourceManaGeneratorBlocks.put(type, block);
            sourceManaGeneratorItems.put(type, item);
        }
    }
   static void register(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
