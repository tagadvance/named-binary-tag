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

import static com.terrain.Chunk.MAX_X;
import static com.terrain.Chunk.MAX_Z;
import static com.terrain.Region.REGION;
import static com.terrain.Region.REGION_REGEX;

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

public class WorldDirectory implements World {

    public static final String FILE_LEVEL = "level.dat";
    public static final String DIRECTORY_REGION = "region";
    public static final String DIRECTORY_PLAYERS = "players";

    private final File base;
    private final Cache<File, Region> cache;

    public WorldDirectory(File base) {
	if (!base.exists())
	    throw new IllegalArgumentException("does not exist");
	this.base = base;

	this.cache = new Cache<File, Region>() {
	    @Override
	    public Region apply(File key) {
		return createRegion(key);
	    }
	};
    }

    protected Region createRegion(File file) {
	try {
	    return new WorldRegion(file);
	} catch (IOException e) {
	    // TODO: don't be lazy
	    throw new IOError(e);
	}
    }

    public Cache<File, Region> getCache() {
	return this.cache;
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
	    set.add(cache.get(file));
	return set;
    }

    public Region getRegion(int regionX, int regionZ) {
	String filename = REGION.replace("x", Integer.toString(regionX))
		.replace("z", Integer.toString(regionZ));
	File regionDirectory = getFile(DIRECTORY_REGION);
	File path = new File(regionDirectory, filename);
	return cache.get(path);
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

    public String toString() {
	return getName();
    }

}