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

import java.awt.Component;
import java.awt.Image;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.NBTConstants;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import com.tag.HexUtils;
import com.tag.ImageFactory;

/**
 * 
 * @author Taggart Spilman
 * 
 */
@SuppressWarnings("serial")
public class NBTTreeTable extends JXTreeTable {

	public NBTTreeTable(CompoundTag tag) {
		super(new NBTTreeTableModel(tag));
		getTreeTableModel().setParent(this);
		init();
	}

	private void init() {
		setRootVisible(true);
		setEditable(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumnModel tableColumnModel = getColumnModel();
		TableColumn valueColumn = tableColumnModel.getColumn(1);
		TableCellEditor cellEditor = new NBTTableCellEditor();
		valueColumn.setCellEditor(cellEditor);
		valueColumn.setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);

				setHorizontalAlignment(LEADING);
				if (value instanceof Number) {
					setHorizontalAlignment(TRAILING);
					String text;
					if (value instanceof Byte) {
						byte b = (Byte) value;
						text = "0x" + HexUtils.byteToHex(b);
					} else {
						NumberFormat nf = NumberFormat.getNumberInstance();
						text = nf.format(value);
					}
					setText(text);
				}

				return this;
			}

		});

		setTreeCellRenderer(new DefaultTreeCellRenderer() {

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded,
						leaf, row, hasFocus);

				if (value instanceof Tag) {
					Tag<?> tag = (Tag<?>) value;
					String name = tag.getName();
					setText(name);
				}

				final int size = 16;
				ImageFactory iconFactory = new ImageFactory();
				Image image = null;
				if (value instanceof Integer) {
					image = iconFactory.createImage(NBTConstants.TYPE_BYTE,
							size);
				} else if (value instanceof ByteTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_BYTE,
							size);
				} else if (value instanceof ShortTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_SHORT,
							size);
				} else if (value instanceof IntTag) {
					image = iconFactory
							.createImage(NBTConstants.TYPE_INT, size);
				} else if (value instanceof LongTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_LONG,
							size);
				} else if (value instanceof FloatTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_FLOAT,
							size);
				} else if (value instanceof DoubleTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_DOUBLE,
							size);
				} else if (value instanceof ByteArrayTag) {
					image = iconFactory.createImage(
							NBTConstants.TYPE_BYTE_ARRAY, size);
				} else if (value instanceof StringTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_STRING,
							size);
				} else if (value instanceof ListTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_LIST,
							size);
				} else if (value instanceof CompoundTag) {
					image = iconFactory.createImage(NBTConstants.TYPE_COMPOUND,
							size);
				}

				if (image != null) {
					Icon icon = new ImageIcon(image);
					setIcon(icon);
				}

				return this;
			}

		});

		expandAll();
	}

	public NBTTreeTableModel getTreeTableModel() {
		return (NBTTreeTableModel) super.getTreeTableModel();
	}

	public TreePath getPathForNode(Object node) {
		NBTTreeTableModel treeTableModel = getTreeTableModel();
		Tag<?> root = treeTableModel.getRoot();
		TreePath path = new TreePath(root);
		return getPathForNode(path, node);
	}

	/**
	 * This can probably be speed up if it becomes a performance bottleneck. It
	 * seems unnecessary to create the pathByAddingChild before actually
	 * checking the child.
	 */
	private TreePath getPathForNode(TreePath path, Object searchNode) {
		Object node = path.getLastPathComponent();
		if (node == searchNode)
			return path;

		NBTTreeTableModel treeTableModel = getTreeTableModel();
		for (int i = 0; i < treeTableModel.getChildCount(node); i++) {
			Object next = treeTableModel.getChild(node, i);
			TreePath childPath = path.pathByAddingChild(next);
			TreePath result = getPathForNode(childPath, searchNode);
			if (result != null)
				return result;
		}
		return null;
	}

}