package com.darkere.enigmaticunity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EU.MODID)
public class EU {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "enigmaticunity";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Config SERVER_CONFIG = new Config();

    public EU() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.spec);
        Registry.register();
        // Register the commonSetup method for modloading
        // Register the Deferred Register to the mod event bus so blocks get registered

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class EUCreativeTab {
        public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab(EU.MODID) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(Registry.sourceGeneratorItems.get(Type.DIM).get());
            }
        };
    }

    @SubscribeEvent
    public void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("eu").then(Commands.literal("aura").then(Commands.argument("amount", IntegerArgumentType.integer()).then(Commands.argument("maxTarget", IntegerArgumentType.integer()).executes(ctx -> {
            var source = ctx.getSource();
            var pos = new BlockPos(source.getPosition());
            var chunk = IAuraChunk.getAuraChunk(source.getLevel(), pos);
            var amount = IntegerArgumentType.getInteger(ctx, "amount");
            var max = IntegerArgumentType.getInteger(ctx, "maxTarget");
            var aura = IAuraChunk.getAuraInArea(source.getLevel(), pos, 25);
            int auraChange;
            auraChange = Math.min(amount, max - aura);

            if (amount > 0)
                chunk.storeAura(pos, auraChange, false, false);
            else
                chunk.drainAura(pos, Math.abs(auraChange), false, false);
            return Command.SINGLE_SUCCESS;
        })))));
    }
}

