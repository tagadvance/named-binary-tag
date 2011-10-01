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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import com.tag.Cache;

import resources.Resource;

public class SpriteRecord extends SimpleRecord implements Sprite {

    private static Grid256 blockGrid;
    private static Grid256 itemGrid;

    private Cache<Float, BufferedImage> cache = new Cache<Float, BufferedImage>() {

	@Override
	public BufferedImage apply(Float key) {
	    BufferedImage image = getImage();
	    BufferedImage rgb = toRGB(image);
	    RescaleOp op = new RescaleOp(key, 0, null);
	    op.filter(rgb, rgb);
	    return rgb;
	}

	private BufferedImage toRGB(BufferedImage image) {
	    int width = image.getWidth(), height = image.getHeight();
	    BufferedImage image2 = new BufferedImage(width, height,
		    BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = image2.createGraphics();
	    int x = 0, y = 0;
	    g.drawImage(image, x, y, null);
	    g.dispose();
	    return image2;
	}

    };

    public SpriteRecord(String[] row) {
	super(row);
    }

    public boolean isBlock() {
	int id = getID();
	return (id >= 0 && id <= 0xFF);
    }

    @Override
    public BufferedImage getImage() {
	Grid256 grid = isBlock() ? getBlockGrid() : getItemGrid();
	int index = getIconIndex();
	return grid.subimage(index);
    }

    public BufferedImage getImage(float darkness) {
	return cache.get(darkness);
    }

    private static Grid256 getBlockGrid() {
	if (blockGrid == null)
	    blockGrid = createGrid("images/terrain.png");
	return blockGrid;
    }

    private static Grid256 getItemGrid() {
	if (itemGrid == null)
	    itemGrid = createGrid("images/items.png");
	return itemGrid;
    }

    private static Grid256 createGrid(String name) {
	Resource resource = new Resource();
	BufferedImage image = resource.getImage(name);
	return new Grid256(image);
    }

}