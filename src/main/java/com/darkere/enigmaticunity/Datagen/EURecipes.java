package com.darkere.enigmaticunity.Datagen;

import com.darkere.enigmaticunity.Registry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;

class EURecipes extends RecipeProvider {

    public EURecipes(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(),event.getLookupProvider());
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        var cat = RecipeCategory.MISC;
        Registry.sourceGeneratorBlocks.forEach((type, object) -> {
            switch (type) {
                case DIM -> ShapedRecipeBuilder.shaped(cat,object.get())
                        .define('s', Items.AMETHYST_SHARD)
                        .define('w', BlockRegistry.CASCADING_LOG)
                        .define('i', ModItems.INFUSED_IRON)
                        .pattern("   ")
                        .pattern(" s ")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                        .save(recipeOutput);
                case BRIGHT -> ShapedRecipeBuilder.shaped(cat,object.get())
                        .define('w', BlockRegistry.CASCADING_LOG)
                        .define('g', ItemsRegistry.SOURCE_GEM)
                        .define('i', ModItems.TAINTED_GOLD)
                        .pattern("   ")
                        .pattern(" g ")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(recipeOutput);
                case IRIDESCENT -> ShapedRecipeBuilder.shaped(cat,object.get())
                        .define('w', BlockRegistry.CASCADING_LOG)
                        .define('g', BlockRegistry.SOURCE_GEM_BLOCK)
                        .define('i', ModItems.SKY_INGOT)
                        .pattern("   ")
                        .pattern(" g ")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(recipeOutput);
            }
        });
        Registry.sourceProducerBlocks.forEach((type, object) -> {
            switch (type) {

                case DIM -> ShapedRecipeBuilder.shaped(cat,object.get())
                        .define('p', Items.GLASS_PANE)
                        .define('s', Items.AMETHYST_SHARD)
                        .define('w', BlockRegistry.VEXING_LOG)
                        .define('i', ModItems.INFUSED_IRON)
                        .pattern("ppp")
                        .pattern("psp")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                        .save(recipeOutput);
                case BRIGHT -> ShapedRecipeBuilder.shaped(cat,object.get())
                        .define('p', Items.GLASS_PANE)
                        .define('w', BlockRegistry.VEXING_LOG)
                        .define('g', ItemsRegistry.SOURCE_GEM)
                        .define('i', ModItems.TAINTED_GOLD)
                        .pattern("ppp")
                        .pattern("pgp")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(recipeOutput);
                case IRIDESCENT -> ShapedRecipeBuilder.shaped(cat,object.get())
                        .define('p', Items.GLASS_PANE)
                        .define('w', BlockRegistry.VEXING_LOG)
                        .define('g', BlockRegistry.SOURCE_GEM_BLOCK)
                        .define('i', ModItems.SKY_INGOT)
                        .pattern("ppp")
                        .pattern("pgp")
                        .pattern("iwi")
                        .unlockedBy("item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemsRegistry.SOURCE_GEM).build()))
                        .save(recipeOutput);
            }
        });
    }
}
