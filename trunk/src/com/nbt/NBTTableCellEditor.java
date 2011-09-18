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

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class NBTTableCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private TableCellEditor editor;

	@Override
	public Object getCellEditorValue() {
		if (editor != null) {
			return editor.getCellEditorValue();
		}

		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		final int stepSize = 1;
		if (value instanceof String) {
			editor = new DefaultCellEditor(new JTextField());
		} else if (value instanceof Boolean) {
			editor = new DefaultCellEditor(new JCheckBox());
		} else if (value instanceof Byte) {
			byte b = (Byte) value;
			editor = new HexCellEditor(b);
		} else if (value instanceof Short) {
			editor = new SpinnerCellEditor(new SpinnerNumberModel(
					(Number) value, Short.MIN_VALUE, Short.MAX_VALUE, stepSize));
		} else if (value instanceof Integer) {
			editor = new SpinnerCellEditor(new SpinnerNumberModel(
					(Number) value, Integer.MIN_VALUE, Integer.MAX_VALUE,
					stepSize));
		} else if (value instanceof Long) {
			editor = new SpinnerCellEditor(new SpinnerNumberModel(
					(Number) value, Long.MIN_VALUE, Long.MAX_VALUE, stepSize));
		} else if (value instanceof Float) {
			editor = new SpinnerCellEditor(new SpinnerNumberModel(
					(Number) value, Float.MIN_VALUE, Float.MAX_VALUE, stepSize));
		} else if (value instanceof Double) {
			editor = new SpinnerCellEditor(new SpinnerNumberModel(
					(Number) value, Double.MIN_VALUE, Double.MAX_VALUE,
					stepSize));
		} else {
			return null;
		}

		return editor.getTableCellEditorComponent(table, value, isSelected,
				row, column);
	}

}