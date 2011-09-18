/*
 * Copyright 2011 Taggart Spilman. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Taggart Spilman ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Taggart Spilman OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Taggart Spilman.
 */

package com.nbt;

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

public class NBTTreeTableModel extends AbstractTreeTableModel {

	static protected String[] columnNames = {
			"Name", "Value"
	};

	protected NBTTreeTable parent;

	public NBTTreeTableModel(CompoundTag root) {
		super(root);
	}

	public NBTTreeTable getParent() {
		if (this.parent == null)
			throw new IllegalStateException("oops...");
		return this.parent;
	}

	protected void setParent(NBTTreeTable parent) {
		this.parent = parent;
	}

	@Override
	public CompoundTag getRoot() {
		return (CompoundTag) root;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		switch (column) {
			case 0:
				return (node instanceof Tag);
			case 1:
				return (node instanceof Tag || node instanceof Integer)
						&& !(node instanceof ByteArrayTag
								|| node instanceof ListTag || node instanceof CompoundTag);
		}
		return super.isCellEditable(node, column);
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof Tag) {
			Tag<?> tag = (Tag<?>) node;
			switch (column) {
				case 0:
					return tag.getName();
				case 1:
					Object value = tag.getValue();
					if (value instanceof byte[]) {
						byte[] bytes = (byte[]) value;
						return bytes.length + " bytes";
					} else if (value instanceof List) {
						List list = (List) value;
						int size = list.size();
						return size
								+ (size != 0 && size > 1 ? " entries"
										: " entry");
					} else if (value instanceof Map) {
						@SuppressWarnings("rawtypes")
						Map map = (Map) value;
						int size = map.size();
						return size
								+ (size != 0 && size > 1 ? " entries"
										: " entry");
					} else {
						return value;
					}
			}
		} else if (node instanceof Integer) {
			int index = (Integer) node;
			NBTTreeTable parent = getParent();
			TreePath path = parent.getPathForNode(node);
			path = path.getParentPath();
			Object parentNode = path.getLastPathComponent();
			switch (column) {
				case 1:
					if (parentNode instanceof ByteArrayTag) {
						ByteArrayTag tag = (ByteArrayTag) parentNode;
						byte[] bytes = tag.getValue();
						return bytes[index];
					}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object value, Object node, int column) {
		switch (column) {
			case 0:
				if (node instanceof Tag) {
					Tag<?> tag = (Tag<?>) node;
					String name = (value == null ? null : value.toString());
					tag.setName(name);
				}
				break;
			case 1:
				if (node instanceof Tag) {
					System.out.println(node.getClass());
					System.out.println(value.getClass());
					@SuppressWarnings("rawtypes")
					Tag tag = (Tag) node;
					tag.setValue(value);
				} else if (node instanceof Integer) {
					int index = (Integer) node;
					NBTTreeTable parent = getParent();
					TreePath path = parent.getPathForNode(node);
					path = path.getParentPath();
					Object parentNode = path.getLastPathComponent();
					if (parentNode instanceof ByteArrayTag) {
						ByteArrayTag tag = (ByteArrayTag) parentNode;
						byte[] bytes = tag.getValue();
						if (value instanceof Number) {
							Number n = (Number) value;
							bytes[index] = n.byteValue();
						}
					}
				}
		}

		super.setValueAt(value, node, column);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof ByteArrayTag) {
			return index;
		} else if (parent instanceof ListTag) {
			ListTag tag = (ListTag) parent;
			List<?> list = tag.getValue();
			return list.get(index);
		} else if (parent instanceof CompoundTag) {
			CompoundTag tag = (CompoundTag) parent;
			Map<String, Tag<?>> map = tag.getValue();
			return map.values().toArray()[index];
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ByteArrayTag) {
			ByteArrayTag tag = (ByteArrayTag) parent;
			byte[] bytes = tag.getValue();
			return bytes.length;
		} else if (parent instanceof ListTag) {
			ListTag tag = (ListTag) parent;
			List<?> list = tag.getValue();
			return list.size();
		} else if (parent instanceof CompoundTag) {
			CompoundTag tag = (CompoundTag) parent;
			Map<String, Tag<?>> map = tag.getValue();
			return map.size();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		int i = 0;
		if (parent instanceof ByteArrayTag) {
			ByteArrayTag tag = (ByteArrayTag) parent;
			byte[] bytes = tag.getValue();
			for (byte b : bytes) {
				i++;
				Byte oByte = Byte.valueOf(b);
				if (oByte.equals(child))
					return i;
			}
		} else if (parent instanceof ListTag) {
			ListTag tag = (ListTag) parent;
			List<?> list = tag.getValue();
			for (Object o : list) {
				i++;
				if (o.equals(child))
					return i;
			}
		} else if (parent instanceof CompoundTag) {
			CompoundTag tag = (CompoundTag) parent;
			Map<String, Tag<?>> map = tag.getValue();
			for (Tag<?> t : map.values()) {
				i++;
				if (t.equals(child))
					return i;
			}
		}
		return -1;
	}

}
