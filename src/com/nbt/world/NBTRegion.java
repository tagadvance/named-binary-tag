package com.nbt.world;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.LazyBranch;
import com.terrain.Chunk;
import com.terrain.ChunkLocation;
import com.terrain.WorldRegion;

public class NBTRegion extends WorldRegion implements LazyBranch{

    public NBTRegion(File path) throws IOException {
	super(path);
    }
    
    @Override
    public Object getChild(int index) {
	Object[] children = getChildren();
	return children[index];
    }

    @Override
    public int getChildCount() {
	Object[] children = getChildren();
	return children.length;
    }

    @Override
    public int getIndexOfChild(Object child) {
	Object[] children = getChildren();
	return ArrayUtils.indexOf(children, child);
    }

    @Override
    public boolean hasChildren() {
	for (ChunkLocation cl : ChunkLocation.createList())
	    if (hasChunk(cl.getX(), cl.getZ()))
		return true;
	return false;
    }

    @Override
    public boolean isPopulated() {
	return !getCache().isEmpty();
    }

    @Override
    public Object[] getChildren() {
	List<Chunk> chunks = getChunks();
	return chunks.toArray();
    }
    
}
