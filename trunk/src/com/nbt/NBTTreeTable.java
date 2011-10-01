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

package com.nbt;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteArrayTag.ByteWrapper;
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

import resources.Resource;

import com.nbt.data.Register;
import com.nbt.data.SpriteRecord;
import com.tag.HexUtils;
import com.tag.ImageFactory;
import com.tag.SwingWorkerUnlimited;
import com.terrain.Block;

/**
 * 
 * @author Taggart Spilman
 * 
 */
@SuppressWarnings("serial")
public class NBTTreeTable extends JXTreeTable implements TreeWillExpandListener {

    public NBTTreeTable(Object root) {
	super(new NBTTreeTableModel(root));
	getTreeTableModel().setParent(this);
	init();
    }

    private void init() {
	setRootVisible(true);
	setEditable(true);
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	addTreeWillExpandListener(this);

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
			text = "0x" + HexUtils.toHex(b);
		    } else {
			NumberFormat nf = NumberFormat.getNumberInstance();
			text = nf.format(value);
		    }
		    setText(text);
		}

		return this;
	    }

	});

	// TODO: find a better spot for this
	final Register<SpriteRecord> register = new Register<SpriteRecord>() {
	    @Override
	    protected SpriteRecord createRecord(String[] row) {
		return new SpriteRecord(row);
	    }
	};
	Resource resource = new Resource();
	List<String[]> blocks = resource.getCSV("csv/blocks.csv");
	register.load(blocks);
	List<String[]> items = resource.getCSV("csv/items.csv");
	register.load(items);

	setTreeCellRenderer(new DefaultTreeCellRenderer() {

	    @Override
	    public Component getTreeCellRendererComponent(JTree tree,
		    Object value, boolean isSelected, boolean isExpanded,
		    boolean isLeaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, isSelected,
			isExpanded, isLeaf, row, hasFocus);

		if (value instanceof NBTNode) {
		    NBTNode node = (NBTNode) value;
		    Object o = node.getValueAt(NBTNode.COLUMN_KEY);
		    String text = (o == null ? null : o.toString());
		    setText(text);
		}

		final int size = 16;
		ImageFactory iconFactory = new ImageFactory();
		Image image = null;
		if (value instanceof Block) {
		    Block block = (Block) value;
		    String name = block.getName();
		    setText(name);

		    int id = block.getBlockID();
		    SpriteRecord record = register.getRecord(id);
		    if (record != null)
			image = record.getImage();
		} else if (value instanceof ByteWrapper) {
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
    }

    public NBTTreeTableModel getTreeTableModel() {
	return (NBTTreeTableModel) super.getTreeTableModel();
    }

    @Override
    public void treeWillExpand(final TreeExpansionEvent event)
	    throws ExpandVetoException {
	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	setCursor(waitCursor);

	// TODO: clean this up
	final TreePath path = event.getPath();
	final Object source = path.getLastPathComponent();
	if (!(source instanceof LazyBranch)) {
	    resetCursor();
	    return;
	}
	final LazyBranch lazyBranch = (LazyBranch) source;
	if (lazyBranch.isPopulated()) {
	    resetCursor();
	    return;
	}
	SwingWorkerUnlimited.execure(new SwingWorker<Void, Void>() {

	    @Override
	    protected Void doInBackground() throws Exception {
		lazyBranch.getChildren();
		return null;
	    }

	    @Override
	    protected void done() {
		NBTTreeTableModel model = getTreeTableModel();
		Object[] children = lazyBranch.getChildren();
		int[] childIndices = new int[children.length];
		for (int i = 0; i < children.length; i++)
		    childIndices[i] = i;
		model.fireTreeNodesInserted(source, path, childIndices,
			children);

		resetCursor();
	    }

	});
    }

    private void resetCursor() {
	Cursor cursor = Cursor.getDefaultCursor();
	setCursor(cursor);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event)
	    throws ExpandVetoException {
    }

    public TreePath getPathForNode(Object node) {
	NBTTreeTableModel treeTableModel = getTreeTableModel();
	Object root = treeTableModel.getRoot();
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

	// TODO: performance fix
	if (searchNode instanceof LazyBranch) {
	    LazyBranch lazy = (LazyBranch) searchNode;
	    if (!lazy.isPopulated())
		return null;
	}

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