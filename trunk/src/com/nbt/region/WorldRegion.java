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

package com.nbt.region;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import com.nbt.Branch;

public class WorldRegion extends RegionFile implements Region, Branch {

	public WorldRegion(File path) throws IOException {
		super(path);
	}

	@Override
	public List<Chunk> getChunks() {
		List<Chunk> chunks = new ArrayList<Chunk>();
		for (int z = 0; z < 32; z++)
			for (int x = 0; x < 32; x++)
				if (hasChunk(x, z))
					chunks.add(new RegionChunk(x, z));
		return chunks;
	}

	@Override
	public Chunk getChunk(int x, int z) {
		return new RegionChunk(x, z);
	}

	@Override
	public Object getChild(int index) {
		List<Chunk> chunks = getChunks();
		return chunks.get(index);
	}

	@Override
	public int getChildCount() {
		List<Chunk> chunks = getChunks();
		return chunks.size();
	}

	@Override
	public int getIndexOfChild(Object child) {
		List<Chunk> chunks = getChunks();
		Object[] array = chunks.toArray();
		return ArrayUtils.indexOf(array, child);
	}

	class RegionChunk implements Chunk, Branch {

		private final int x, z;

		// TODO: replace with intelligent cache
		private Tag<?> tag;

		public RegionChunk(int x, int z) {
			super();
			this.x = x;
			this.z = z;
			System.out.println(new ChunkLocation(x, z));
		}

		@Override
		public Tag<?> load() throws IOException {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(getInputStream());
				return is.readTag();
			} finally {
				IOUtils.closeQuietly(is);
			}
		}

		@Override
		public void save(Tag<?> tag) throws IOException {
			NBTOutputStream os = null;
			try {
				os = new NBTOutputStream(getOutputStream());
				os.writeTag(tag);
			} finally {
				IOUtils.closeQuietly(os);
			}
		}

		@Override
		public boolean isEmpty() {
			throw new RuntimeException("stub");
		}

		@Override
		public int getX() {
			return this.x;
		}

		@Override
		public int getZ() {
			return this.z;
		}

		@Override
		public int getSector() {
			throw new RuntimeException("stub");
		}

		@Override
		public int getTimestamp() {
			throw new RuntimeException("stub");
		}

		@Override
		public int getOffset() {
			throw new RuntimeException("stub");
		}

		@Override
		public int getLength() {
			throw new RuntimeException("stub");
		}

		@Override
		public int getCompressionType() {
			throw new RuntimeException("stub");
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return getChunkInputStream(x, z);
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			return getChunkOutputStream(x, z);
		}

		@Override
		public Object getChild(int index) {
			if (tag == null)
				try {
					tag = load();
				} catch (IOException e) {
					e.printStackTrace();
				}
			return tag;
		}

		@Override
		public int getChildCount() {
			return 1;
		}

		@Override
		public int getIndexOfChild(Object child) {
			return (child == null ? -1 : 0);
		}

	}

}