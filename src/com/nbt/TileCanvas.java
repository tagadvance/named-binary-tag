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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import resources.Resource;

import com.nbt.data.Register;
import com.nbt.data.SpriteRecord;
import com.nbt.world.Block;
import com.nbt.world.World;
import com.tag.Utils;

@SuppressWarnings("serial")
public class TileCanvas extends JComponent {

    private static final Register<SpriteRecord> register;
    static {
	register = new Register<SpriteRecord>() {
	    @Override
	    protected SpriteRecord createRecord(String[] row) {
		return new SpriteRecord(row);
	    }
	};
	Resource resource = new Resource();
	List<String[]> blocks = resource.getCSV("csv/blocks.csv");
	register.load(blocks);
	List<String[]> items = resource.getCSV("csv/items.csv");
	register.load(items);
    }

    private static final int MIN_TILE_WIDTH = 1, MIN_TILE_HEIGHT = 1;
    // TODO: this should be calculated
    private static final int SPRITE_SIZE = 16;

    private final World world;
    private int x, z, altitude;
    private int tileWidth = 16, tileHeight = 16;

    public TileCanvas(final World world) {
	if (world == null)
	    throw new IllegalArgumentException("world must not be null");
	this.world = world;

	setFocusable(true);
	addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		TileCanvas.this.keyPressed(e);
	    }
	});
	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		Point point = e.getPoint();
		int x = pixelsToTile(point.x);
		int z = pixelsToTile(point.y);
		int tileX = x + getTileX();
		int tileZ = z + getTileZ();
		int y = getAltitude();
		Block block = world.getBlock(tileX, y, tileZ);
		blockClicked(block);
	    }
	});
	
	JPanel hud = new JPanel();
	hud.setOpaque(false);

	BoxLayout boxLayout = new BoxLayout(hud, BoxLayout.Y_AXIS);
	hud.setLayout(boxLayout);

	final JLabel xl = new JLabel();
	xl.setAlignmentX(Component.LEFT_ALIGNMENT);
	hud.add(xl);

	final JLabel zl = new JLabel();
	zl.setAlignmentX(Component.LEFT_ALIGNMENT);
	hud.add(zl);

	final JLabel al = new JLabel();
	al.setAlignmentX(Component.LEFT_ALIGNMENT);
	hud.add(al);

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		while (isVisible()) {
		    xl.setText("X: " + getTileX());
		    zl.setText("Z: " + getTileZ());
		    al.setText("Y: " + getAltitude());
		    try {
			TimeUnit.SECONDS.sleep(1);
		    } catch (InterruptedException e) {
			break;
		    }
		}
	    }
	}).start();
	
	add(hud);
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	Graphics2D g2d = (Graphics2D) g.create();

	g2d.setColor(Color.BLACK);
	int x = 0, y = 0;
	Dimension size = getPreferredSize();
	g2d.fillRect(x, y, size.width, size.height);

	final int altitude = getAltitude();
	for (int w = 0; w < getTileWidth(); w++) {
	    x = w * SPRITE_SIZE;
	    for (int h = 0; h < getTileHeight(); h++) {
		y = h * SPRITE_SIZE;

		int xOffset = w + getTileX();
		int zOffset = h + getTileZ();
		BufferedImage tile = getBackgroundTile(xOffset, altitude,
			zOffset);
		if (tile != null)
		    g2d.drawImage(tile, x, y, null);
	    }
	}

	g2d.dispose();
    }

    protected void keyPressed(KeyEvent e) {
	final int keyCode = e.getKeyCode();
	switch (keyCode) {
	case KeyEvent.VK_UP:
	    int z = getTileZ();
	    setTileZ(z - 1);
	    break;
	case KeyEvent.VK_DOWN:
	    z = getTileZ();
	    setTileZ(z + 1);
	    break;
	case KeyEvent.VK_LEFT:
	    int x = getTileX();
	    setTileX(x - 1);
	    break;
	case KeyEvent.VK_RIGHT:
	    x = getTileX();
	    setTileX(x + 1);
	    break;
	case KeyEvent.VK_PAGE_UP:
	    int altitude = getAltitude();
	    setAltitude(altitude + 1);
	    break;
	case KeyEvent.VK_PAGE_DOWN:
	    altitude = getAltitude();
	    setAltitude(altitude - 1);
	    break;
	}
	doRepaint();
    }

    public void doRepaint() {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		repaint();
	    }
	});
    }

    protected void blockClicked(Block block) {

    }

    public int pixelsToTile(int pixels) {
	return pixels / SPRITE_SIZE;
    }

    public SpriteRecord getTile(int x, int y, int z) {
	Block block = world.getBlock(x, y, z);
	if (block == null)
	    return null;
	int id = block.getBlockID();
	return register.getRecord(id);
    }

    public BufferedImage getBackgroundTile(int x, int y, int z) {
	float darkness = 1;
	for (; y >= Block.MIN_Y; y--) {
	    SpriteRecord sprite = getTile(x, y, z);
	    if (sprite != null)
		return darkness < 1 ? sprite.getImage(darkness) : sprite
			.getImage();
	    darkness -= .05f;
	}
	return null;
    }

    public int getTileX() {
	return x;
    }

    public void setTileX(int x) {
	this.x = x;
    }

    public int getTileZ() {
	return z;
    }

    public void setTileZ(int z) {
	this.z = z;
    }

    public int getAltitude() {
	return altitude;
    }

    public void setAltitude(int altitude) {
	Utils.validate(altitude, Block.MIN_Y, Block.MAX_Y);
	this.altitude = altitude;
    }

    public int getTileWidth() {
	return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
	Utils.validate(tileWidth, MIN_TILE_WIDTH, Integer.MAX_VALUE);
	this.tileWidth = tileWidth;
	updateSize();
    }

    public int getTileHeight() {
	return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
	Utils.validate(tileHeight, MIN_TILE_HEIGHT, Integer.MAX_VALUE);
	this.tileHeight = tileHeight;
	updateSize();
    }

    private void updateSize() {
	int width = getTileWidth() * SPRITE_SIZE;
	int height = getTileHeight() * SPRITE_SIZE;
	super.setPreferredSize(new Dimension(width, height));
    }

}