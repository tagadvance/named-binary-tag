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

import org.jnbt.CompoundTag;

import com.nbt.NBTBranch;
import com.terrain.Block;
import com.terrain.BlockLocation;
import com.terrain.WorldChunk;
import com.terrain.WorldRegion;

public class NBTChunk extends WorldChunk implements NBTBranch {

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
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    return tag.getChildCount();
	}
	return 0;
    }

    @Override
    public Object getChild(int index) {
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    return tag.getChild(index);
	}
	return null;
    }

    @Override
    public int getIndexOfChild(Object child) {
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    tag.getIndexOfChild(child);
	}
	return -1;
    }

}