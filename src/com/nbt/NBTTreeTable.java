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
import java.io.File;
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
import org.jnbt.ByteArrayTag.ByteWrapper;
import org.jnbt.ByteTag;
import org.jnbt.NBTConstants;

import resources.Resource;

import com.google.common.base.Function;
import com.nbt.data.Register;
import com.nbt.data.Sprite;
import com.nbt.data.SpriteRecord;
import com.nbt.world.NBTFileBranch;
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

    static final Register<SpriteRecord> register;
    static {
	register = new Register<SpriteRecord>() {
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
    }

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
	valueColumn.setCellEditor(new NBTTableCellEditor());
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

	ImageFactory imageFactory = new ImageFactory();
	int size = 16;
	final Image byteImage = imageFactory
		.createImage(ByteTag.TAG_TYPE, size);
	final Image compoundImage = imageFactory.createImage(
		NBTConstants.TYPE_COMPOUND, size);
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

		Image image = null;
		if (value instanceof Block) {
		    Block block = (Block) value;
		    String name = block.getName();
		    setText(name);

		    int id = block.getBlockID();
		    SpriteRecord record = register.getRecord(id);
		    if (record != null)
			image = record.getImage();
		} else if (value instanceof Sprite) {
		    Sprite sprite = (Sprite) value;
		    image = sprite.getImage();
		} else if (value instanceof ByteWrapper) {
		    image = byteImage;
		} else if (value instanceof NBTFileBranch.TagWrapper) {
		    image = compoundImage;
		}

		if (image != null) {
		    Icon icon = new ImageIcon(image);
		    setIcon(icon);
		}

		if (value instanceof NBTFileBranch) {
		    NBTFileBranch branch = (NBTFileBranch) value;
		    File file = branch.getFile();
		    if (file.isFile()) {
			Icon icon = getLeafIcon();
			setIcon(icon);
		    }
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
	final TreePath path = event.getPath();
	final Object source = path.getLastPathComponent();
	if (!(source instanceof LazyBranch))
	    return;

	final LazyBranch lazyBranch = (LazyBranch) source;
	if (lazyBranch.isPopulated())
	    return;

	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	setCursor(waitCursor);

	SwingWorkerUnlimited.execure(new SwingWorker<Void, Void>() {

	    @Override
	    protected Void doInBackground() throws Exception {
		lazyBranch.getChildren();
		return null;
	    }

	    @Override
	    protected void done() {
		treeExpanded(source, path);
	    }

	});
    }

    protected void treeExpanded(Object source, TreePath path) {
	NBTTreeTableModel model = getTreeTableModel();
	model.fireTreeNodesInserted(source, path);

	Cursor defaultCursor = Cursor.getDefaultCursor();
	setCursor(defaultCursor);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event)
	    throws ExpandVetoException {
    }

    public TreePath getPath() {
	int row = getSelectedRow();
	return getPathForRow(row);
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
    private TreePath getPathForNode(TreePath path, final Object searchNode) {
	Object node = path.getLastPathComponent();
	if (node == searchNode) {
	    return path;
	}

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

    /**
     * function should return <code>true</code> if its children should be
     * iterated over
     */
    public void iterate(Function<TreePath, Boolean> function) {
	NBTTreeTableModel model = getTreeTableModel();
	Object root = model.getRoot();
	TreePath path = new TreePath(root);
	iterate(path, function);
    }
    
    // TODO: add enum BranchStatement
    public void iterate(TreePath path,
	    final Function<TreePath, Boolean> function) {
	Object node = path.getLastPathComponent();
	NBTTreeTableModel treeTableModel = getTreeTableModel();
	for (int i = 0; i < treeTableModel.getChildCount(node); i++) {
	    Object next = treeTableModel.getChild(node, i);
	    TreePath childPath = path.pathByAddingChild(next);
	    if (function.apply(childPath))
		iterate(childPath, function);
	}
    }

}