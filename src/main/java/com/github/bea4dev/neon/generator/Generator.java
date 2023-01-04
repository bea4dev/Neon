package com.github.bea4dev.neon.generator;

import org.bukkit.World;
import org.bukkit.util.Vector;
import thpmc.vanilla_source.api.entity.tick.TickThread;

public class Generator {

    private final TickThread thread;
    private final World world;
    private Vector rangeMin;
    private Vector rangeMax;

    public Generator(TickThread thread, World world, Vector rangePos1, Vector rangePos2) {
        this.thread = thread;
        this.world = world;
        this.rangeMin = Vector.getMinimum(rangePos1, rangePos2);
        this.rangeMax = Vector.getMaximum(rangePos1, rangePos2);
    }

    public BlockInfo getBlock(int x, int y, int z) {
        
    }

}
