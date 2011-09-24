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

import java.util.ArrayList;
import java.util.List;

import com.nbt.NBTBranch;

/**
 * The <code>TAG_List</code> tag.
 * 
 * @author Graham Edgecombe
 * @author Taggart Spilman
 * 
 */
public class ListTag<E extends Tag<?>> extends Tag<List<E>> implements
	NBTBranch {

    /**
     * The type.
     */
    private Class<E> type;

    /**
     * Creates the tag.
     * 
     * @param name
     *            The name.
     * @param type
     *            The type of item in the list.
     * @param value
     *            The value.
     */
    public ListTag(String name, List<E> value, Class<E> type) {
	super(name, value);
	setType(type);
    }

    /**
     * Gets the type of item in this list.
     * 
     * @return The type of item in this list.
     */
    public Class<E> getType() {
	return type;
    }

    private void setType(Class<E> type) {
	if (type == null)
	    throw new IllegalArgumentException("type must not be null");
	this.type = type;
    }

    @Override
    protected List<E> createDefaultValue() {
	return new ArrayList<E>();
    }

    @Override
    public boolean isCellEditable(int column) {
	return false;
    }

    @Override
    public Object getValueAt(int column) {
	switch (column) {
	case COLUMN_VALUE:
	    List<E> list = getValue();
	    int size = list.size();
	    return size + (size != 0 && size > 1 ? " entries" : " entry");
	default:
	    return super.getValueAt(column);
	}
    }

    @Override
    public Object getChild(int index) {
	List<?> list = getValue();
	return list.get(index);
    }

    @Override
    public int getChildCount() {
	List<?> list = getValue();
	return list.size();
    }

    @Override
    public int getIndexOfChild(Object child) {
	if (child != null) {
	    List<E> list = getValue();
	    for (int i = 0; i < list.size(); i++) {
		Tag<?> tag = list.get(i);
		if (child.equals(tag))
		    return i;
	    }
	}
	return -1;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder("TAG_List");
	String name = getName();
	if (!name.isEmpty())
	    sb.append("(\"").append(name).append("\")");
	List<E> value = getValue();
	int size = value.size();
	Class<E> type = getType();
	String typeName = NBTUtils.getTypeName(type);
	sb.append(": ").append(size).append(" entries of type ")
		.append(typeName).append("\r\n{\r\n");
	for (Tag<?> tag : value) {
	    String s = tag.toString();
	    s = s.replaceAll("\r\n", "\r\n   ");
	    sb.append("   ").append(s).append("\r\n");
	}
	sb.append("}");
	return sb.toString();
    }

}
