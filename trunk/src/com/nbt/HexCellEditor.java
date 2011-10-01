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

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.tag.HexUtils;

@SuppressWarnings("serial")
public class HexCellEditor extends SpinnerCellEditor {

    static final int MINIMUM = 0;
    static final int MAXIMUM = 0xFF;
    static final int STEP_SIZE = 1;

    public HexCellEditor(byte b) {
	this(b & 0xFF);
    }

    public HexCellEditor(int value) {
	super(new SpinnerNumberModel(value, MINIMUM, MAXIMUM, STEP_SIZE));
	spinner.setEditor(new HexEditor(spinner));
    }

    class HexEditor extends DefaultEditor {

	public HexEditor(JSpinner spinner) {
	    super(spinner);

	    JFormattedTextField ftf = getTextField();
	    ftf.setEditable(true);
	    ftf.setColumns(2);
	    ftf.setHorizontalAlignment(JTextField.RIGHT);
	    ftf.setFormatterFactory(new DefaultFormatterFactory(
		    new HexFormatter()));
	}

    }

    class HexFormatter extends DefaultFormatter {

	private HexFormatter() {
	    super();
	}

	public Object stringToValue(String string) throws ParseException {
	    try {
		int radix = 16;
		return Integer.valueOf(string, radix);
	    } catch (NumberFormatException nfe) {
		int errorOffset = 0;
		throw new ParseException(string, errorOffset);
	    }
	}

	@Override
	public String valueToString(Object value) throws ParseException {
	    if (!(value instanceof Number)) {
		int errorOffset = 0;
		throw new ParseException("" + value, errorOffset);
	    }

	    Number n = (Number) value;
	    int i = n.intValue();
	    return HexUtils.toHex(i);
	}

    }

}