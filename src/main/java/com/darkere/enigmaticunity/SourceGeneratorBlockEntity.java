package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceGeneratorBlockEntity extends BlockEntity {
    private final EnergyStorage power;
    private final LazyOptional<IEnergyStorage> powerCap;

    private final Type type;
    private long tick = 0;
    private Direction facing;

    public SourceGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.sourceGeneratorBlockEntityType.get(), pos, state);
        this.facing   = state.getValue(SourceGeneratorBlock.FACING);
        this.type     = ((SourceGeneratorBlock) state.getBlock()).type;
        this.power    = new EnergyStorage(
                type.getPowerBuffer(),
                Integer.MAX_VALUE,
                type.getMaxTransfer());
        this.powerCap = LazyOptional.of(() -> this.power);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
            @NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY &&
                (side == facing.getOpposite() || side == null)) {
            return powerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("power")) {
            this.power.deserializeNBT(tag.get("power"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("power", this.power.serializeNBT());
    }

    public void tickServerSide() {
        if ((tick++ % type.getTickInterval()) != 0) return;

        this.facing = getLevel().getBlockState(getBlockPos())
                .getValue(SourceGeneratorBlock.FACING);

        int auraArea = EU.NA_LOADED
                ? IAuraChunk.getAuraInArea(getLevel(), getBlockPos(), 35)
                : 0;

        int sourceOp    = type.getAmountPerOperation();
        float feFromSrc = (float) (sourceOp * Config.get().getSourceConversion());
        int auraChange  = type.getAuraChange();
        float feFromAura = (float) (auraChange * Config.get().getAuraConversion());
        int totalFE     = (int)(feFromSrc + (auraArea > auraChange ? feFromAura : 0));

        boolean hasSource = SourceUtil.canTakeSource(getBlockPos(), getLevel(), type.getRange())
                .stream()
                .anyMatch(p -> p.isValid() && p.getSource().getSource() >= sourceOp);

        if (power.receiveEnergy(totalFE, true) == totalFE && hasSource) {
            if (auraArea > 0) {
                BlockPos spot = IAuraChunk.getHighestSpot(
                        getLevel(), getBlockPos(), 35, getBlockPos());
                IAuraChunk chunk = IAuraChunk.getAuraChunk(getLevel(), spot);

                int drained = chunk.drainAura(spot, auraChange);
                power.receiveEnergy(-totalFE, false);
                power.receiveEnergy(totalFE, true);

                Vec3 center = new Vec3(
                        getBlockPos().getX(),
                        getBlockPos().getY(),
                        getBlockPos().getZ()
                );
                EU.send(new ParticleMessage(center, false, facing),
                        getBlockPos(), 100, getLevel());
            }

            SourceUtil.takeSourceWithParticles(
                    getBlockPos(), getLevel(), type.getRange(), sourceOp);
            power.receiveEnergy(totalFE, false);

            var outPos = getBlockPos().relative(facing.getOpposite());
            var be = getLevel().getBlockEntity(outPos);
            if (be != null) {
                be.getCapability(ForgeCapabilities.ENERGY, facing)
                        .ifPresent(storage -> {
                            int canRecv = storage.receiveEnergy(Integer.MAX_VALUE, true);
                            int toSend  = power.extractEnergy(canRecv, true);
                            power.extractEnergy(toSend, false);
                            storage.receiveEnergy(toSend, false);
                        });
            }
        }
    }
}
