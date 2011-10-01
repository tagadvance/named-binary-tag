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

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.LazyBranch;
import com.nbt.NBTBranch;
import com.terrain.WorldBlock;
import com.terrain.WorldChunk;

public class NBTBlock extends WorldBlock implements NBTBranch {

    private Object[] children;

    public NBTBlock(WorldChunk chunk, int x, int y, int z) {
	super(chunk, x, y, z);
    }

    @Override
    public int getChildCount() {
	Object[] children = getChildren();
	return children.length;
    }

    @Override
    public Object getChild(int index) {
	Object[] children = getChildren();
	return children[index];
    }

    @Override
    public int getIndexOfChild(Object child) {
	Object[] children = getChildren();
	return ArrayUtils.indexOf(children, child);
    }

//    @Override
//    public boolean hasChildren() {
//	return true;
//    }
//
//    @Override
//    public boolean isPopulated() {
//	return children != null;
//    }
//
//    @Override
    public Object[] getChildren() {
	if (children == null)
	    children = new Object[] {};
	return children;
    }

}