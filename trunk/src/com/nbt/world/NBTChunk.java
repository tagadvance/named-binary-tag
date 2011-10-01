/**
 * Copyright 2011 Taggart Spilman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nbt.world;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.LazyBranch;
import com.terrain.Block;
import com.terrain.BlockLocation;
import com.terrain.WorldChunk;
import com.terrain.WorldRegion;

public class NBTChunk extends WorldChunk implements LazyBranch {

    public NBTChunk(WorldRegion region, int x, int z) {
	super(region, x, z);
    }

    @Override
    protected Block createBlock(BlockLocation location) {
	return new NBTBlock(this, location.getX(), location.getY(),
		location.getZ());
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