package com.darkere.enigmaticunity;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;

public class SourceProducerBlockEntity extends BlockEntity {
    public final EnergyStorage power;

    EUBlockType EUBlockType;
    long tick = 0;

    Direction facing;

    public SourceProducerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Registry.sourceProducerBlockEntityType.get(), p_155229_, p_155230_);
        facing = p_155230_.getValue(BlockStateProperties.FACING);
        EUBlockType = ((SourceProducerBlock)p_155230_.getBlock()).EUBlockType;
        power = new EnergyStorage(EUBlockType.getPowerBuffer(), EUBlockType.getMaxTransfer(), Integer.MAX_VALUE);
    }

    public static void RegisterCapability(RegisterCapabilitiesEvent Event)
    {
        Event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registry.sourceProducerBlockEntityType.get(),
                (be,side)-> be.power
                );
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        power.deserializeNBT(registries,tag.get("power"));
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("power", power.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }


    public void tickServerSide() {

        if (tick++ % EUBlockType.getTickInterval() == 0) {

            var targets = SourceUtil.canGiveSource(getBlockPos(), getLevel(), EUBlockType.getRange());
            var target = targets.stream().filter(provider -> provider.isValid() && provider.getSource().getMaxSource() > provider.getSource().getSource()).findAny();
            facing = getLevel().getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING);
            target.ifPresentOrElse(jar -> {
                var maxInsert = jar.getSource().getMaxSource() - jar.getSource().getSource();
                if (maxInsert < EUBlockType.getAmountPerOperation())
                    return;
                int powerConsumed = (int) (EUBlockType.getAmountPerOperation() * Config.get().getSourceConversion());
                if (power.extractEnergy(powerConsumed, true) != powerConsumed)
                    return;
                jar.getSource().addSource(EUBlockType.getAmountPerOperation());
                power.extractEnergy(powerConsumed, false);

                EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(level, Vec3.atCenterOf(getBlockPos()), Vec3.atCenterOf(jar.getCurrentPos()));
                getLevel().addFreshEntity(aoeProjectile);

            }, () -> {
                if(!EU.NA_LOADED)
                    return;
                int powerConsumed = (int) (EUBlockType.getAuraChange() * Config.get().getAuraConversion());
                if (power.extractEnergy(powerConsumed, true) != powerConsumed)
                    return;

                if (IAuraChunk.getAuraInArea(getLevel(), getBlockPos(), 20) + EUBlockType.getAuraChange() <= IAuraChunk.DEFAULT_AURA * 2) {
                    power.extractEnergy(powerConsumed, false);
                    var spot = IAuraChunk.getLowestSpot(getLevel(),getBlockPos(),20,getBlockPos());
                    var chunk = IAuraChunk.getAuraChunk(getLevel(), spot);
                    chunk.storeAura(spot, EUBlockType.getAuraChange(), false, false);
                    EU.send(new ParticleMessage(getBlockPos(), true, facing), getBlockPos(), 100, getLevel());
                }
            });
        }
    }
}
