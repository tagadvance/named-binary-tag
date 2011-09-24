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
import java.io.FilenameFilter;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.Tag;

import com.nbt.NBTBranch;

public class WorldDirectory implements NBTBranch {

    public static final String REGION = "r.x.z.mcr";
    private static final String REGION_REGEX = "r.([\\-]?[\\d]+).([\\-]?[\\d]+).mcr";

    public static final String FILE_LEVEL = "level.dat";
    public static final String DIRECTORY_REGION = "region";
    public static final String DIRECTORY_PLAYERS = "players";

    private final File base;

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

    public List<Region> getRegions() {
	File region = getFile(DIRECTORY_REGION);
	File[] files = region.listFiles(new FilenameFilter() {

	    @Override
	    public boolean accept(File dir, String name) {
		return name.matches(REGION_REGEX);
	    }

	});
	List<Region> set = new ArrayList<Region>();
	for (File file : files) {
	    try {
		set.add(new WorldRegion(file));
	    } catch (IOException e) {
		// TODO: don't be lazy
		throw new IOError(e);
	    }
	}
	return set;
    }

    public Region getRegion(int x, int z) {
	String filename = REGION.replace("x", Integer.toString(x));
	filename = REGION.replace("z", Integer.toString(z));
	File regionDirectory = getFile(DIRECTORY_REGION);
	File path = new File(regionDirectory, filename);
	try {
	    return new WorldRegion(path);
	} catch (IOException e) {
	    // TODO: don't be lazy
	    throw new IOError(e);
	}
    }

    public Tag<?> getTag(int chunkX, int chunkZ) {
	int localX = (int) Math.floor((double) chunkX / 32);
	int localZ = (int) Math.floor((double) chunkZ / 32);
	Region region = getRegion(localX, localZ);
	return region.loadTag(chunkX, chunkZ);
    }

    @Override
    public Object getChild(int index) {
	List<Region> regions = getRegions();
	return regions.get(index);
    }

    @Override
    public int getChildCount() {
	List<Region> regions = getRegions();
	return regions.size();
    }

    @Override
    public int getIndexOfChild(Object child) {
	List<Region> regions = getRegions();
	Object[] array = regions.toArray();
	return ArrayUtils.indexOf(array, child);
    }

    public String toString() {
	return base.toString();
    }

}