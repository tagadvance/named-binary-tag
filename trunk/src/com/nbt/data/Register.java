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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

public abstract class Register<E extends Record> {

    protected final Map<Integer, E> map;

    public Register() {
	this.map = new LinkedHashMap<Integer, E>();
    }

    public void load(List<String[]> rows) {
	Validate.notNull(rows, "rows must not be null");
	for (String[] row : rows) {
	    E value = createRecord(row);
	    int key = value.getID();
	    map.put(key, value);
	}
    }

    protected abstract E createRecord(String[] row);

    public E getRecord(byte b) {
	int id = 0xFF & b;
	return getRecord(id);
    }

    public E getRecord(int id) {
	return map.get(id);
    }

    public void clear() {
	map.clear();
    }

}