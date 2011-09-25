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