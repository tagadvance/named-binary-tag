package com.nbt.world;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.LazyBranch;
import com.terrain.Block;
import com.terrain.WorldChunk;
import com.terrain.WorldRegion;

public class NBTChunk extends WorldChunk implements LazyBranch {
    
    public NBTChunk(WorldRegion region, int x, int z) {
	super(region, x, z);
    }

    @Override
    public int getChildCount() {
	Object[] children = getChildren();
	return children.length;
    }

    @Override
    public Object getChild(int index) {
	Object[] children = getChildren();
	return children[index];
    }

    @Override
    public int getIndexOfChild(Object child) {
	Object[] children = getChildren();
	return ArrayUtils.indexOf(children, child);
    }

    @Override
    public boolean hasChildren() {
	return true;
    }

    @Override
    public boolean isPopulated() {
	return chunkTag != null;
    }

    @Override
    public Object[] getChildren() {
	List<Block> blocks = getBlocks();
	return blocks.toArray();
    }

}