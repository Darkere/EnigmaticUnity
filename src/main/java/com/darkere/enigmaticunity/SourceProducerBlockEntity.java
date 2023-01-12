package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.mojang.math.Vector3d;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceProducerBlockEntity extends BlockEntity {
    EnergyStorage power;
    LazyOptional<IEnergyStorage> powerCap = LazyOptional.of(() -> power);

    Type type = Type.DIM;
    long tick = 0;

    Direction facing;

    public SourceProducerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Registry.sourceProducerBlockEntityType.get(), p_155229_, p_155230_);
        facing = p_155230_.getValue(SourceGeneratorBlock.FACING);
    }

    public void setType(Type type) {
        this.type = type;
        power = new EnergyStorage(type.getPowerBuffer(), type.getMaxTransfer(), Integer.MAX_VALUE);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == facing || side == null)
                return powerCap.cast();

        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag pTag) {
        power.deserializeNBT(pTag.get("power"));
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("power", power.serializeNBT());
        super.saveAdditional(pTag);
    }

    public void tickServerSide() {

        if (tick++ % type.getTickInterval() == 0) {

            var targets = SourceUtil.canGiveSource(getBlockPos(), getLevel(), type.getRange());
            var target = targets.stream().filter(provider -> provider.isValid() && provider.getSource().getMaxSource() > provider.getSource().getSource()).findAny();
            facing = getLevel().getBlockState(getBlockPos()).getValue(SourceProducerBlock.FACING);
            target.ifPresentOrElse(jar -> {
                var maxInsert = jar.getSource().getMaxSource() - jar.getSource().getSource();
                if (maxInsert < type.getAmountPerOperation())
                    return;
                int powerConsumed = (int) (type.getAmountPerOperation() * Config.get().getSourceConversion());
                if (power.extractEnergy(powerConsumed, true) != powerConsumed)
                    return;
                jar.getSource().addSource(type.getAmountPerOperation());
                power.extractEnergy(powerConsumed, false);

                EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(level, getBlockPos(), jar.getCurrentPos());
                getLevel().addFreshEntity(aoeProjectile);

            }, () -> {
                int powerConsumed = (int) (type.getAuraChange() * Config.get().getAuraConversion());
                if (power.extractEnergy(powerConsumed, true) != powerConsumed)
                    return;

                if (IAuraChunk.getAuraInArea(getLevel(), getBlockPos(), 20) + type.getAuraChange() <= IAuraChunk.DEFAULT_AURA * 2) {
                    power.extractEnergy(powerConsumed, false);
                    var chunk = IAuraChunk.getAuraChunk(getLevel(), getBlockPos());
                    chunk.storeAura(getBlockPos(), type.getAuraChange(), false, false);
                    var vec = new Vector3d(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                    EU.send(new ParticleMessage(vec, true, facing), getBlockPos(), 100, getLevel());
                }
            });
        }
    }
}
