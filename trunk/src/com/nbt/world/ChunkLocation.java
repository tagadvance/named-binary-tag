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
import static com.nbt.world.Chunk.MIN_X;
import static com.nbt.world.Chunk.MIN_Z;

import java.util.ArrayList;
import java.util.List;

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