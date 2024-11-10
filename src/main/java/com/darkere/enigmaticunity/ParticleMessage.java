package com.darkere.enigmaticunity;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

public record ParticleMessage(BlockPos Location, boolean Generate, Direction Direction) implements CustomPacketPayload {

    public static final Type<ParticleMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EU.MODID,"particlemessage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,ParticleMessage::Location,
            ByteBufCodecs.BOOL,ParticleMessage::Generate,
            net.minecraft.core.Direction.STREAM_CODEC,ParticleMessage::Direction,
            ParticleMessage::new
    );

    public static void handle(ParticleMessage msg, IPayloadContext Context) {
        int max = (int) (20 + 10 * (Math.random() * (Math.random() > 0.5f ? -1 : 1)));
        var vector = new Vector3f(0.5f, 0.5f, 0.5f);
        for (int i = 0; i < max; i++) {
            if (msg.Generate) {
                Vector3f scatter = new Vector3f((float) Math.random() * 0.02F * (Math.random() > 0.5f ? -1 : 1), (float) Math.random() * 0.04F + 0.02F, (float) Math.random() * 0.02F * (Math.random() > 0.5f ? -1 : 1));
                scatter.rotate(msg.Direction.getRotation());
                NaturesAuraAPI.instance().spawnMagicParticle(
                        msg.Location.getX() + vector.x(),
                        msg.Location.getY() + vector.y(),
                        msg.Location.getZ() + vector.z(),
                        scatter.x(),
                        scatter.y(),
                        scatter.z(),
                        0x5ccc30,
                        (float) (Math.random() * 2),
                        40, 0, false, true);
            } else {
                var x = Math.random() * 0.02F * (Math.random() > 0.5f ? -1 : 1);
                var y = Math.random() * 0.04F + 0.02F;
                var z = Math.random() * 0.02F * (Math.random() > 0.5f ? -1 : 1);
                Vector3f vec = new Vector3f((float) x, (float) y, (float) z);
                vec.rotate(msg.Direction.getRotation());
                NaturesAuraAPI.instance().spawnMagicParticle(
                        msg.Location.getX() + 20 * vec.x() + vector.x(),
                        msg.Location.getY() + 20 * vec.y() + vector.y(),
                        msg.Location.getZ() + 20 * vec.z() + vector.z(),
                        -vec.x(),
                        -vec.y(),
                        -vec.z(),
                        0x5ccc30,
                        (float) (Math.random() * 2),
                        18, 0, false, false);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
