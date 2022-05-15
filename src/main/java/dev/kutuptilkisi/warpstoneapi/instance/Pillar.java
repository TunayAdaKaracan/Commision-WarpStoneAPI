package dev.kutuptilkisi.warpstoneapi.instance;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Pillar {
    private final Location location;
    private final Block block;

    public Pillar(Location location, Block block){
        this.location = location;
        this.block = block;
    }

    public Location getLocation() {
        return location;
    }

    public Block getBlock() {
        return block;
    }
}
