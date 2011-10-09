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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
		return createChunk(key);
	    }
	};
    }

    protected Chunk createChunk(ChunkLocation location) {
	return new WorldChunk(WorldRegion.this, location.getX(),
		location.getZ());
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
	    // this must be synchronized to avoid ConcurrentModificationException
	    chunks = Collections.synchronizedList(new ArrayList<Chunk>());
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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((chunks == null) ? 0 : chunks.hashCode());
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
	WorldRegion other = (WorldRegion) obj;
	if (chunks == null) {
	    if (other.chunks != null)
		return false;
	} else if (!chunks.equals(other.chunks))
	    return false;
	if (x != other.x)
	    return false;
	if (z != other.z)
	    return false;
	return true;
    }

}