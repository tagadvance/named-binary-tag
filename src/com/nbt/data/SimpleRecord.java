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

package com.nbt.data;

public class SimpleRecord implements Record {

    private static final int EXPECTED_LENGTH = 3;
    private static final int INDEX_NAME = 0, INDEX_ID = 1, INDEX_ICON = 2;

    private final String name;
    private final int id, iconIndex;

    /**
     * 
     * @param row
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public SimpleRecord(String[] row) {
	if (row.length < EXPECTED_LENGTH)
	    throw new IllegalArgumentException("row is too short");
	this.name = row[INDEX_NAME].trim();
	this.id = Integer.parseInt(row[INDEX_ID].trim());
	this.iconIndex = Integer.parseInt(row[INDEX_ICON].trim());
    }

    @Override
    public String getName() {
	return this.name;
    }

    @Override
    public int getID() {
	return this.id;
    }

    @Override
    public int getIconIndex() {
	return this.iconIndex;
    }

    public String toString() {
	return getName() + ", " + getID() + ", " + getIconIndex();
    }

}