/*
 * Copyright 2011 Taggart Spilman. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Taggart Spilman ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Taggart Spilman OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Taggart Spilman.
 */

package com.nbt.world;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import com.nbt.BlockID;
import com.nbt.LazyBranch;
import com.tag.Cache;
import com.tag.Utils;

public class WorldRegion extends RegionFile implements Region, LazyBranch {

    private Cache<ChunkLocation, Chunk> chunkCache = new Cache<ChunkLocation, Chunk>() {
	@Override
	public Chunk apply(ChunkLocation key) {
	    return new WorldChunk(key.getX(), key.getZ());
	}
    };
    private final int x, z;
    private List<Chunk> chunks;

    public WorldRegion(File path) throws IOException {
	super(path);

	String name = path.getName();
	Pattern pattern = Pattern.compile(REGION_REGEX);
	Matcher matcher = pattern.matcher(name);
	if (!matcher.matches())
	    throw new IllegalArgumentException(name);
	String groupX = matcher.group(1);
	this.x = Integer.parseInt(groupX);
	String groupZ = matcher.group(2);
	this.z = Integer.parseInt(groupZ);
    }

    @Override
    public int getRegionX() {
	return this.x;
    }

    @Override
    public int getRegionZ() {
	return this.z;
    }

    @Override
    public Chunk getChunk(int x, int z) {
	if (hasChunk(x, z))
	    return chunkCache.get(new ChunkLocation(x, z));
	return null;
    }

    @Override
    public List<Chunk> getChunks() {
	if (chunks == null) {
	    chunks = new ArrayList<Chunk>();
	    for (ChunkLocation cl : ChunkLocation.createList())
		if (hasChunk(cl.getX(), cl.getZ()))
		    chunks.add(getChunk(cl.getX(), cl.getZ()));
	}
	return chunks;
    }

    @Override
    public String getName() {
	return path.getName();
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
	return !chunkCache.isEmpty();
    }

    @Override
    public Object[] getChildren() {
	List<Chunk> chunks = getChunks();
	return chunks.toArray();
    }

    @Override
    public String toString() {
	return getName();
    }

    @SuppressWarnings("rawtypes")
    public class WorldChunk implements Chunk, LazyBranch {

	private final int x, z;
	private Tag<?> chunkTag;
	private List<Block> blocks;

	private Cache<BlockLocation, Block> cache = new Cache<BlockLocation, Block>() {
	    @Override
	    public Block apply(BlockLocation key) {
		populate();
		return new WorldBlock(key.getX(), key.getY(), key.getZ());
	    }
	};

	public WorldChunk(int x, int z) {
	    this.x = x;
	    this.z = z;
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
	    populate();

	    List<Block> blocks = getBlocks();
	    return blocks.toArray();
	}

	// TODO: beautify this
	private void populate() {
	    if (chunkTag == null) {
		NBTInputStream is = null;
		try {
		    is = new NBTInputStream(getChunkInputStream(x, z));
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
	public String toString() {
	    return getName();
	}

	public class WorldBlock implements Block, LazyBranch {

	    private final int x, y, z;
	    private Object[] children;

	    public WorldBlock(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	    }

	    private int getIndex() {
		return y
			+ (z * BlockID.MAX_Y + (x * BlockID.MAX_Y * BlockID.MAX_Z));
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
	    public int getAltitude() {
		return this.y;
	    }
	    
	    @Override
	    public int getAbsoluteX() {
		return -1;
	    }

	    @Override
	    public int getAbsoluteZ() {
		return -1;
	    }

	    @Override
	    public int getBlockID() {
		if (chunkTag instanceof CompoundTag) {
		    CompoundTag tag = (CompoundTag) chunkTag;
		    Tag<?> search = tag.search("Blocks");
		    if (search instanceof ByteArrayTag) {
			ByteArrayTag baTag = (ByteArrayTag) search;
			byte[] bytes = baTag.getValue();
			int index = getIndex();
			return bytes[index];
		    }
		}
		return -1;
	    }

	    @Override
	    public int getData() {
		if (chunkTag instanceof CompoundTag) {
		    CompoundTag tag = (CompoundTag) chunkTag;
		    Tag<?> search = tag.search("Data");
		    if (search instanceof ByteArrayTag) {
			ByteArrayTag baTag = (ByteArrayTag) search;
			byte[] bytes = baTag.getValue();
			int index = getIndex();
			halfByte(bytes, index);
		    }
		}
		return -1;
	    }

	    @Override
	    public int getSkyLight() {
		if (chunkTag instanceof CompoundTag) {
		    CompoundTag tag = (CompoundTag) chunkTag;
		    Tag<?> search = tag.search("SkyLight");
		    if (search instanceof ByteArrayTag) {
			ByteArrayTag baTag = (ByteArrayTag) search;
			byte[] bytes = baTag.getValue();
			int index = getIndex();
			halfByte(bytes, index);
		    }
		}
		return -1;
	    }

	    @Override
	    public int getBlockLight() {
		if (chunkTag instanceof CompoundTag) {
		    CompoundTag tag = (CompoundTag) chunkTag;
		    Tag<?> search = tag.search("BlockLight");
		    if (search instanceof ByteArrayTag) {
			ByteArrayTag baTag = (ByteArrayTag) search;
			byte[] bytes = baTag.getValue();
			int index = getIndex();
			halfByte(bytes, index);
		    }
		}
		return -1;
	    }

	    private int halfByte(byte[] data, int index) {
		int i = index / 2;
		byte b = data[i];
		boolean even = (index == 0) || (index % 2 == 0);
		// TODO: make sure I didn't reverse the order
		return (even ? Utils.getLow(b) : Utils.getHigh(b));
	    }

	    @Override
	    public String getName() {
		return "Block X = " + getLocalX() + ", Z = " + getLocalZ() + ", Y = "
			+ getAltitude();
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
		if (children == null) {
		    children = new Object[] {};
		}
		return children;
	    }

	    @Override
	    public String toString() {
		return getName();
	    }

	} // class WorldBlock

    } // class WorldRegion

}