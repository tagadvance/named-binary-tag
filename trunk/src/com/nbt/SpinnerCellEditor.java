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
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class SpinnerCellEditor extends AbstractCellEditor implements
	TableCellEditor {

    protected JSpinner spinner;
    protected int clickCountToStart = 2;

    public SpinnerCellEditor(SpinnerNumberModel model) {
	this.spinner = new JSpinner(model);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	spinner.setValue(value);
	return spinner;
    }

    public boolean isCellEditable(EventObject e) {
	if (e instanceof MouseEvent) {
	    MouseEvent event = (MouseEvent) e;
	    int clickCount = event.getClickCount();
	    return clickCount >= clickCountToStart;
	}
	return true;
    }

    public Object getCellEditorValue() {
	return spinner.getValue();
    }

}