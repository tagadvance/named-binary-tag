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

		int size = 8;
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