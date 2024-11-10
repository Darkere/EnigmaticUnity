package com.darkere.enigmaticunity.Datagen;

import com.darkere.enigmaticunity.EU;
import com.darkere.enigmaticunity.Registry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class EUBlockStateProvider extends BlockStateProvider {

    public EUBlockStateProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), EU.MODID, event.getExistingFileHelper());
    }

    @Override
    protected void registerStatesAndModels() {

        Registry.sourceProducerBlocks.forEach((type,block)->{
            String name = type.toString() + "_source_producer";
            ResourceLocation res = modLoc(name.toLowerCase());
            getVariantBuilder(block.get()).forAllStates(
                    state ->{
                      var facing = state.getValue(BlockStateProperties.FACING);
                      var water = state.getValue(BlockStateProperties.WATERLOGGED);
                      return ConfiguredModel.builder()
                              .modelFile(models().getExistingFile(res))
                              .rotationX(facing == Direction.DOWN ? 180 : facing == Direction.UP ? 0 : 90)
                              .rotationY(getDirection(facing))
                              .build();
                    });
        });
        Registry.sourceGeneratorBlocks.forEach((type,block)->{
            String name = type.toString() + "_source_generator";
            ResourceLocation res = modLoc(name.toLowerCase());
            getVariantBuilder(block.get()).forAllStates(
                    state ->{
                        var facing = state.getValue(BlockStateProperties.FACING);
                        var water = state.getValue(BlockStateProperties.WATERLOGGED);
                        return ConfiguredModel.builder()
                                .modelFile(models().getExistingFile(res))
                                .rotationX(facing == Direction.DOWN ? 180 : facing == Direction.UP ? 0 : 90)
                                .rotationY(getDirection(facing))
                                .build();
                    });
        });

    }

    private int getDirection(Direction facing) {
        return (int)facing.getOpposite().toYRot();
//        return switch (facing) {
//            case DOWN, SOUTH -> 180;
//            case EAST -> 90;
//            case NORTH, UP -> 0;
//            case WEST -> 270;
//        };
    }
}
