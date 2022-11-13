package com.darkere.enigmaticunity;

import com.google.common.base.Predicates;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;

import java.util.List;

public class ManaGeneratorBlockEntity extends BlockEntity implements SparkAttachable, ManaReceiver {
    private int mana = 0;
    private String manaTag = "mana";
    private String typeTag = "type";
    private int maxMana = 100000;
    EnergyStorage power;
    LazyOptional<SparkAttachable> sparkCap = LazyOptional.of(() -> this);
    LazyOptional<ManaReceiver> manaCap = LazyOptional.of(() -> this);
    LazyOptional<IEnergyStorage> powerCap = LazyOptional.of(() -> power);

    Type type = Type.MANA;

    public ManaGeneratorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Registry.manaGeneratorBlockEntityType.get(), p_155229_, p_155230_);

    }

    public void setType(Type type) {
        this.type = type;
        power = new EnergyStorage(Config.get().manaGenerator.getManaGeneratorCapacity(type));
        maxMana = Config.get().manaGenerator.getManaGeneratorCapacity(type);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == BotaniaForgeCapabilities.SPARK_ATTACHABLE)
            return sparkCap.cast();
        if (cap == BotaniaForgeCapabilities.MANA_RECEIVER)
            return manaCap.cast();
        if (cap == ForgeCapabilities.ENERGY)
            return powerCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(0, maxMana - mana);
    }

    @Override
    public ManaSpark getAttachedSpark() {
        List<Entity> sparks = this.level.getEntitiesOfClass(Entity.class, new AABB(this.worldPosition.above(), this.worldPosition.above().offset(1, 1, 1)), Predicates.instanceOf(ManaSpark.class));
        if (sparks.size() == 1) {
            Entity e = sparks.get(0);
            return (ManaSpark) e;
        } else {
            return null;
        }
    }


    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public void load(CompoundTag pTag) {
        this.mana = pTag.getInt(manaTag);
        this.type = Type.values()[pTag.getInt(typeTag)];
        power.deserializeNBT(pTag.get("power"));
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt(manaTag, mana);
        pTag.putInt(typeTag, type.ordinal());
        pTag.put("power", power.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public Level getManaReceiverLevel() {
        return level;
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return getBlockPos();
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }

    @Override
    public boolean isFull() {
        return mana == maxMana;
    }

    @Override
    public void receiveMana(int i) {
        mana += i;
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }

    public void tickServerSide() {

        if (mana > 0 && power.receiveEnergy(1, true) > 0) {
            int toExtract = Math.min(mana, Config.get().manaGenerator.getManaGeneratorConversionPerTick(type)); //TODO config //TODO Tiers
            double ratio = Config.get().manaGenerator.getManaGeneratorConversion(type);
            int toInsert = (int) Math.round(toExtract / ratio);
            int actualInsert = power.receiveEnergy(toInsert, true);
            if (toInsert > actualInsert)
                toExtract = (int) (actualInsert * ratio);
            mana -= toExtract;
            power.receiveEnergy(actualInsert, false);
        }
    }
}
