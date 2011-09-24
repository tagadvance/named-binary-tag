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

package com.nbt.data;

import java.awt.image.BufferedImage;

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
	if (image == null)
	    throw new IllegalArgumentException("image must not be null");
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