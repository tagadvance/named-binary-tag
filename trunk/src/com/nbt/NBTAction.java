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

import java.awt.Image;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.tag.ImageFactory;

@SuppressWarnings("serial")
abstract class NBTAction extends AbstractAction {
	
	public static final int smallIconSize = 16, largeIconSize = 24;
	
	public NBTAction(String name, String imageName, String toolTip,
			Integer mnemonic) {
		this(name, toolTip, mnemonic);

		ImageFactory factory = new ImageFactory();
		try {
			Image image = factory.readImage(imageName, smallIconSize);
			setSmallIcon(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Image image = factory.readImage(imageName, largeIconSize);
			setLargeIcon(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public NBTAction(String name, int type, String toolTip, Integer mnemonic) {
		this(name, toolTip, mnemonic);

		ImageFactory factory = new ImageFactory();
		Image image = factory.createImage(type, smallIconSize);
		setSmallIcon(image);

		image = factory.createImage(type, largeIconSize);
		setLargeIcon(image);
	}

	public NBTAction(String name, String toolTip, Integer mnemonic) {
		super(name);
		putValue(SHORT_DESCRIPTION, toolTip);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	protected void setSmallIcon(Image image) {
		Icon icon = new ImageIcon(image);
		putValue(SMALL_ICON, icon);
	}

	protected void setLargeIcon(Image image) {
		Icon icon = new ImageIcon(image);
		putValue(LARGE_ICON_KEY, icon);
	}

}