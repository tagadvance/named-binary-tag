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

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_DEFAULT;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_DEFAULT;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
import static java.awt.RenderingHints.VALUE_RENDER_DEFAULT;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_RENDER_SPEED;
import static java.awt.image.BufferedImage.TYPE_CUSTOM;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

public class Thumbnail {

    public static final int SPEED = -1, DEFAULT = 0, QUALITY = 1;

    // hide constructor
    private Thumbnail() {

    }

    public static BufferedImage createCroppedThumbnail(BufferedImage image,
	    Dimension maximumSize) {
	Map<RenderingHints.Key, ?> hints = null;
	return createCroppedThumbnail(image, maximumSize, hints);
    }

    public static BufferedImage createCroppedThumbnail(BufferedImage image,
	    Dimension maximumSize, Map<RenderingHints.Key, ?> hints) {
	Dimension size = scale(image, maximumSize);
	return createThumbnail(image, size, hints);
    }

    public static BufferedImage createLetterboxThumbnail(BufferedImage image,
	    Dimension maximumSize) {
	Map<RenderingHints.Key, ?> hints = null;
	return createLetterboxThumbnail(image, maximumSize, hints);
    }

    public static BufferedImage createLetterboxThumbnail(BufferedImage image,
	    Dimension maximumSize, Map<RenderingHints.Key, ?> hints) {
	Color background = null;
	return createLetterboxThumbnail(image, maximumSize, hints, background);
    }

    public static BufferedImage createLetterboxThumbnail(BufferedImage image,
	    Dimension maximumSize, Map<RenderingHints.Key, ?> hints,
	    Color background) {
	boolean center = true;
	return createLetterboxThumbnail(image, maximumSize, hints, background,
		center);
    }

    public static BufferedImage createLetterboxThumbnail(BufferedImage image,
	    Dimension maximumSize, Map<RenderingHints.Key, ?> hints,
	    Color background, boolean center) {
	int type = getType(image);
	BufferedImage thumbnail = new BufferedImage(maximumSize.width,
		maximumSize.height, type);

	Dimension scale = scale(image, maximumSize);

	int x = 0, y = 0;
	if (center) {
	    x = (maximumSize.width - scale.width) / 2;
	    y = (maximumSize.height - scale.height) / 2;
	}

	Graphics2D g2d = thumbnail.createGraphics();
	if (hints != null)
	    g2d.setRenderingHints(hints);

	ImageObserver observer = null;
	if (background != null) {
	    g2d.setColor(background);
	    g2d.fillRect(0, 0, maximumSize.width, maximumSize.height);
	    g2d.drawImage(image, x, y, scale.width, scale.height, background,
		    observer);
	} else {
	    g2d.drawImage(image, x, y, scale.width, scale.height, observer);
	}

	g2d.dispose();
	return thumbnail;
    }

    private static Dimension scale(BufferedImage image, Dimension maximumSize) {
	double widthScale = (double) maximumSize.width / image.getWidth();
	double heightScale = (double) maximumSize.height / image.getHeight();
	double scale = Math.min(widthScale, heightScale);

	int width = (int) (image.getWidth() * scale);
	int height = (int) (image.getHeight() * scale);
	return new Dimension(width, height);
    }

    public static BufferedImage createScaledThumbnail(BufferedImage image,
	    double scale) {
	Map<RenderingHints.Key, ?> hints = null;
	return createScaledThumbnail(image, scale, hints);
    }

    public static BufferedImage createScaledThumbnail(BufferedImage image,
	    double scale, Map<RenderingHints.Key, ?> hints) {
	int width = (int) (image.getWidth() * scale);
	int height = (int) (image.getHeight() * scale);
	int type = getType(image);
	BufferedImage thumbnail = new BufferedImage(width, height, type);

	Graphics2D g2d = thumbnail.createGraphics();
	if (hints != null)
	    g2d.setRenderingHints(hints);

	int x = 0, y = 0;
	ImageObserver observer = null;
	g2d.drawImage(image, x, y, width, height, observer);
	g2d.dispose();
	return thumbnail;
    }

    public static BufferedImage createThumbnail(BufferedImage image,
	    Dimension size) {
	return createThumbnail(image, size, null);
    }

    public static BufferedImage createThumbnail(BufferedImage image,
	    Dimension size, Map<RenderingHints.Key, ?> hints) {
	int type = getType(image);
	BufferedImage thumbnail = new BufferedImage(size.width, size.height,
		type);

	Graphics2D g2d = thumbnail.createGraphics();
	if (hints != null)
	    g2d.setRenderingHints(hints);

	ImageObserver observer = null;
	int x = 0, y = 0;
	g2d.drawImage(image, x, y, size.width, size.height, observer);
	g2d.dispose();
	return thumbnail;
    }

    private static int getType(BufferedImage image) {
	int type = image.getType();
	switch (type) {
	case TYPE_CUSTOM:
	    type = TYPE_INT_ARGB;
	}
	return type;
    }

    public static BufferedImage toBufferedImage(Image image) {
	return toBufferedImage(image, TYPE_INT_ARGB);
    }

    public static BufferedImage toBufferedImage(Image image, int type) {
	if (image instanceof BufferedImage)
	    return (BufferedImage) image;

	ImageObserver observer = null;
	int width = image.getWidth(observer);
	int height = image.getHeight(observer);
	BufferedImage bufferedImage = new BufferedImage(width, height, type);
	Graphics2D g2d = bufferedImage.createGraphics();
	int x = 0, y = 0;
	g2d.drawImage(image, x, y, width, height, observer);
	g2d.dispose();
	return bufferedImage;
    }

    public static Map<RenderingHints.Key, ?> createRenderingHints(int quality) {
	Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();
	switch (quality) {
	case SPEED:
	    hints.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED);
	    hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
	    hints.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED);
	    hints.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	    hints.put(KEY_RENDERING, VALUE_RENDER_SPEED);
	    break;
	case QUALITY:
	    hints.put(KEY_ALPHA_INTERPOLATION,
		    VALUE_ALPHA_INTERPOLATION_QUALITY);
	    hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
	    hints.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
	    hints.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
	    hints.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
	    break;
	default:
	    hints.put(KEY_ALPHA_INTERPOLATION,
		    VALUE_ALPHA_INTERPOLATION_DEFAULT);
	    hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_DEFAULT);
	    hints.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_DEFAULT);
	    hints.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
	    hints.put(KEY_RENDERING, VALUE_RENDER_DEFAULT);
	    break;
	}
	return hints;
    }

}