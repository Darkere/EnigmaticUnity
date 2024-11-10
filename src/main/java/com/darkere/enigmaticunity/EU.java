package com.darkere.enigmaticunity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EU.MODID)
public class EU {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "enigmaticunity";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Config SERVER_CONFIG = new Config();
    public static boolean NA_LOADED = false;

    private static final String NETWORK_VERSION = "1";

    private int ID = 0;

    public EU(IEventBus ModEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.spec);
        Registry.register(ModEventBus);
        ModEventBus.addListener(EU::register);
        ModEventBus.addListener(EU::RegisterCapabilities);
        NA_LOADED = ModList.get().isLoaded("naturesaura");
        // Register the commonSetup method for modloading
        // Register the Deferred Register to the mod event bus so blocks get registered

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

    }
    public static void send(CustomPacketPayload message, BlockPos pos, int range, Level level){
        PacketDistributor.sendToPlayersNear((ServerLevel) level,null,pos.getX(), pos.getY(), pos.getZ(), range,message);
    }
    public static void register(RegisterPayloadHandlersEvent Event) {
        var registrar = Event.registrar(EU.MODID).versioned(NETWORK_VERSION).optional();
        registrar.playToClient(ParticleMessage.TYPE,ParticleMessage.STREAM_CODEC,ParticleMessage::handle);
    }

    public static void RegisterCapabilities(RegisterCapabilitiesEvent Event)
    {
        SourceGeneratorBlockEntity.RegisterCapability(Event);
        SourceProducerBlockEntity.RegisterCapability(Event);
    }

    @SubscribeEvent
    public void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("eu").then(Commands.literal("aura").then(Commands.argument("amount", IntegerArgumentType.integer()).then(Commands.argument("maxTarget", IntegerArgumentType.integer()).executes(ctx -> {
            var source = ctx.getSource();
            var pos = BlockPos.containing(source.getPosition());
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

