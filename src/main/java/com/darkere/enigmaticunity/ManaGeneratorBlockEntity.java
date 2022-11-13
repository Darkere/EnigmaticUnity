package com.darkere.enigmaticunity;

import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;

import java.util.List;

public class ManaGeneratorBlockEntity extends BlockEntity implements SparkAttachable, ManaReceiver {




    private int mana = 0;
    private String manaTag = "mana";
    private int maxMana = 100000; //TODO: config

    public ManaGeneratorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(EU.manaGeneratorBlockEntityType.get(), p_155229_, p_155230_);
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
            return (ManaSpark)e;
        } else {
            return null;
        }
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == BotaniaForgeCapabilities.SPARK_ATTACHABLE)
            return super.getCapability(cap);
            return super.getCapability(cap);

    }
    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public void load(CompoundTag pTag) {
        this.mana = pTag.getInt(manaTag);
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt(manaTag,mana);
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
}
