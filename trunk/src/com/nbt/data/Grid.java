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

package com.nbt.data;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.Validate;

public class Grid {

    private BufferedImage image;
    private int rows, columns;
    private BufferedImage[] subimages;

    public Grid(BufferedImage image, int rows, int columns) {
	setImage(image);
	setRows(rows);
	setColumns(columns);
	updateSubimages();
    }

    protected void updateSubimages() {
	final int rows = getRows(), columns = getColumns();
	final int width = image.getWidth(), height = image.getHeight();
	if (width < columns || (width >= columns && width % columns > 0))
	    throw new IllegalArgumentException();
	if (height < rows || (height >= rows && height % rows > 0))
	    throw new IllegalArgumentException();

	int length = rows * columns;
	this.subimages = new BufferedImage[length];

	int w = width / columns;
	int h = height / rows;

	int i = 0;
	for (int c = 0; c < columns; c++) {
	    int y = c * h;
	    for (int r = 0; r < rows; r++) {
		int x = r * w;
		subimages[i++] = image.getSubimage(x, y, w, h);
	    }
	}
    }

    public BufferedImage getImage() {
	return image;
    }

    public void setImage(BufferedImage image) {
	Validate.notNull("image must not be null");
	this.image = image;
    }

    public BufferedImage subimage(int i) {
	return subimages[i];
    }

    public BufferedImage subimage(int x, int y) {
	int cols = getColumns();
	int index = (y * cols) + x;
	return subimages[index];
    }

    public int getRows() {
	return rows;
    }

    public void setRows(int rows) {
	this.rows = rows;
    }

    public int getColumns() {
	return columns;
    }

    public void setColumns(int columns) {
	this.columns = columns;
    }

}