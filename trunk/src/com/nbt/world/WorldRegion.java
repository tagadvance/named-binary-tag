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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import com.nbt.LazyBranch;

public class WorldRegion extends RegionFile implements Region, LazyBranch {

    private Object[] children;

    public WorldRegion(File path) throws IOException {
	super(path);
    }

    @Override
    public int getX() {
	return 0;
    }

    @Override
    public int getZ() {
	return 0;
    }

    @Override
    public List<Tag<?>> getTags() {
	throw new RuntimeException("stub");
    }

    @Override
    public Tag<?> loadTag(int x, int z) {
	NBTInputStream is = null;
	try {
	    is = new NBTInputStream(getChunkInputStream(x, z));
	    return is.readTag();
	} catch (IOException e) {
	    // TODO: don't be lazy
	    throw new IOError(e);
	} finally {
	    IOUtils.closeQuietly(is);
	}
    }

    @Override
    public void saveTag(int x, int z, Tag<?> tag) {
	NBTOutputStream is = null;
	try {
	    is = new NBTOutputStream(getChunkOutputStream(x, z));
	    is.writeTag(tag);
	} catch (IOException e) {
	    // TODO: don't be lazy
	    throw new IOError(e);
	} finally {
	    IOUtils.closeQuietly(is);
	}
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
    public boolean isPopulated() {
	return (children != null);
    }

    @Override
    public boolean hasChildren() {
	for (int z = ChunkLocation.MIN_Z; z < ChunkLocation.MAX_Z; z++)
	    for (int x = ChunkLocation.MIN_X; x < ChunkLocation.MAX_X; x++)
		if (hasChunk(x, z))
		    return true;

	return false;
    }

    @Override
    public Object[] getChildren() {
	if (children == null) {
	    ArrayList<Tag<?>> tags = new ArrayList<Tag<?>>();
	    for (int z = ChunkLocation.MIN_Z; z < ChunkLocation.MAX_Z; z++)
		for (int x = ChunkLocation.MIN_X; x < ChunkLocation.MAX_X; x++)
		    if (hasChunk(x, z))
			tags.add(loadTag(x, z));

	    children = tags.toArray();
	}
	return children;
    }

    public String toString() {
	return getName();
    }

}