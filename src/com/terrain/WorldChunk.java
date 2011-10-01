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

package com.terrain;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import com.tag.Cache;

@SuppressWarnings("rawtypes")
public class WorldChunk implements Chunk {

    private final WorldRegion region;
    private final int x, z;
    private List<Block> blocks;

    protected Tag<?> chunkTag;

    private final Cache<BlockLocation, Block> cache;

    protected WorldChunk(WorldRegion region, int x, int z) {
	Validate.notNull(region, "region must not be null");
	this.region = region;
	this.x = x;
	this.z = z;

	this.cache = new Cache<BlockLocation, Block>() {
	    @Override
	    public Block apply(BlockLocation key) {
		populate();
		return new WorldBlock(WorldChunk.this, key.getX(), key.getY(),
			key.getZ());
	    }
	};
    }

    public Cache<BlockLocation, Block> getCache() {
	return this.cache;
    }

    // TODO: beautify this
    private void populate() {
	if (chunkTag == null) {
	    NBTInputStream is = null;
	    try {
		is = new NBTInputStream(region.getChunkInputStream(x, z));
		chunkTag = is.readTag();
	    } catch (IOException e) {
		// TODO: don't be lazy
		throw new IOError(e);
	    } finally {
		IOUtils.closeQuietly(is);
	    }
	}
    }

    @Override
    public Block getBlock(int x, int y, int z) {
	return getBlock(new BlockLocation(x, y, z));
    }

    public Block getBlock(BlockLocation blockLocation) {
	return cache.get(blockLocation);
    }

    @Override
    public List<Block> getBlocks() {
	if (blocks == null) {
	    blocks = new ArrayList<Block>();
	    for (BlockLocation bl : BlockLocation.createList())
		blocks.add(getBlock(bl));
	}
	return blocks;
    }

    @Override
    public List<Entity> getEntities() {
	throw new UnsupportedOperationException("stub");
    }

    @Override
    public List<TileEntity> getTileEntities() {
	throw new UnsupportedOperationException("stub");
    }

    @Override
    public long getLastUpdate() {
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    Tag search = tag.search("LastUpdate");
	    if (search instanceof LongTag) {
		LongTag longTag = (LongTag) search;
		return longTag.getValue();
	    }
	}
	return -1;
    }

    @Override
    public int getLocalX() {
	return this.x;
    }

    @Override
    public int getLocalZ() {
	return this.z;
    }

    @Override
    public int getXpos() {
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    Tag search = tag.search("xPos");
	    if (search instanceof IntTag) {
		IntTag intTag = (IntTag) search;
		return intTag.getValue();
	    }
	}
	return -1;
    }

    @Override
    public int getZpos() {
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    Tag search = tag.search("zPos");
	    if (search instanceof IntTag) {
		IntTag intTag = (IntTag) search;
		return intTag.getValue();
	    }
	}
	return -1;
    }

    @Override
    public boolean isTerrainPopulated() {
	if (chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunkTag;
	    Tag search = tag.search("TerrainPopulated");
	    if (search instanceof ByteTag) {
		ByteTag byteTag = (ByteTag) search;
		byte b = byteTag.getValue();
		return b != 0;
	    }
	}
	return false;
    }

    @Override
    public String getName() {
	return "Chunk X = " + getLocalX() + ", Z = " + getLocalZ();
    }

    @Override
    public String toString() {
	return getName();
    }

}