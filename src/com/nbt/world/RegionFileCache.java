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

/*
 * 2011 January 5
 * 
 * The author disclaims copyright to this source code. In place of a legal
 * notice, here is a blessing:
 * 
 * May you do good and not evil.
 * 
 * May you find forgiveness for yourself and forgive others.
 * 
 * May you share freely, never taking more than you give.
 */

/*
 * 2011 February 16
 * 
 * This source code is based on the work of Scaevolus (see notice above). It has
 * been slightly modified by Mojang AB to limit the maximum cache size (relevant
 * to extremely big worlds on Linux systems with limited number of file
 * handles). The region files are postfixed with ".mcr" (Minecraft region file)
 * instead of ".data" to differentiate from the original McRegion files.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache and wrapper for efficiently multiple RegionFiles
 * simultaneously.
 * 
 * http://pastebin.com/jvZ1yhAd
 */
public class RegionFileCache {

    private static final int MAX_CACHE_SIZE = 256;

    private static final Map<File, Reference<RegionFile>> cache = new HashMap<File, Reference<RegionFile>>();

    private RegionFileCache() {
    }

    public static synchronized RegionFile getRegionFile(File basePath,
	    int chunkX, int chunkZ) throws IOException {
	File regionDir = new File(basePath, "region");
	File file = new File(regionDir, "r." + (chunkX >> 5) + "."
		+ (chunkZ >> 5) + ".mcr");

	Reference<RegionFile> ref = cache.get(file);

	if (ref != null && ref.get() != null)
	    return ref.get();

	if (!regionDir.exists())
	    regionDir.mkdirs();

	if (cache.size() >= MAX_CACHE_SIZE)
	    RegionFileCache.clear();

	RegionFile reg = new RegionFile(file);
	cache.put(file, new SoftReference<RegionFile>(reg));
	return reg;
    }

    public static synchronized void clear() {
	for (Reference<RegionFile> ref : cache.values()) {
	    try {
		if (ref.get() != null) {
		    ref.get().close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	cache.clear();
    }

    public static int getSizeDelta(File basePath, int chunkX, int chunkZ)
	    throws IOException {
	RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
	return r.getSizeDelta();
    }

    public static InputStream getChunkInputStream(File basePath, int chunkX,
	    int chunkZ) throws IOException {
	RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
	return r.getChunkInputStream(chunkX & 31, chunkZ & 31);
    }

    public static OutputStream getChunkOutputStream(File basePath, int chunkX,
	    int chunkZ) throws IOException {
	RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
	return r.getChunkOutputStream(chunkX & 31, chunkZ & 31);
    }

}