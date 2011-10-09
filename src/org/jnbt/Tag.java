package org.jnbt;

import com.nbt.NBTNode;

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
 * Represents a single NBT tag.
 * 
 * @author Graham Edgecombe
 * @author Taggart Spilman
 * 
 */
// TODO: decouple NBTNode
public abstract class Tag<T> implements NBTNode {

    /**
     * The name of this tag.
     */
    private String name;
    private T value;

    public Tag() {

    }

    /**
     * Creates the tag with the specified name.
     * 
     * @param name
     *            The name.
     */
    public Tag(String name) {
	this(name, null);
    }

    /**
     * Creates the tag with the specified name.
     * 
     * @param name
     *            The name.
     * @param value
     *            The Value.
     */
    public Tag(String name, T value) {
	setName(name);
	setValue(value);
    }

    /**
     * Gets the name of this tag.
     * 
     * @return The name of this tag.
     */
    public final String getName() {
	return name;
    }

    public final void setName(String name) {
	if (name == null)
	    name = createDefaultName();
	this.name = name;
    }

    protected String createDefaultName() {
	return "";
    }

    /**
     * Gets the value of this tag.
     * 
     * @return The value of this tag.
     * @throws IllegalStateException
     */
    public T getValue() {
	if (value == null)
	    setValue(null);
	return this.value;
    }

    /**
     * Sets the value of this <code>Tag</code> to the specified value.
     * 
     * @param value
     *            the new value to be set
     */
    public void setValue(T value) {
	if (value == null)
	    value = createDefaultValue();
	this.value = value;
    }

    protected T createDefaultValue() {
	throw new IllegalStateException();
    }

    protected String toString(String tagType) {
	StringBuilder sb = new StringBuilder(tagType);
	String name = getName();
	if (!name.isEmpty())
	    sb.append("(\"").append(name).append("\")");
	sb.append(": ").append(value);
	return sb.toString();
    }

    @Override
    public boolean isCellEditable(int column) {
	return true;
    }

    @Override
    public Object getValueAt(int column) {
	switch (column) {
	case COLUMN_KEY:
	    return getName();
	case COLUMN_VALUE:
	    return getValue();
	default:
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValueAt(Object value, int column) {
	switch (column) {
	case COLUMN_KEY:
	    String name = (value == null ? null : value.toString());
	    setName(name);
	    break;
	case COLUMN_VALUE:
	    setValue((T) value);
	    break;
	}
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	Tag other = (Tag) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }

}