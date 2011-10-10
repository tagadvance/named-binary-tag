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

package com.nbt.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

import com.nbt.NBTBranch;
import com.nbt.NBTNode;
import com.tag.Cache;
import com.terrain.Region;
import com.terrain.WorldDirectory;

public class NBTFileBranch implements NBTNode, NBTBranch {

    private final NBTFileBranch parent;
    private final File file;

    private Cache<String, File> fileCache;
    private Cache<File, CompoundTag> tagCache;
    private Cache<File, Region> regionCache;
    private Cache<File, NBTBranch> branchCache;

    public NBTFileBranch(final File file) {
	this(null, file);
    }

    public NBTFileBranch(NBTFileBranch parent, File file) {
	this.parent = parent;

	Validate.notNull(file, "file must not be null");
	if (!file.exists())
	    throw new IllegalArgumentException("file does not exist");
	this.file = file;

	this.fileCache = createFileCache();
	this.tagCache = parent == null ? createTagCache() : parent.tagCache;
	this.regionCache = parent == null ? createRegionCache()
		: parent.regionCache;
	this.branchCache = parent == null ? createBranchCache()
		: parent.branchCache;
    }

    public final NBTFileBranch getParent() {
	return parent;
    }

    public final File getFile() {
	return file;
    }

    private Cache<String, File> createFileCache() {
	return new Cache<String, File>() {
	    @Override
	    public File apply(String key) {
		return new File(file, key);
	    }
	};
    }

    protected Cache<File, CompoundTag> createTagCache() {
	return new Cache<File, CompoundTag>() {
	    @Override
	    public CompoundTag apply(File key) {
		NBTInputStream ns = null;
		try {
		    ns = new NBTInputStream(new FileInputStream(key));
		    return (CompoundTag) ns.readTag();
		} catch (IOException e) {
		    // TODO: don't be lazy
		    throw new IOError(e);
		} finally {
		    IOUtils.closeQuietly(ns);
		}
	    }
	};
    }

    protected Cache<File, Region> createRegionCache() {
	return new Cache<File, Region>() {
	    @Override
	    public Region apply(File key) {
		try {
		    return new NBTRegion(key);
		} catch (IOException e) {
		    // TODO: don't be lazy
		    throw new IOError(e);
		}
	    }
	};
    }

    protected Cache<File, NBTBranch> createBranchCache() {
	return new Cache<File, NBTBranch>() {
	    @Override
	    public NBTBranch apply(File key) {
		String[] names = key.list();
		if (ArrayUtils.contains(names, WorldDirectory.DIRECTORY_REGION))
		    return new NBTWorld(key);
		return new NBTFileBranch(NBTFileBranch.this, key);
	    }
	};
    }

    @Override
    public int getChildCount() {
	if (file.isDirectory()) {
	    String[] names = file.list();
	    return names.length;
	}

	// TODO: beautify this
	String name = file.getName();
	File parent = file.getParentFile();
	String parentName = parent.getName();
	if (name.equals("level.dat") || name.equals("level.dat_old")
		|| parentName.equals("players"))
	    return 1;

	return 0;
    }

    @Override
    public Object getChild(int index) {
	if (file.isDirectory()) {
	    String name = file.getName();
	    File[] files = file.listFiles();
	    if (name.equals("data")) {
		// TODO: data
	    } else if (name.equals("region")) {
		return regionCache.get(files[index]);
	    }

	    return branchCache.get(files[index]);
	}

	// TODO: beautify this
	String name = file.getName();
	File parent = file.getParentFile();
	String parentName = parent.getName();
	if (name.equals("level.dat") || name.equals("level.dat_old")
		|| parentName.equals("players"))
	    return tagCache.get(file);

	return null;
    }

    @Override
    public int getIndexOfChild(Object child) {
	Validate.notNull(child, "child must not be null");
	int count = getChildCount();
	for (int i = 0; i < count; i++) {
	    Object o = getChild(i);
	    if (child.equals(o))
		return i;
	}
	return -1;
    }

    @Override
    public boolean isCellEditable(int column) {
	switch (column) {
	case COLUMN_KEY:
	    return true;
	}
	return false;
    }

    @Override
    public Object getValueAt(int column) {
	switch (column) {
	case COLUMN_KEY:
	    return file.getName();
	}
	return null;
    }

    @Override
    public void setValueAt(Object value, int column) {
	Validate.notNull(value, "value must not be null");
	switch (column) {
	case COLUMN_KEY:
	    String name = value.toString();
	    File dest = fileCache.get(name);
	    file.renameTo(dest);
	}
    }

    public String toString() {
	return file.getName();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((file == null) ? 0 : file.hashCode());
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
	NBTFileBranch other = (NBTFileBranch) obj;
	if (file == null) {
	    if (other.file != null)
		return false;
	} else if (!file.equals(other.file))
	    return false;
	return true;
    }

}