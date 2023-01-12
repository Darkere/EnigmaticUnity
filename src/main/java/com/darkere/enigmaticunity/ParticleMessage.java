package com.darkere.enigmaticunity;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticleMessage {

    Vector3d location;
    boolean generate;
    Direction dir;

    public ParticleMessage(Vector3d location, boolean generate, Direction dir) {
        this.location = location;
        this.generate = generate;
        this.dir = dir;
    }

    public static void encode(ParticleMessage msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.location.x);
        buf.writeDouble(msg.location.y);
        buf.writeDouble(msg.location.z);
        buf.writeBoolean(msg.generate);
        buf.writeBlockPos(new BlockPos(msg.dir.getNormal()));
    }

    public static ParticleMessage decode(FriendlyByteBuf buf) {
        return new ParticleMessage(new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readBoolean(), Direction.fromNormal(buf.readBlockPos()));
    }

    public static void handle(ParticleMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {

        contextSupplier.get().enqueueWork(() -> {

            int max = (int) (20 + 10 * (Math.random() * (Math.random() > 0.5f ? -1 : 1)));
            var vector = new Vector3f(0.5f, 0.5f, 0.5f);
            for (int i = 0; i < max; i++) {
                if (msg.generate) {
                    Vector3f scatter = new Vector3f((float) Math.random() * 0.02F * (Math.random() > 0.5f ? -1 : 1), (float) Math.random() * 0.04F + 0.02F, (float) Math.random() * 0.02F * (Math.random() > 0.5f ? -1 : 1));
                    scatter.transform(msg.dir.getRotation());
                    NaturesAuraAPI.instance().spawnMagicParticle(
                        msg.location.x + vector.x(),
                        msg.location.y + vector.y(),
                        msg.location.z + vector.z(),
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
                    vec.transform(msg.dir.getRotation());
                    NaturesAuraAPI.instance().spawnMagicParticle(
                        msg.location.x + 20 * vec.x() + vector.x(),
                        msg.location.y + 20 * vec.y() + vector.y(),
                        msg.location.z + 20 * vec.z() + vector.z(),
                        -vec.x(),
                        -vec.y(),
                        -vec.z(),
                        0x5ccc30,
                        (float) (Math.random() * 2),
                        18, 0, false, false);
                }
            }
        });
    }
}
