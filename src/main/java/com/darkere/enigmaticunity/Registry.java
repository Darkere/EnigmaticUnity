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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Locale;
import java.util.function.Supplier;

public class Registry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EU.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EU.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EU.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EU.MODID);

    public static Supplier<BlockEntityType<SourceGeneratorBlockEntity>> sourceGeneratorBlockEntityType;
    public static final EnumMap<EUBlockType, DeferredBlock<SourceGeneratorBlock>> sourceGeneratorBlocks = new EnumMap<>(EUBlockType.class);
    public static final EnumMap<EUBlockType, DeferredItem<BlockItem>> sourceGeneratorItems = new EnumMap<>(EUBlockType.class);

    public static Supplier<BlockEntityType<SourceProducerBlockEntity>> sourceProducerBlockEntityType ;
    public static final EnumMap<EUBlockType, DeferredBlock<SourceProducerBlock>> sourceProducerBlocks = new EnumMap<>(EUBlockType.class);
    public static final EnumMap<EUBlockType, DeferredItem<BlockItem>> sourceProducerItems = new EnumMap<>(EUBlockType.class);
    public static final Supplier<CreativeModeTab> EU_CREATIVE_TAB = CREATIVE_MODE_TABS.register("enigmaticunity", () -> CreativeModeTab.builder()
            //Set the title of the tab. Don't forget to add a translation!
            .title(Component.translatable("itemGroup." + EU.MODID))
            //Set the icon of the tab.
            .icon(() -> new ItemStack(sourceProducerItems.get(EUBlockType.IRIDESCENT).get()))
            //Add your items to the tab.
            .displayItems((params, output) -> {
                sourceGeneratorBlocks.forEach((type,deferred)-> output.accept(deferred.get()));
                sourceProducerBlocks.forEach((type,deferred)-> output.accept(deferred.get()));
            })
            .build()
    );

    static {
        for (EUBlockType EUBlockType : EUBlockType.values()) {
            String name = EUBlockType.toString().toLowerCase(Locale.ROOT) + "_source_generator";
            var block = BLOCKS.register(name, () -> new SourceGeneratorBlock(BlockBehaviour.Properties.of().strength(1), EUBlockType));
            var item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
            sourceGeneratorBlocks.put(EUBlockType, block);
            sourceGeneratorItems.put(EUBlockType, item);

            String sourceProducerName = EUBlockType.toString().toLowerCase(Locale.ROOT) + "_source_producer";
            var spblock = BLOCKS.register(sourceProducerName, () -> new SourceProducerBlock(BlockBehaviour.Properties.of().strength(1), EUBlockType));
            var spitem = ITEMS.register(sourceProducerName, () -> new BlockItem(spblock.get(), new Item.Properties()));
            sourceProducerBlocks.put(EUBlockType, spblock);
            sourceProducerItems.put(EUBlockType, spitem);

        }
        sourceGeneratorBlockEntityType = BLOCK_ENTITY_TYPES.register("source_generator",
            () -> BlockEntityType.Builder.of(SourceGeneratorBlockEntity::new, sourceGeneratorBlocks.values().stream().map(DeferredBlock::get).toArray(Block[]::new)).build(null));
        sourceProducerBlockEntityType = BLOCK_ENTITY_TYPES.register("source_producer",
                () -> BlockEntityType.Builder.of(SourceProducerBlockEntity::new, sourceProducerBlocks.values().stream().map(DeferredBlock::get).toArray(Block[]::new)).build(null));
    }
   static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
