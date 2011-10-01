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