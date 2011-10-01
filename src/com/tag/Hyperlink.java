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

package com.tag;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.Validate;

@SuppressWarnings("serial")
public class Hyperlink extends JButton {

    private URI uri;

    public Hyperlink(String text, String uri) {
	super(text);

	try {
	    setUri(new URI(uri));
	} catch (URISyntaxException e) {
	    throw new IllegalArgumentException(e);
	}

	init();
    }

    public Hyperlink(String text, URI uri) {
	super(text);
	setUri(uri);

	init();
    }

    private void init() {
	setHorizontalAlignment(SwingConstants.LEFT);
	setBackground(Color.WHITE);
	setBorder(null);
	setBorderPainted(false);
	setOpaque(false);

	String toolTip = getUri().toString();
	setToolTipText(toolTip);

	addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		Desktop desktop = Desktop.getDesktop();
		boolean mail = desktop.isSupported(Action.BROWSE);
		if (mail) {
		    try {
			desktop.browse(uri);
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }
		}
	    }

	});
    }

    public URI getUri() {
	return uri;
    }

    private void setUri(URI uri) {
	Validate.notNull(uri, "uri must not be null");
	this.uri = uri;
    }

}
