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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jnbt.NBTConstants;

/**
 * This class generates and caches simple text-only icons.
 * 
 * @author Taggart Spilman
 */
public class ImageFactory {

    public ImageFactory() {

    }

    public Image readImage(String imageName, int size) throws IOException {
	final String extension = ".gif";
	String name = "/toolbarButtonGraphics/general/" + imageName + size
		+ extension;
	Class<?> c = getClass();
	URL location = c.getResource(name);
	return ImageIO.read(location);
    }

    public Image createImage(int type, int size) {
	switch (type) {
	case NBTConstants.TYPE_BYTE:
	    return createImage("B", Color.BLACK, size, size);
	case NBTConstants.TYPE_SHORT:
	    return createImage("S", Color.RED, size, size);
	case NBTConstants.TYPE_INT:
	    return createImage("I", Color.ORANGE, size, size);
	case NBTConstants.TYPE_LONG:
	    return createImage("L", Color.YELLOW, size, size);
	case NBTConstants.TYPE_FLOAT:
	    return createImage("F", Color.PINK, size, size);
	case NBTConstants.TYPE_DOUBLE:
	    return createImage("D", new Color(0x800080), size, size);
	case NBTConstants.TYPE_BYTE_ARRAY:
	    return createImage("[]", new Color(0x00CCCC), size, size);
	case NBTConstants.TYPE_STRING:
	    return createImage("Str", Color.BLACK, size, size);
	case NBTConstants.TYPE_LIST:
	    return createImage("()", Color.BLACK, size, size);
	case NBTConstants.TYPE_COMPOUND:
	    return createImage("{}", Color.BLACK, size, size);
	default:
	    throw new IllegalArgumentException("invalid type");
	}
    }

    private static BufferedImage createImage(String str, Color color,
	    int width, int height) {
	BufferedImage image = new BufferedImage(width, height,
		BufferedImage.TYPE_INT_ARGB);
	final Graphics2D g2d = image.createGraphics();

	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2d.setColor(color == null ? Color.BLACK : color);

	int size = 1;
	Font font = null;
	FontMetrics metrics;
	do {
	    font = new Font("Tahoma", Font.BOLD, size++);
	    metrics = g2d.getFontMetrics(font);
	} while (metrics.getHeight() < height
		&& metrics.charsWidth(str.toCharArray(), 0, str.length()) < width);

	int w = metrics.charsWidth(str.toCharArray(), 0, str.length());
	int x = (width - w) / 2;
	int ascent = metrics.getAscent(), descent = metrics.getDescent();
	// http://stackoverflow.com/questions/1055851/how-do-you-draw-a-string-centered-vertically-in-java/1055884#1055884
	int y = (height / 2) - ((ascent + descent) / 2) + ascent;

	String tall = "(){}[]";
	for (char c : tall.toCharArray()) {
	    if (str.charAt(0) == c) {
		y -= 2;
		break;
	    }
	}

	g2d.setFont(font);
	g2d.drawString(str, x, y);

	return image;
    }

}