package com.darkere.enigmaticunity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SourceProducerBlock extends Block implements EntityBlock,SimpleWaterloggedBlock {
    public EUBlockType EUBlockType;

    private static final VoxelShape SHAPE_N = Shapes.join(
            Block.box(4.5, 4.5, 6, 11.5, 11.5, 13),
            Block.box(4, 4, 13, 12, 12, 16),
            BooleanOp.OR);

    private static final VoxelShape SHAPE_E = VoxelUtil.rotateY(SHAPE_N, 90);
    private static final VoxelShape SHAPE_S = VoxelUtil.rotateY(SHAPE_E, 90);
    private static final VoxelShape SHAPE_W = VoxelUtil.rotateY(SHAPE_S, 90);
    private static final VoxelShape SHAPE_U = VoxelUtil.rotateX(SHAPE_N, 90);
    private static final VoxelShape SHAPE_D = VoxelUtil.rotateX(SHAPE_S, 90);

    private static final VoxelShape[] SHAPES = new VoxelShape[] { SHAPE_D, SHAPE_U, SHAPE_N, SHAPE_S, SHAPE_W, SHAPE_E };

    public SourceProducerBlock(Properties p_49795_, EUBlockType EUBlockType) {
        super(p_49795_);
        this.EUBlockType = EUBlockType;
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED,false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(BlockStateProperties.FACING).get3DDataValue()];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace()).setValue(BlockStateProperties.WATERLOGGED,fluidState.getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }


    @Override
    public boolean canPlaceLiquid(Player player, BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(player, worldIn, pos, state, fluidIn);
    }
    @Override
    public BlockState rotate (BlockState state, Rotation rot) {
        var newDir = rot.rotate(state.getValue(BlockStateProperties.FACING));
        return state.setValue(BlockStateProperties.FACING, newDir);
    }

    @Override
    public BlockState mirror (BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SourceProducerBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide)
            return null;
        return ((pLevel1, pPos, pState1, te) ->{
            if(te instanceof SourceProducerBlockEntity tile)
                tile.tickServerSide();
        });
    }
}
