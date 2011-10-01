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
	    final float MIN_VALUE = Float.MAX_VALUE * -1;
	    editor = new SpinnerCellEditor(new SpinnerNumberModel(
		    (Number) value, MIN_VALUE, Float.MAX_VALUE, stepSize));
	} else if (value instanceof Double) {
	    // http://stackoverflow.com/questions/3884793/minimum-values-and-double-min-value-in-java
	    final double MIN_VALUE = Double.MAX_VALUE * -1;
	    editor = new SpinnerCellEditor(new SpinnerNumberModel(
		    (Number) value, MIN_VALUE, Double.MAX_VALUE, stepSize));
	} else {
	    return null;
	}

	return editor.getTableCellEditorComponent(table, value, isSelected,
		row, column);
    }

}