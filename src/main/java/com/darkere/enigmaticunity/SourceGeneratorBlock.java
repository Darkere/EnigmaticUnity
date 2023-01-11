package com.darkere.enigmaticunity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class SourceGeneratorBlock extends Block implements EntityBlock {
    private Type type;
    private static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_N_DIM = Stream.of(
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(7, 7, 11, 9, 9, 14),
            Block.box(8, 8, 13, 10, 10, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E_DIM = VoxelUtil.rotateY(SHAPE_N_DIM, 90);
    private static final VoxelShape SHAPE_S_DIM = VoxelUtil.rotateY(SHAPE_E_DIM, 90);
    private static final VoxelShape SHAPE_W_DIM = VoxelUtil.rotateY(SHAPE_S_DIM, 90);
    private static final VoxelShape SHAPE_U_DIM = VoxelUtil.rotateX(SHAPE_N_DIM, 90);
    private static final VoxelShape SHAPE_D_DIM = VoxelUtil.rotateX(SHAPE_S_DIM, 90);

    private static final VoxelShape[] SHAPES_DIM = new VoxelShape[] { SHAPE_D_DIM, SHAPE_U_DIM, SHAPE_N_DIM, SHAPE_S_DIM, SHAPE_W_DIM, SHAPE_E_DIM };

    private static final VoxelShape SHAPE_N_BRIGHT = Stream.of(
            Block.box(5, 5, 12, 7, 7, 14),
            Block.box(7, 7, 9, 9, 9, 14),
            Block.box(8, 8, 12, 10, 10, 13),
            Block.box(6, 6, 13, 11, 11, 14),
            Block.box(4, 4, 14, 12, 12, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E_BRIGHT = VoxelUtil.rotateY(SHAPE_N_BRIGHT, 90);
    private static final VoxelShape SHAPE_S_BRIGHT = VoxelUtil.rotateY(SHAPE_E_BRIGHT, 90);
    private static final VoxelShape SHAPE_W_BRIGHT = VoxelUtil.rotateY(SHAPE_S_BRIGHT, 90);
    private static final VoxelShape SHAPE_U_BRIGHT = VoxelUtil.rotateX(SHAPE_N_BRIGHT, 90);
    private static final VoxelShape SHAPE_D_BRIGHT = VoxelUtil.rotateX(SHAPE_S_BRIGHT, 90);

    private static final VoxelShape[] SHAPES_BRIGHT = new VoxelShape[] { SHAPE_D_BRIGHT, SHAPE_U_BRIGHT, SHAPE_N_BRIGHT, SHAPE_S_BRIGHT, SHAPE_W_BRIGHT, SHAPE_E_BRIGHT };

    private static final VoxelShape SHAPE_N_IRIDESCENT = Stream.of(
            Block.box(5, 5, 11, 8, 8, 14),
            Block.box(6, 6, 9, 8, 8, 11),
            Block.box(7, 7, 7, 9, 9, 14),
            Block.box(8, 8, 10, 10, 10, 14),
            Block.box(6, 6, 13, 11, 11, 14),
            Block.box(4, 4, 14, 12, 12, 16),
            Block.box(8, 5, 12, 10, 7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E_IRIDESCENT = VoxelUtil.rotateY(SHAPE_N_IRIDESCENT, 90);
    private static final VoxelShape SHAPE_S_IRIDESCENT = VoxelUtil.rotateY(SHAPE_E_IRIDESCENT, 90);
    private static final VoxelShape SHAPE_W_IRIDESCENT = VoxelUtil.rotateY(SHAPE_S_IRIDESCENT, 90);
    private static final VoxelShape SHAPE_U_IRIDESCENT = VoxelUtil.rotateX(SHAPE_N_IRIDESCENT, 90);
    private static final VoxelShape SHAPE_D_IRIDESCENT = VoxelUtil.rotateX(SHAPE_S_IRIDESCENT, 90);

    private static final VoxelShape[] SHAPES_IRIDESCENT = new VoxelShape[] {SHAPE_D_IRIDESCENT, SHAPE_U_IRIDESCENT, SHAPE_N_IRIDESCENT, SHAPE_S_IRIDESCENT, SHAPE_W_IRIDESCENT, SHAPE_E_IRIDESCENT};

    public SourceGeneratorBlock(Properties p_49795_, Type type) {
        super(p_49795_);
        this.type = type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (type == Type.DIM) {
            return SHAPES_DIM[state.getValue(FACING).get3DDataValue()];
        } else if (type == Type.BRIGHT) {
            return SHAPES_BRIGHT[state.getValue(FACING).get3DDataValue()];
        } else {
            return SHAPES_IRIDESCENT[state.getValue(FACING).get3DDataValue()];
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate (BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror (BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        var gen =  new SourceGeneratorBlockEntity(pPos, pState);
        gen.setType(type);
        return gen;

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide)
            return null;
        return ((pLevel1, pPos, pState1, te) ->{
            if(te instanceof SourceGeneratorBlockEntity tile)
                tile.tickServerSide();
        });
    }
}
