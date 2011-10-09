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
import java.io.IOError;
import java.io.IOException;

import com.nbt.NBTBranch;
import com.tag.Cache;
import com.terrain.Region;
import com.terrain.WorldDirectory;

public class NBTWorld extends WorldDirectory implements NBTBranch {

    private final NBTFileBranch branch;

    public NBTWorld(File base) {
	super(base);
	this.branch = new NBTFileBranch(base) {
	    @Override
	    protected Cache<File, Region> createRegionCache() {
		return getCache();
	    }
	};
    }

    @Override
    protected Region createRegion(File file) {
	try {
	    return new NBTRegion(file);
	} catch (IOException e) {
	    // TODO: don't be lazy
	    throw new IOError(e);
	}
    }

    @Override
    public int getChildCount() {
	return branch.getChildCount();
    }

    @Override
    public Object getChild(int index) {
	return branch.getChild(index);
    }

    @Override
    public int getIndexOfChild(Object child) {
	return branch.getIndexOfChild(child);
    }

}