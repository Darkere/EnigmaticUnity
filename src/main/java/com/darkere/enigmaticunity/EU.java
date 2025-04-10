package com.darkere.enigmaticunity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

@Mod(EU.MODID)
public class EU {

    public static final String MODID = "enigmaticunity";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Config SERVER_CONFIG = new Config();
    public static boolean NA_LOADED = false;

    private static final String NETWORK_VERSION = "1";
    private static final ResourceLocation CHANNEL_ID =
            new ResourceLocation(MODID, "network");

    private static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(CHANNEL_ID,
                    () -> NETWORK_VERSION,
                    NETWORK_VERSION::equals,
                    s -> true);

    private int nextId = 0;

    public EU() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,
                SERVER_CONFIG.spec);

        Registry.register();
        NA_LOADED = ModList.get().isLoaded("naturesaura");

        MinecraftForge.EVENT_BUS.register(this);

        CHANNEL.registerMessage(nextId++, ParticleMessage.class,
                ParticleMessage::encode,
                ParticleMessage::decode,
                ParticleMessage::handle);
    }

    public static void send(Object message, BlockPos pos, int range, Level level) {
        CHANNEL.send(PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(),
                                range, level.dimension())),
                message);
    }

    @SubscribeEvent
    public void commands(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("eu")
                        .then(Commands.literal("aura")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .then(Commands.argument("maxTarget", IntegerArgumentType.integer())
                                                .executes(ctx -> {

                                                    var source = ctx.getSource();
                                                    BlockPos pos = BlockPos.containing(source.getPosition());
                                                    IAuraChunk chunk = IAuraChunk.getAuraChunk(source.getLevel(), pos);

                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                    int max    = IntegerArgumentType.getInteger(ctx, "maxTarget");
                                                    int aura   = IAuraChunk.getAuraInArea(source.getLevel(), pos, 25);

                                                    int auraChange = Math.min(amount, max - aura);

                                                    if (amount > 0) {
                                                        chunk.storeAura(pos, auraChange, false, false);
                                                    } else {
                                                        chunk.drainAura(pos, Math.abs(auraChange), false, false);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}