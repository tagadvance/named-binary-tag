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

import com.nbt.NBTNode;
import com.terrain.WorldBlock;
import com.terrain.WorldChunk;

public class NBTBlock extends WorldBlock implements NBTNode {

    public NBTBlock(WorldChunk chunk, int x, int y, int z) {
	super(chunk, x, y, z);
    }

    @Override
    public boolean isCellEditable(int column) {
	switch (column) {
	case COLUMN_VALUE:
	    return true;
	}
	return false;
    }

    @Override
    public Object getValueAt(int column) {
	switch (column) {
	case COLUMN_KEY:
	    return getName();
	case COLUMN_VALUE:
	    return getBlockID();
	}
	return null;
    }

    @Override
    public void setValueAt(Object value, int column) {
	switch (column) {
	case COLUMN_VALUE:
	    if (value instanceof Integer) {
		int blockID = (Integer) value;
		setBlockID(blockID);
	    }
	}
    }

}