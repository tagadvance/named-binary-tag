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

import org.apache.commons.lang3.Validate;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import com.nbt.BlockID;
import com.tag.Utils;

public class WorldBlock implements Block {

    private final WorldChunk chunk;
    private final int x, y, z;

    public WorldBlock(WorldChunk chunk, int x, int y, int z) {
	Validate.notNull(chunk, "chunk must not be null");
	this.chunk = chunk;
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public Chunk getChunk() {
	return this.chunk;
    }

    private int getIndex() {
	return y + (z * BlockID.MAX_Y + (x * BlockID.MAX_Y * BlockID.MAX_Z));
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
	byte[] bytes = getByteArray("Blocks");
	if (bytes != null) {
	    int index = getIndex();
	    return bytes[index];
	}
	return -1;
    }

    @Override
    public int getData() {
	byte[] bytes = getByteArray("Data");
	if (bytes != null) {
	    int index = getIndex();
	    return halfByte(bytes, index);
	}
	return -1;
    }

    @Override
    public int getSkyLight() {
	byte[] bytes = getByteArray("SkyLight");
	if (bytes != null) {
	    int index = getIndex();
	    return halfByte(bytes, index);
	}
	return -1;
    }

    @Override
    public int getBlockLight() {
	byte[] bytes = getByteArray("BlockLight");
	if (bytes != null) {
	    int index = getIndex();
	    return halfByte(bytes, index);
	}
	return -1;
    }

    private byte[] getByteArray(String name) {
	if (chunk.chunkTag instanceof CompoundTag) {
	    CompoundTag tag = (CompoundTag) chunk.chunkTag;
	    Tag<?> search = tag.search(name);
	    if (search instanceof ByteArrayTag) {
		ByteArrayTag baTag = (ByteArrayTag) search;
		return baTag.getValue();
	    }
	}
	return null;
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
    public String toString() {
	return getName();
    }

}