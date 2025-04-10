package com.darkere.enigmaticunity;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Locale;

public class Registry {

    public static final DeferredRegister<Block>             BLOCKS  =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EU.MODID);
    public static final DeferredRegister<Item>              ITEMS   =
            DeferredRegister.create(ForgeRegistries.ITEMS, EU.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EU.MODID);
    public static final DeferredRegister<CreativeModeTab>   TABS    =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EU.MODID);   // ← vanilla key

    public static final EnumMap<Type, RegistryObject<SourceGeneratorBlock>> sourceGeneratorBlocks  = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>>            sourceGeneratorItems   = new EnumMap<>(Type.class);

    public static final EnumMap<Type, RegistryObject<SourceProducerBlock>> sourceProducerBlocks   = new EnumMap<>(Type.class);
    public static final EnumMap<Type, RegistryObject<BlockItem>>            sourceProducerItems    = new EnumMap<>(Type.class);

    public static final RegistryObject<BlockEntityType<SourceGeneratorBlockEntity>> sourceGeneratorBlockEntityType;
    public static final RegistryObject<BlockEntityType<SourceProducerBlockEntity>>  sourceProducerBlockEntityType;

    static {

        BlockBehaviour.Properties props =
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.STONE)
                        .strength(1.0F);

        for (Type type : Type.values()) {

            String genName = type.name().toLowerCase(Locale.ROOT) + "_source_generator";

            var genBlock = BLOCKS.register(genName,
                    () -> new SourceGeneratorBlock(props, type));

            var genItem  = ITEMS.register(genName,
                    () -> new BlockItem(
                            genBlock.getHolder().orElseThrow().value(),
                            new Item.Properties()));

            sourceGeneratorBlocks.put(type, genBlock);
            sourceGeneratorItems .put(type, genItem);

            String prodName = type.name().toLowerCase(Locale.ROOT) + "_source_producer";

            var prodBlock = BLOCKS.register(prodName,
                    () -> new SourceProducerBlock(props, type));

            var prodItem  = ITEMS.register(prodName,
                    () -> new BlockItem(
                            prodBlock.getHolder().orElseThrow().value(),
                            new Item.Properties()));

            sourceProducerBlocks.put(type, prodBlock);
            sourceProducerItems .put(type, prodItem);
        }

        sourceGeneratorBlockEntityType = BLOCK_ENTITY_TYPES.register(
                "source_generator",
                () -> BlockEntityType.Builder.of(
                                SourceGeneratorBlockEntity::new,
                                sourceGeneratorBlocks.values().stream()
                                        .map(ro -> ro.getHolder().orElseThrow().value())
                                        .toArray(Block[]::new))
                        .build(null));

        sourceProducerBlockEntityType = BLOCK_ENTITY_TYPES.register(
                "source_producer",
                () -> BlockEntityType.Builder.of(
                                SourceProducerBlockEntity::new,
                                sourceProducerBlocks.values().stream()
                                        .map(ro -> ro.getHolder().orElseThrow().value())
                                        .toArray(Block[]::new))
                        .build(null));

        TABS.register("main", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup." + EU.MODID))
                        .icon(() -> new ItemStack(
                                sourceGeneratorItems.get(Type.DIM)
                                        .getHolder().orElseThrow().value()))
                        .displayItems((params, output) -> {
                            sourceGeneratorItems.values()
                                    .forEach(ro -> output.accept(ro.getHolder().orElseThrow().value()));
                            sourceProducerItems.values()
                                    .forEach(ro -> output.accept(ro.getHolder().orElseThrow().value()));
                        })
                        .build());
    }

    static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS .register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        TABS  .register(bus);
    }
}