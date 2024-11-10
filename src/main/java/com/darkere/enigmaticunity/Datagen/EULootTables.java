package com.darkere.enigmaticunity.Datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

public class EULootTables extends LootTableProvider {
    public EULootTables(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), Set.of(), List.of(new LootTableProvider.SubProviderEntry(EUBlockLoot::new, LootContextParamSets.BLOCK)), event.getLookupProvider());
    }
}
