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

    public static final RegistryObject<BlockEntityType<SourceGeneratorBlockEntity>> sourceGeneratorBlockEntityType;
    public static final EnumMap<Type, RegistryObject<SourceGeneratorBlock>> sourceGeneratorBlocks = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>> sourceGeneratorItems = new EnumMap<>(Type.class);

    public static final RegistryObject<BlockEntityType<SourceProducerBlockEntity>> sourceProducerBlockEntityType;
    public static final EnumMap<Type, RegistryObject<SourceProducerBlock>> sourceProducerBlocks = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>> sourceProducerItems = new EnumMap<>(Type.class);


    static {
        for (Type type : Type.values()) {
            String name = type.toString().toLowerCase(Locale.ROOT) + "_source_generator";
            var block = BLOCKS.register(name, () -> new SourceGeneratorBlock(BlockBehaviour.Properties.of(Material.STONE), type));
            var item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(EU.EUCreativeTab.CREATIVE_MODE_TAB)));
            sourceGeneratorBlocks.put(type, block);
            sourceGeneratorItems.put(type, item);

            String sourceProducerName = type.toString().toLowerCase(Locale.ROOT) + "_source_producer";
            var spblock = BLOCKS.register(sourceProducerName, () -> new SourceProducerBlock(BlockBehaviour.Properties.of(Material.STONE), type));
            var spitem = ITEMS.register(sourceProducerName, () -> new BlockItem(spblock.get(), new Item.Properties().tab(EU.EUCreativeTab.CREATIVE_MODE_TAB)));
            sourceProducerBlocks.put(type, spblock);
            sourceProducerItems.put(type, spitem);

        }
        sourceGeneratorBlockEntityType = BLOCK_ENTITY_TYPES.register("source_generator",
            () -> BlockEntityType.Builder.of(SourceGeneratorBlockEntity::new, sourceGeneratorBlocks.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));
        sourceProducerBlockEntityType = BLOCK_ENTITY_TYPES.register("source_producer",
                () -> BlockEntityType.Builder.of(SourceProducerBlockEntity::new, sourceProducerBlocks.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));
    }
   static void register(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
