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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.Validate;

import resources.Resource;

import com.nbt.data.Register;
import com.nbt.data.SpriteRecord;
import com.tag.MouseDragAndDrop;
import com.tag.Utils;
import com.terrain.Block;
import com.terrain.World;

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

    private HUD hud;

    public TileCanvas(final World world) {
	Validate.notNull(world, "world must not be null");
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
	addMouseWheelListener(new MouseWheelListener() {
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent e) {
		int amount = e.getWheelRotation();
		int altitude = getAltitude();
		setAltitude(amount + altitude);

		updateXYZ();
		doRepaint();
	    }
	});
	new MouseDragAndDrop(this) {

	    private int tileX, tileZ;

	    @Override
	    public void selected(MouseEvent e) {
		this.tileX = getTileX();
		this.tileZ = getTileZ();
	    }

	    @Override
	    public void dragged(MouseEvent e) {
		MouseEvent startEvent = getStartEvent();
		Point startPt = startEvent.getPoint();
		Point releasePt = e.getPoint();
		int x = tileX + (pixelsToTile(startPt.x) - pixelsToTile(releasePt.x));
		int z = tileZ + (pixelsToTile(startPt.y) - pixelsToTile(releasePt.y));
		setTileX(x);
		setTileZ(z);

		updateXYZ();
		doRepaint();
	    }

	    @Override
	    public void dropped(MouseEvent press, MouseEvent release) {
		// Point startPt = press.getPoint();
		// Point releasePt = release.getPoint();
		// int x = getTileX() + pixelsToTile(startPt.x - releasePt.x);
		// int z = getTileZ() + pixelsToTile(startPt.y - releasePt.y);
		// setTileX(x);
		// setTileZ(z);
		//
		// updateXYZ();
		// doRepaint();
	    }

	}.install();

	setLayout(null);
	hud = new HUD();
	int width = 200, height = 200;
	hud.setSize(width, height);
	add(hud);

	addMouseMotionListener(new MouseMotionAdapter() {
	    @Override
	    public void mouseMoved(MouseEvent e) {
		updateXYZ();
	    }
	});
    }

    private void updateXYZ() {
	Point pt = getMousePosition();
	if (pt == null)
	    pt = new Point();

	int x = getTileX() + pixelsToTile(pt.x);
	int z = getTileZ() + pixelsToTile(pt.y);
	hud.xl.setText("X: " + x);
	hud.zl.setText("Z: " + z);

	hud.al.setText("Y: " + getAltitude());
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	Graphics2D g2d = (Graphics2D) g.create();

	g2d.setColor(Color.BLACK);
	int x = 0, y = 0;
	Dimension size = getSize();
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

	updateXYZ();

	doRepaint();
    }

    public void doRepaint() {
	Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
		repaint();
	    }
	};
	if (SwingUtilities.isEventDispatchThread())
	    runnable.run();
	else
	    SwingUtilities.invokeLater(runnable);
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
	int width = getWidth();
	int tileWidth = (width / SPRITE_SIZE) + 1;
	return Math.max(this.tileWidth, tileWidth);
    }

    public void setTileWidth(int tileWidth) {
	Utils.validate(tileWidth, MIN_TILE_WIDTH, Integer.MAX_VALUE);
	this.tileWidth = tileWidth;
	updateSize();
    }

    public int getTileHeight() {
	int height = getHeight();
	int tileHeight = (height / SPRITE_SIZE) + 1;
	return Math.max(this.tileHeight, tileHeight);
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

    private static class HUD extends JPanel {

	public final JLabel xl;
	public final JLabel zl;
	public final JLabel al;

	public HUD() {
	    super();

	    BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
	    setLayout(boxLayout);

	    setOpaque(false);

	    add(xl = createLabel(" "));
	    add(zl = createLabel(" "));
	    add(al = createLabel(" "));
	}

	protected JLabel createLabel(String text) {
	    JLabel label = new JLabel(text);
	    label.setHorizontalAlignment(JLabel.LEADING);
	    label.setForeground(Color.WHITE);
	    label.setOpaque(false);
	    return label;
	}

    }

}