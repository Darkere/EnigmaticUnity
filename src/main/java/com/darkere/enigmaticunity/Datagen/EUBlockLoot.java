package com.darkere.enigmaticunity.Datagen;

import com.darkere.enigmaticunity.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class EUBlockLoot extends BlockLootSubProvider {

    public EUBlockLoot(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS,provider);
    }

    @Override
    protected void generate() {
        for (DeferredHolder<Block, ? extends Block> entry : Registry.BLOCKS.getEntries()) {
            dropSelf(entry.get());
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registry.BLOCKS.getEntries().stream().map(e -> (Block) e.get()).toList();
    }
}
