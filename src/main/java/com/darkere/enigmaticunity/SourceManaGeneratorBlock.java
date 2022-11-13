package com.darkere.enigmaticunity;


import net.minecraft.world.level.block.Block;

public class SourceManaGeneratorBlock extends Block {
    private Type type;

    public SourceManaGeneratorBlock(Properties p_49795_, Type type) {
        super(p_49795_);
        this.type = type;
    }
}
