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
