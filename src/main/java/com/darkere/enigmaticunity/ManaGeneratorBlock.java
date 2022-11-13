package com.darkere.enigmaticunity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ManaGeneratorBlock extends Block implements EntityBlock {
    private Type type;

    public ManaGeneratorBlock(Properties p_49795_, Type type) {
        super(p_49795_);
        this.type = type;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        var gen =  new ManaGeneratorBlockEntity(pPos, pState);
        gen.setType(type);
        return gen;

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide)
            return null;
        return ((pLevel1, pPos, pState1, te) ->{
            if(te instanceof ManaGeneratorBlockEntity tile)
                tile.tickServerSide();
        });
    }
}
