package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
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

public class SourceGeneratorBlockEntity extends BlockEntity {
    EnergyStorage power;
    LazyOptional<IEnergyStorage> powerCap = LazyOptional.of(() -> power);

    Type type = Type.DULL;
    long tick = 0;

    public SourceGeneratorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Registry.sourceGeneratorBlockEntityType.get(), p_155229_, p_155230_);

    }

    public void setType(Type type) {
        this.type = type;
        power = new EnergyStorage(type.getPowerBuffer());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY)
            return powerCap.cast();
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
            int powerToProduce = (int) (type.getAmountPerOperation() * type.getConversionRatio());
            if (power.receiveEnergy(powerToProduce, true) > 0
                    && SourceUtil.canTakeSource(getBlockPos(), getLevel(), type.getRange()).stream().anyMatch(provider -> provider.isValid() && provider.getSource().getSource() >= type.getAmountPerOperation())) {
                SourceUtil.takeSourceWithParticles(getBlockPos(), getLevel(), type.getRange(), type.getAmountPerOperation());
                power.receiveEnergy(powerToProduce, false);
            }
        }
        if (power.getEnergyStored() == 0)
            return;
        for (Direction dir : Direction.values()) {
            var pos = getBlockPos().relative(dir);
            var be = getLevel().getBlockEntity(pos);
            if (be != null) {
                be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(powerstorage -> {
                    int powerToTransfer = Integer.MAX_VALUE; //TODO: powertransfer config
                    int maxReceive = powerstorage.receiveEnergy(powerToTransfer, true);
                    int toTransfer = power.extractEnergy(maxReceive, true);
                    power.extractEnergy(toTransfer, false);
                    powerstorage.receiveEnergy(toTransfer, false);
                });
            }
        }
    }
}
