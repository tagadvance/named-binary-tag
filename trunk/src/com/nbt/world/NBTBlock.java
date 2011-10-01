package com.nbt.world;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.LazyBranch;
import com.terrain.WorldBlock;
import com.terrain.WorldChunk;

public class NBTBlock extends WorldBlock implements LazyBranch {

    private Object[] children;

    public NBTBlock(WorldChunk chunk, int x, int y, int z) {
	super(chunk, x, y, z);
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
	return children != null;
    }

    @Override
    public Object[] getChildren() {
	if (children == null)
	    children = new Object[] {};
	return children;
    }

}