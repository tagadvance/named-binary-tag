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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.LazyBranch;
import com.terrain.Chunk;
import com.terrain.ChunkLocation;
import com.terrain.WorldRegion;

public class NBTRegion extends WorldRegion implements LazyBranch {

    public NBTRegion(File file) throws IOException {
	super(file);
    }

    @Override
    protected Chunk createChunk(ChunkLocation location) {
	return new NBTChunk(this, location.getX(), location.getZ());
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
