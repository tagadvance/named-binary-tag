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

package com.terrain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tag.Cache;

public class WorldRegion extends RegionFile implements Region {

    private final int x, z;
    private List<Chunk> chunks;
    
    private final Cache<ChunkLocation, Chunk> cache;

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

	this.cache = new Cache<ChunkLocation, Chunk>() {
	    @Override
	    public Chunk apply(ChunkLocation key) {
		return new WorldChunk(WorldRegion.this, key.getX(), key.getZ());
	    }
	};
    }

    public Cache<ChunkLocation, Chunk> getCache() {
	return this.cache;
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
	    return cache.get(new ChunkLocation(x, z));
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
    public String toString() {
	return getName();
    }

}