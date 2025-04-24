package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = EU.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        var gen    = event.getGenerator();
        var output = gen.getPackOutput();

        if (event.includeServer()) {
            gen.addProvider(true, new EULanguageProvider(output));
            gen.addProvider(true, new EULootTables(output));
            gen.addProvider(true, new EURecipes(output));
        }
    }

    static class EULanguageProvider extends LanguageProvider {
        public EULanguageProvider(PackOutput output) {
            super(output, EU.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            Registry.sourceProducerBlocks.forEach((type, blockRO) -> {
                add(blockRO.get(), type.getFName() + " Resonating Crystal");
            });
            Registry.sourceGeneratorBlocks.forEach((type, blockRO) -> {
                add(blockRO.get(), type.getFName() + " Pulsating Crystal");
            });
            add("itemGroup." + EU.MODID, "Enigmatic Unity");
        }
    }

    static class EULootTables extends LootTableProvider {
        public EULootTables(PackOutput output) {
            super(output, Set.of(), List.of(
                    new SubProviderEntry(EUBlockLoot::new, LootContextParamSets.BLOCK)
            ));
        }
    }

    static class EUBlockLoot extends net.minecraft.data.loot.BlockLootSubProvider {
        public EUBlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            Registry.BLOCKS.getEntries().forEach(ro -> {
                this.dropSelf(ro.get());
            });
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Registry.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
        }
    }

    static class EURecipes extends RecipeProvider {
        public EURecipes(PackOutput out) {
            super(out);
        }

        @Override
        protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

            Registry.sourceGeneratorBlocks.forEach((type, blockRO) -> {
                switch (type) {
                    case DIM:
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRO.get())
                                .define('s', Items.AMETHYST_SHARD)
                                .define('w', BlockRegistry.CASCADING_LOG)
                                .define('i', ModItems.INFUSED_IRON)
                                .pattern("   ")
                                .pattern(" s ")
                                .pattern("iwi")
                                .unlockedBy("item", inventoryTrigger(
                                        ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                                .save(consumer);
                        break;

                    case BRIGHT:
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRO.get())
                                .define('w', BlockRegistry.CASCADING_LOG)
                                .define('g', ItemsRegistry.SOURCE_GEM)
                                .define('i', ModItems.TAINTED_GOLD)
                                .pattern("   ")
                                .pattern(" g ")
                                .pattern("iwi")
                                .unlockedBy("item", inventoryTrigger(
                                        ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                                .save(consumer);
                        break;

                    case IRIDESCENT:
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRO.get())
                                .define('w', BlockRegistry.CASCADING_LOG)
                                .define('g', BlockRegistry.SOURCE_GEM_BLOCK)
                                .define('i', ModItems.SKY_INGOT)
                                .pattern("   ")
                                .pattern(" g ")
                                .pattern("iwi")
                                .unlockedBy("item", inventoryTrigger(
                                        ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                                .save(consumer);
                        break;
                }
            });

            Registry.sourceProducerBlocks.forEach((type, blockRO) -> {
                switch (type) {
                    case DIM:
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRO.get())
                                .define('p', Items.GLASS_PANE)
                                .define('s', Items.AMETHYST_SHARD)
                                .define('w', BlockRegistry.VEXING_LOG)
                                .define('i', ModItems.INFUSED_IRON)
                                .pattern("ppp")
                                .pattern("psp")
                                .pattern("iwi")
                                .unlockedBy("item", inventoryTrigger(
                                        ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                                .save(consumer);
                        break;

                    case BRIGHT:
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRO.get())
                                .define('p', Items.GLASS_PANE)
                                .define('w', BlockRegistry.VEXING_LOG)
                                .define('g', ItemsRegistry.SOURCE_GEM)
                                .define('i', ModItems.TAINTED_GOLD)
                                .pattern("ppp")
                                .pattern("pgp")
                                .pattern("iwi")
                                .unlockedBy("item", inventoryTrigger(
                                        ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                                .save(consumer);
                        break;

                    case IRIDESCENT:
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRO.get())
                                .define('p', Items.GLASS_PANE)
                                .define('w', BlockRegistry.VEXING_LOG)
                                .define('g', BlockRegistry.SOURCE_GEM_BLOCK)
                                .define('i', ModItems.SKY_INGOT)
                                .pattern("ppp")
                                .pattern("pgp")
                                .pattern("iwi")
                                .unlockedBy("item", inventoryTrigger(
                                        ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                                .save(consumer);
                        break;
                }
            });
        }
    }
}