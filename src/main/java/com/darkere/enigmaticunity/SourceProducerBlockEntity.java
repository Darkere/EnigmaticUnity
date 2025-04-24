package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
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

public class SourceProducerBlockEntity extends BlockEntity {
    private final EnergyStorage power;
    private final LazyOptional<IEnergyStorage> powerCap;

    private final Type type;
    private long tick = 0;
    private Direction facing;

    public SourceProducerBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.sourceProducerBlockEntityType.get(), pos, state);
        this.facing   = state.getValue(SourceGeneratorBlock.FACING);
        this.type     = ((SourceProducerBlock) state.getBlock()).type;
        this.power    = new EnergyStorage(type.getPowerBuffer(), type.getMaxTransfer(), Integer.MAX_VALUE);
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

        var jarOpt = SourceUtil.canGiveSource(getBlockPos(), getLevel(), type.getRange())
                .stream()
                .filter(p -> p.isValid()
                        && p.getSource().getMaxSource() > p.getSource().getSource())
                .findAny();

        jarOpt.ifPresentOrElse(jar -> {
            int give   = type.getAmountPerOperation();
            int costFE = (int)(give * Config.get().getSourceConversion());
            if (power.extractEnergy(costFE, true) == costFE) {
                jar.getSource().addSource(give);
                power.extractEnergy(costFE, false);
                getLevel().addFreshEntity(
                        new EntityFollowProjectile(level, getBlockPos(), jar.getCurrentPos()));
            }
        }, () -> {
            if (!EU.NA_LOADED) {
                return;
            }

            int auraDelta = type.getAuraChange();
            int costFE    = (int)(auraDelta * Config.get().getAuraConversion());

            if (power.extractEnergy(costFE, true) != costFE) {
                return;
            }

            BlockPos spot = IAuraChunk.getLowestSpot(
                    getLevel(), getBlockPos(), 20, getBlockPos());
            IAuraChunk chunk = IAuraChunk.getAuraChunk(getLevel(), spot);

            chunk.storeAura(spot, auraDelta);
            power.extractEnergy(costFE, false);

            Vec3 center = new Vec3(
                    getBlockPos().getX(),
                    getBlockPos().getY(),
                    getBlockPos().getZ()
            );
            EU.send(new ParticleMessage(center, true, facing),
                    getBlockPos(), 100, getLevel());
        });
    }
}
