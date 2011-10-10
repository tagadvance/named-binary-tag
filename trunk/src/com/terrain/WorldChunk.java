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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import com.tag.Cache;

@SuppressWarnings("rawtypes")
public class WorldChunk implements Chunk, Saveable {

    private final WorldRegion region;
    private final int x, z;
    private List<Block> blocks;

    protected CompoundTag chunkTag;

    private final Cache<BlockLocation, Block> cache;

    private int hashCode;

    protected WorldChunk(WorldRegion region, int x, int z) {
	Validate.notNull(region, "region must not be null");
	this.region = region;
	this.x = x;
	this.z = z;

	// System.out.println("populate " + this);
	NBTInputStream is = null;
	try {
	    boolean gzip = false;
	    is = new NBTInputStream(region.getChunkInputStream(x, z), gzip);
	    this.chunkTag = (CompoundTag) is.readTag();
	} catch (IOException e) {
	    // TODO: don't be lazy
	    throw new IllegalArgumentException(e);
	} finally {
	    IOUtils.closeQuietly(is);
	}
	mark();

	this.cache = new Cache<BlockLocation, Block>() {
	    @Override
	    public Block apply(BlockLocation key) {
		return createBlock(key);
	    }
	};
    }

    protected Block createBlock(BlockLocation location) {
	return new WorldBlock(WorldChunk.this, location.getX(),
		location.getY(), location.getZ());
    }

    public Cache<BlockLocation, Block> getCache() {
	return this.cache;
    }

    public Region getRegion() {
	return this.region;
    }

    public Tag<?> getTag() {
	return this.chunkTag;
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
	Tag search = chunkTag.search("LastUpdate");
	if (search instanceof LongTag) {
	    LongTag longTag = (LongTag) search;
	    return longTag.getValue();
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
	Tag search = chunkTag.search("xPos");
	if (search instanceof IntTag) {
	    IntTag intTag = (IntTag) search;
	    return intTag.getValue();
	}
	return -1;
    }

    @Override
    public int getZpos() {
	Tag search = chunkTag.search("zPos");
	if (search instanceof IntTag) {
	    IntTag intTag = (IntTag) search;
	    return intTag.getValue();
	}
	return -1;
    }

    @Override
    public boolean isTerrainPopulated() {
	Tag search = chunkTag.search("TerrainPopulated");
	if (search instanceof ByteTag) {
	    ByteTag byteTag = (ByteTag) search;
	    byte b = byteTag.getValue();
	    return b != 0;
	}
	return false;
    }

    @Override
    public String getName() {
	StringBuilder sb = new StringBuilder();
	Formatter formatter = new Formatter(sb, Locale.US);
	formatter.format("Chunk [x=%1s, z=%1s]", getLocalX(), getLocalZ());
	return sb.toString();
    }

    @Override
    public void mark() {
	this.hashCode = hashCode();
    }

    @Override
    public boolean hasChanged() {
	if (this.hashCode == 0)
	    // TODO: perhaps I should throw an exception instead?
	    return false;

	if (chunkTag.hasChanged())
	    return true;

	return (this.hashCode != hashCode());
    }

    @Override
    public void save() throws IOException {
	Region region = getRegion();
	if (region instanceof WorldRegion) {
	    WorldRegion worldRegion = (WorldRegion) region;
	    int x = getLocalX(), z = getLocalZ();
	    NBTOutputStream out = null;
	    try {
		boolean gzip = false;
		out = new NBTOutputStream(
			worldRegion.getChunkOutputStream(x, z), gzip);
		out.writeTag(chunkTag);
	    } finally {
		IOUtils.closeQuietly(out);
	    }
	}
	mark();
    }

    @Override
    public String toString() {
	return getName();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
	result = prime * result
		+ ((chunkTag == null) ? 0 : chunkTag.hashCode());
	result = prime * result + x;
	result = prime * result + z;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	WorldChunk other = (WorldChunk) obj;
	if (blocks == null) {
	    if (other.blocks != null)
		return false;
	} else if (!blocks.equals(other.blocks))
	    return false;
	if (chunkTag == null) {
	    if (other.chunkTag != null)
		return false;
	} else if (!chunkTag.equals(other.chunkTag))
	    return false;
	if (x != other.x)
	    return false;
	if (z != other.z)
	    return false;
	return true;
    }

}