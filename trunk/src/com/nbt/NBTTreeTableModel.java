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

import java.io.File;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import com.nbt.world.NBTFileBranch;

public class NBTTreeTableModel extends AbstractTreeTableModel {

    static protected String[] columnNames = { "Name", "Value" };

    // TODO: this violates MVC
    protected NBTTreeTable parent;

    public NBTTreeTableModel(Object root) {
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
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
	return columnNames[column];
    }

    @Override
    public boolean isLeaf(Object node) {
	if (node instanceof LazyBranch) {
	    LazyBranch lazyBranch = (LazyBranch) node;
	    return !lazyBranch.hasChildren();
	}

	if (node instanceof NBTFileBranch) {
	    NBTFileBranch branch = (NBTFileBranch) node;
	    File file = branch.getFile();
	    if (file.isDirectory())
		return false;
	}

	return super.isLeaf(node);
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
	if (node instanceof NBTNode) {
	    NBTNode n = (NBTNode) node;
	    return n.isCellEditable(column);
	}

	return super.isCellEditable(node, column);
    }

    @Override
    public Object getValueAt(Object node, int column) {
	if (node instanceof NBTNode) {
	    NBTNode n = (NBTNode) node;
	    return n.getValueAt(column);
	}

	return null;
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
	if (node instanceof NBTNode) {
	    NBTNode n = (NBTNode) node;
	    n.setValueAt(value, column);
	    return;
	}

	super.setValueAt(value, node, column);
    }

    @Override
    public Object getChild(Object parent, int index) {
	if (parent instanceof NBTBranch) {
	    NBTBranch branch = (NBTBranch) parent;
	    return branch.getChild(index);
	}
	return null;
    }

    @Override
    public int getChildCount(Object parent) {
	if (parent instanceof LazyBranch) {
	    LazyBranch lazyBranch = (LazyBranch) parent;
	    if (lazyBranch.isPopulated()) {
		Object[] children = lazyBranch.getChildren();
		return children.length;
	    }
	    return 0;
	}
	if (parent instanceof NBTBranch) {
	    NBTBranch branch = (NBTBranch) parent;
	    return branch.getChildCount();
	}
	return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
	if (parent instanceof NBTBranch) {
	    NBTBranch branch = (NBTBranch) parent;
	    return branch.getIndexOfChild(child);
	}
	return -1;
    }

    protected void fireTreeNodesInserted(Object source, TreePath path,
	    Object... children) {
	Object parent = path.getLastPathComponent();
	int length = children.length;
	int[] childIndices = new int[length];
	for (int i = 0; i < length; i++) {
	    Object child = children[i];
	    childIndices[i] = getIndexOfChild(parent, child);
	    if (childIndices[i] == -1)
		throw new IllegalArgumentException("child index is -1");
	}
	fireTreeNodesInserted(source, path, childIndices, children);
    }

    protected void fireTreeNodesInserted(Object source, TreePath path) {
	Object parent = path.getLastPathComponent();
	int count = getChildCount(parent);
	int[] childIndices = new int[count];
	Object[] children = new Object[count];
	for (int i = 0; i < count; i++) {
	    childIndices[i] = i;
	    children[i] = getChild(parent, i);
	}
	fireTreeNodesInserted(source, path, childIndices, children);
    }

    /**
     * @see TreeModelEvent#TreeModelEvent(Object, Object[], int[], Object[])
     * @see TreeModelEvent#TreeModelEvent(Object, TreePath, int[], Object[])
     * @see TreeModelListener#treeNodesInserted(TreeModelEvent)
     */
    protected void fireTreeNodesInserted(Object source, TreePath path,
	    int[] childIndices, Object[] children) {
	TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
		children);
	TreeModelListener[] listeners = getTreeModelListeners();
	ArrayUtils.reverse(listeners);
	for (TreeModelListener listener : listeners)
	    listener.treeNodesInserted(event);
    }

}