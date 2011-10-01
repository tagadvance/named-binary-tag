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
import static com.terrain.Chunk.MIN_X;
import static com.terrain.Chunk.MIN_Z;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class ChunkLocation {

    private int x, z;

    public ChunkLocation() {
	this(MIN_X, MIN_Z);
    }

    public ChunkLocation(int localX, int localZ) {
	// Utils.validate(x, MIN_X, MAX_X);
	// Utils.validate(z, MIN_Z, MAX_Z);
	this.x = localX;
	this.z = localZ;
    }

    public int getX() {
	return x;
    }

    public int getZ() {
	return z;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	Formatter formatter = new Formatter(sb, Locale.US);
	formatter.format("ChunkLocation [x=%1s, z=%1s]", getX(), getZ());
	return sb.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
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
	ChunkLocation other = (ChunkLocation) obj;
	if (x != other.x)
	    return false;
	if (z != other.z)
	    return false;
	return true;
    }

    public static List<ChunkLocation> createList() {
	List<ChunkLocation> list = new ArrayList<ChunkLocation>();
	for (int z = MIN_Z; z < MAX_Z; z++)
	    for (int x = MIN_X; x < MAX_X; x++)
		list.add(new ChunkLocation(x, z));
	return list;
    }

}