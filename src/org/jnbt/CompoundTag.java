package org.jnbt;

/*
 * JNBT License
 * 
 * Copyright (c) 2010 Graham Edgecombe All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * * Neither the name of the JNBT team nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nbt.NBTBranch;

/**
 * The <code>TAG_Compound</code> tag.
 * 
 * @author Graham Edgecombe
 * @author Taggart Spilman
 * 
 */
public class CompoundTag extends Tag<Map<String, Tag<?>>> implements
	Mutable<Tag<?>>, NBTBranch, Searchable {

    public static final String TAG_NAME = "TAG_Compound";

    public CompoundTag(String name) {
	super(name, null);
    }

    public CompoundTag(String name, Map<String, Tag<?>> value) {
	super(name, value);
    }

    @Override
    protected Map<String, Tag<?>> createDefaultValue() {
	return new LinkedHashMap<String, Tag<?>>();
    }

    @Override
    public void add(Tag<?> value) {
	Map<String, Tag<?>> list = getValue();
	String name = value.getName();
	list.put(name, value);
    }

    @Override
    public void add(int index, Tag<?> value) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int index) {
	Map<String, Tag<?>> map = getValue();
	int i = 0;
	for (String key : map.keySet()) {
	    if (i++ == index) {
		map.remove(key);
		break;
	    }
	}
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
	case COLUMN_VALUE:
	    Map<String, Tag<?>> map = getValue();
	    int size = map.size();
	    return size + (size != 0 && size > 1 ? " entries" : " entry");
	default:
	    return super.getValueAt(column);
	}
    }

    @Override
    public Object getChild(int index) {
	Map<String, Tag<?>> map = getValue();
	Collection<Tag<?>> values = map.values();
	return values.toArray()[index];
    }

    @Override
    public int getChildCount() {
	Map<String, Tag<?>> map = getValue();
	return map.size();
    }

    @Override
    public int getIndexOfChild(Object child) {
	if (child != null) {
	    Map<String, Tag<?>> map = getValue();
	    int i = 0;
	    for (Tag<?> t : map.values()) {
		if (t.equals(child))
		    return i;
		i++;
	    }
	}
	return -1;
    }

    @Override
    public Tag<?> search(String name) {
	Map<String, Tag<?>> map = getValue();
	for (Map.Entry<String, Tag<?>> entry : map.entrySet()) {
	    String key = entry.getKey();
	    Tag<?> value = entry.getValue();
	    if (name.equals(key)) {
		return value;
	    } else if (value instanceof Searchable) {
		Searchable searchable = (Searchable) value;
		Tag<?> tag = searchable.search(name);
		if (tag != null)
		    return tag;
	    }
	}
	return null;
    }

    @Override
    public Tag<?> search(Object value) {
	throw new UnsupportedOperationException("stub");
    }

    @Override
    public String toString() {
	// TODO: dirty hack to improve performance
	StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	for (StackTraceElement element : stackTrace) {
	    if (element.getMethodName().equals("convertValueToText"))
		return "";
	}

	StringBuilder sb = new StringBuilder(TAG_NAME);
	String name = getName();
	if (!name.isEmpty())
	    sb.append("(\"").append(name).append("\")");
	Map<String, Tag<?>> map = getValue();
	int size = map.size();
	sb.append(": ").append(size).append(" entries\r\n{\r\n");
	for (Map.Entry<String, Tag<?>> entry : map.entrySet()) {
	    Tag<?> tag = entry.getValue();
	    String s = tag.toString();
	    s = s.replaceAll("\r\n", "\r\n   ");
	    sb.append("   ").append(s).append("\r\n");
	}
	sb.append("}");
	return sb.toString();
    }

}