package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class SourceGeneratorBlockEntity extends BlockEntity {
    public final EnergyStorage power;
    private BlockCapabilityCache<IEnergyStorage, @Nullable Direction> capCache;

    EUBlockType EUBlockType;
    long tick = 0;

    Direction facing;

    public SourceGeneratorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Registry.sourceGeneratorBlockEntityType.get(), p_155229_, p_155230_);
        facing = p_155230_.getValue(BlockStateProperties.FACING);
        EUBlockType = ((SourceGeneratorBlock) p_155230_.getBlock()).EUBlockType;
        power = new EnergyStorage(EUBlockType.getPowerBuffer(), Integer.MAX_VALUE, EUBlockType.getMaxTransfer());
    }


    public static void RegisterCapability(RegisterCapabilitiesEvent Event) {
        Event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registry.sourceGeneratorBlockEntityType.get(),
                (be, side) -> be.power
        );
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        power.deserializeNBT(registries, tag.get("power"));
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("power", power.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }

    public void tickServerSide() {

        if (tick++ % EUBlockType.getTickInterval() == 0) {
            int aura = -1;
            if (EU.NA_LOADED)
                aura = IAuraChunk.getAuraInArea(getLevel(), getBlockPos(), 35);
            var aurachange = EUBlockType.getAuraChange() * Config.get().getAuraConversion();
            int powerToProduce = (int) (EUBlockType.getAmountPerOperation() * Config.get().getSourceConversion() + (aura > aurachange ? aurachange : 0.0f));
            facing = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING);
            if (power.receiveEnergy(powerToProduce, true) == powerToProduce
                    && SourceUtil.canTakeSource(getBlockPos(), getLevel(), EUBlockType.getRange()).stream().anyMatch(provider -> provider.isValid() && provider.getSource().getSource() >= EUBlockType.getAmountPerOperation())) {
                if (aura > 0) {
                    var spot = IAuraChunk.getHighestSpot(getLevel(), getBlockPos(), 35, getBlockPos());
                    var chunk = IAuraChunk.getAuraChunk(getLevel(), spot);
                    chunk.drainAura(spot, EUBlockType.getAuraChange(), true, false);

                    EU.send(new ParticleMessage(getBlockPos(), false, facing), getBlockPos(), 100, getLevel());
                }
                SourceUtil.takeSourceWithParticles(getBlockPos(), getLevel(), EUBlockType.getRange(), EUBlockType.getAmountPerOperation());
                power.receiveEnergy(powerToProduce, false);
            }
        }

        var pos = getBlockPos().relative(facing.getOpposite());
        if(capCache == null)
        {
            capCache = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK,(ServerLevel) getLevel(),pos,facing);
        }
        var cap = capCache.getCapability();
        if (cap != null) {
            int powerToTransfer = Integer.MAX_VALUE;
            int maxReceive = cap.receiveEnergy(powerToTransfer, true);
            int toTransfer = power.extractEnergy(maxReceive, true);
            power.extractEnergy(toTransfer, false);
            cap.receiveEnergy(toTransfer, false);
        }

    }
}
