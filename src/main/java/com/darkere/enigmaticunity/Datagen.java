package com.darkere.enigmaticunity;


import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EU.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen   {
    @SubscribeEvent
    public static void gather(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new EULanguageProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new EULootTables(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new EURecipes(event.getGenerator()));
    }


    static class EULanguageProvider extends LanguageProvider {
        public EULanguageProvider(DataGenerator gen) {
            super(gen, EU.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            Registry.sourceProducerBlocks.forEach((type, object) -> add(object.get(), type.getFName() + " Resonating Crystal"));
            Registry.sourceGeneratorBlocks.forEach((type, object) -> add(object.get(),type.getFName() + " Pulsating Crystal"));
            add(EU.EUCreativeTab.CREATIVE_MODE_TAB.getRecipeFolderName(),"Enigmatic Unity");
        }
    }
    static class EUBlockLoot extends BlockLoot {
        @Override
        protected void addTables() {
            for (RegistryObject<Block> entry : Registry.BLOCKS.getEntries()) {
                dropSelf(entry.get());
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Registry.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
        }
    }
    static class EULootTables extends LootTableProvider{

        public EULootTables(DataGenerator pGenerator) {
            super(pGenerator);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
            return ImmutableList.of(Pair.of(EUBlockLoot::new, LootContextParamSets.BLOCK));
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
            //
        }

        @Override
        public String getName() {
            return "EnigmaticUnity Loottables";
        }
    }
    static class EURecipes extends RecipeProvider{

        public EURecipes(DataGenerator pGenerator) {
            super(pGenerator);
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
            Registry.sourceGeneratorBlocks.forEach((type,object)->{
                switch (type){

                    case DIM -> ShapedRecipeBuilder.shaped(object.get())
                        .define('s',Items.AMETHYST_SHARD)
                        .define('w', BlockRegistry.CASCADING_LOG)
                        .define('i',ModItems.INFUSED_IRON)
                        .pattern("   ")
                        .pattern(" s ")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                        .save(pFinishedRecipeConsumer);
                    case BRIGHT -> ShapedRecipeBuilder.shaped(object.get())
                        .define('w', BlockRegistry.CASCADING_LOG)
                        .define('g', ItemsRegistry.SOURCE_GEM)
                        .define('i', ModItems.TAINTED_GOLD)
                        .pattern("   ")
                        .pattern(" g ")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(pFinishedRecipeConsumer);
                    case IRIDESCENT -> ShapedRecipeBuilder.shaped(object.get())
                        .define('w', BlockRegistry.CASCADING_LOG)
                        .define('g', BlockRegistry.SOURCE_GEM_BLOCK)
                        .define('i', ModItems.SKY_INGOT)
                        .pattern("   ")
                        .pattern(" g ")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(pFinishedRecipeConsumer);
                }
            });
            Registry.sourceProducerBlocks.forEach((type,object)->{
                switch (type){

                    case DIM -> ShapedRecipeBuilder.shaped(object.get())
                        .define('p', Items.GLASS_PANE)
                        .define('s',Items.AMETHYST_SHARD)
                        .define('w', BlockRegistry.VEXING_LOG)
                        .define('i', ModItems.INFUSED_IRON)
                        .pattern("ppp")
                        .pattern("psp")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                        .save(pFinishedRecipeConsumer);
                    case BRIGHT -> ShapedRecipeBuilder.shaped(object.get())
                        .define('p', Items.GLASS_PANE)
                        .define('w', BlockRegistry.VEXING_LOG)
                        .define('g', ItemsRegistry.SOURCE_GEM)
                        .define('i', ModItems.TAINTED_GOLD)
                        .pattern("ppp")
                        .pattern("pgp")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(pFinishedRecipeConsumer);
                    case IRIDESCENT -> ShapedRecipeBuilder.shaped(object.get())
                        .define('p', Items.GLASS_PANE)
                        .define('w', BlockRegistry.VEXING_LOG)
                        .define('g', BlockRegistry.SOURCE_GEM_BLOCK)
                        .define('i',ModItems.SKY_INGOT)
                        .pattern("ppp")
                        .pattern("pgp")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(pFinishedRecipeConsumer);
                }
            });
        }
    }
}
