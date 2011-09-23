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

import java.awt.Point;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jnbt.ByteArrayTag;

public class NBTTreeTableModel extends AbstractTreeTableModel {

	static protected String[] columnNames = {
			"Name", "Value"
	};

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
	public boolean isCellEditable(Object node, int column) {
		if (node instanceof Node) {
			Node n = (Node) node;
			return n.isCellEditable(column);
		}

		return super.isCellEditable(node, column);
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof Node) {
			Node n = (Node) node;
			return n.getValueAt(column);
		}

		return null;
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		if (node instanceof Node) {
			Node n = (Node) node;
			n.setValueAt(value, column);
			return;
		}

		super.setValueAt(value, node, column);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof Branch) {
			Branch branch = (Branch) parent;
			return branch.getChild(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof Branch) {
			Branch branch = (Branch) parent;
			return branch.getChildCount();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof Branch) {
			Branch branch = (Branch) parent;
			return branch.getIndexOfChild(child);
		}
		return -1;
	}

}
