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
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.NBTBranch;
import com.terrain.Region;
import com.terrain.WorldDirectory;

public class NBTWorld extends WorldDirectory implements NBTBranch {

    public NBTWorld(File base) {
	super(base);
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
	List<Region> regions = getRegions();
	return regions.size();
    }

    @Override
    public Object getChild(int index) {
	List<Region> regions = getRegions();
	return regions.get(index);
    }

    @Override
    public int getIndexOfChild(Object child) {
	List<Region> regions = getRegions();
	Object[] array = regions.toArray();
	return ArrayUtils.indexOf(array, child);
    }

}