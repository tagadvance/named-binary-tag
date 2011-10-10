package org.jnbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.nbt.NBTBranch;
import com.nbt.NBTNode;
import com.tag.HexUtils;

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

/**
 * The <code>TAG_Byte_Array</code> tag.
 * 
 * @author Graham Edgecombe
 * @author Taggart Spilman
 * 
 */
// TODO: Saveable here is a hack and should be removed
public class ByteArrayTag extends Tag<byte[]> implements Mutable<Byte>,
	NBTBranch {

    public static final String TAG_NAME = "TAG_Byte_Array";
    public static final int TAG_TYPE = 7;

    private boolean modified;

    public ByteArrayTag(String name) {
	super(name);
    }

    public ByteArrayTag(String name, byte[] value) {
	super(name, value);
    }

    @Override
    protected byte[] createDefaultValue() {
	return new byte[] {};
    }

    @Override
    public int getTagType() {
	return TAG_TYPE;
    }

    @Override
    public void add(Byte b) {
	byte[] value = getValue();
	byte[] newValue = new byte[value.length + 1];
	System.arraycopy(value, 0, newValue, 0, value.length);
	setValue(newValue);
    }

    @Override
    public void add(int index, Byte b) {
	byte[] value = getValue();
	Byte[] bytes = ArrayUtils.toObject(value);
	List<Byte> list = new ArrayList<Byte>(Arrays.asList(bytes));
	list.add(index, (byte) 0);
	bytes = list.toArray(bytes);
	value = ArrayUtils.toPrimitive(bytes);
	setValue(value);
    }

    @Override
    public void remove(int index) {
	byte[] value = getValue();
	Byte[] bytes = ArrayUtils.toObject(value);
	List<Byte> list = new ArrayList<Byte>(Arrays.asList(bytes));
	list.remove(index);
	bytes = new Byte[list.size()];
	list.toArray(bytes);
	value = ArrayUtils.toPrimitive(bytes);
	setValue(value);
    }

    @Override
    public boolean isCellEditable(int column) {
	return false;
    }

    @Override
    public Object getValueAt(int column) {
	switch (column) {
	case COLUMN_VALUE:
	    byte[] bytes = getValue();
	    return bytes.length + " bytes";
	default:
	    return super.getValueAt(column);
	}
    }

    @Override
    public Object getChild(int index) {
	return new ByteWrapper(index);
    }

    @Override
    public int getChildCount() {
	byte[] value = getValue();
	return value.length;
    }

    @Override
    public int getIndexOfChild(Object child) {
	if (child instanceof ByteWrapper) {
	    ByteWrapper wrapper = (ByteWrapper) child;
	    return wrapper.getIndex();
	}
	return -1;
    }

    @Override
    public void mark() {
	this.modified = false;
    }

    @Override
    public boolean hasChanged() {
	return this.modified;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder(TAG_NAME);
	String name = getName();
	if (!name.isEmpty())
	    sb.append("(\"").append(name).append("\")");
	sb.append(":");
	for (byte b : getValue()) {
	    sb.append(" ");
	    String hex = HexUtils.toHex(b);
	    sb.append(hex);
	}
	return sb.toString();
    }

    public class ByteWrapper implements NBTNode {

	private final int index;

	public ByteWrapper(int index) {
	    this.index = index;
	}

	public ByteArrayTag getTag() {
	    return ByteArrayTag.this;
	}

	public int getIndex() {
	    return this.index;
	}

	@Override
	public boolean isCellEditable(int column) {
	    switch (column) {
	    case NBTNode.COLUMN_VALUE:
		return true;
	    }
	    return false;
	}

	@Override
	public Object getValueAt(int column) {
	    switch (column) {
	    case NBTNode.COLUMN_KEY:
		return index;
	    case NBTNode.COLUMN_VALUE:
		byte[] bytes = getValue();
		return bytes[index];
	    }
	    return null;
	}

	@Override
	public void setValueAt(Object value, int column) {
	    switch (column) {
	    case NBTNode.COLUMN_VALUE:
		if (value instanceof Integer) {
		    int i = (Integer) value;
		    byte[] bytes = getValue();
		    bytes[index] = (byte) i;
		    modified = true;
		}
	    }
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    ByteWrapper other = (ByteWrapper) obj;
	    // TODO: clean up this train wreck
	    if (!Arrays.equals(getTag().getValue(), other.getTag().getValue()))
		return false;
	    if (index != other.index)
		return false;
	    return true;
	}

    }

}