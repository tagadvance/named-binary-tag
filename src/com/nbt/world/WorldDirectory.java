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

import static com.nbt.world.Chunk.MAX_X;
import static com.nbt.world.Chunk.MAX_Z;
import static com.nbt.world.Region.REGION;
import static com.nbt.world.Region.REGION_REGEX;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.Tag;

import com.nbt.NBTBranch;
import com.tag.Cache;

public class WorldDirectory implements World, NBTBranch {

    public static final String FILE_LEVEL = "level.dat";
    public static final String DIRECTORY_REGION = "region";
    public static final String DIRECTORY_PLAYERS = "players";

    private final File base;
    private Cache<File, WorldRegion> regionCache = new Cache<File, WorldRegion>() {
	@Override
	public WorldRegion apply(File key) {
	    try {
		return new WorldRegion(key);
	    } catch (IOException e) {
		// TODO: don't be lazy
		throw new IOError(e);
	    }
	}
    };

    public WorldDirectory(File base) {
	if (!base.exists())
	    throw new IllegalArgumentException("does not exist");
	this.base = base;
    }

    public File getBaseDirectory() {
	return base;
    }

    public boolean hasFile(String child) {
	File file = new File(base, child);
	return file.exists();
    }

    public File getFile(String child) {
	return new File(base, child);
    }

    @Override
    public Tag<?> getLevel() {
	throw new UnsupportedOperationException("stub");
    }

    @Override
    public Player getPlayer(String name) {
	throw new UnsupportedOperationException("stub");
    }

    @Override
    public List<Player> getPlayers() {
	throw new UnsupportedOperationException("stub");
    }

    public List<Region> getRegions() {
	File region = getFile(DIRECTORY_REGION);
	File[] files = region.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return name.matches(REGION_REGEX);
	    }
	});
	List<Region> set = new ArrayList<Region>();
	for (File file : files)
	    set.add(regionCache.get(file));
	return set;
    }

    public Region getRegion(int regionX, int regionZ) {
	String filename = REGION.replace("x", Integer.toString(regionX))
		.replace("z", Integer.toString(regionZ));
	File regionDirectory = getFile(DIRECTORY_REGION);
	File path = new File(regionDirectory, filename);
	return regionCache.get(path);
    }

    @Override
    public Region getRegionFor(int chunkX, int chunkZ) {
	int regionX = divide(chunkX, MAX_X);
	int regionZ = divide(chunkZ, MAX_Z);
	return getRegion(regionX, regionZ);
    }

    @Override
    public Chunk getChunkFor(int x, int z) {
	int chunkX = divide(x, Block.MAX_X);
	int chunkZ = divide(z, Block.MAX_Z);
	Region region = getRegionFor(chunkX, chunkZ);
	if (region == null) {
	    // System.err.println("region is null!");
	    return null;
	}

	chunkX = toChunkLocal(chunkX);
	chunkZ = toChunkLocal(chunkZ);

	return region.getChunk(chunkX, chunkZ);
    }

    private static int divide(int dividend, int divisor) {
	int quotient = dividend / divisor;
	if (dividend < 0 && divisor >= 0)
	    quotient--;
	return quotient;
    }

    private int toChunkLocal(int coordinate) {
	return (coordinate < 0 ? Chunk.MAX_X - Math.abs(coordinate)
		: coordinate);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
	Chunk chunk = getChunkFor(x, z);
	if (chunk == null) {
	    // System.err.println("chunk is null!");
	    return null;
	}

	int localX = modulus(x, Block.MAX_X);
	localX = toBlockLocal(localX);

	int localZ = modulus(z, Block.MAX_Z);
	localZ = toBlockLocal(localZ);

	return chunk.getBlock(localX, y, localZ);
    }
    
    private static int modulus(int dividend, int divisor) {
	int modulo = dividend % divisor;
	if (dividend < 0)
	    modulo--;
	return modulo;
    }

    private int toBlockLocal(int coordinate) {
	return (coordinate < 0 ? Block.MAX_X - Math.abs(coordinate)
		: coordinate);
    }

    @Override
    public String getName() {
	return base.toString();
    }

    @Override
    public int getChildCount() {
	List<Region> regions = getRegions();
	return regions.size();
    }

    @Override
    public Object getChild(int index) {
	List<Region> regions = getRegions();
	return regions.get(index);
    }

    @Override
    public int getIndexOfChild(Object child) {
	List<Region> regions = getRegions();
	Object[] array = regions.toArray();
	return ArrayUtils.indexOf(array, child);
    }

    public String toString() {
	return getName();
    }

}