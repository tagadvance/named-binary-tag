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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class FramePreferences {

    static final String KEY_EXTENDED_STATE = "frameExtendedState";
    static final String KEY_WIDTH = "frameDimensionWidth";
    static final String KEY_HEIGHT = "frameDimensionHeight";
    static final String KEY_X = "frameLocationX";
    static final String KEY_Y = "frameLocationY";

    private Frame frame;
    private String pathName;
    private WindowStateListener windowStateListener;
    private ComponentListener componentListener;

    public FramePreferences(Frame frame) {
	this(frame, null);
    }

    public FramePreferences(Frame frame, String pathName) {
	setFrame(frame);
	setPathName(pathName);
    }

    @SuppressWarnings("serial")
    public FramePreferences(final JInternalFrame frame, String pathName) {
	setFrame(new Frame() {

	    @Override
	    public synchronized int getExtendedState() {
		if (frame.isMaximum()) {
		    return Frame.MAXIMIZED_BOTH;
		} else if (frame.isIcon()) {
		    return Frame.ICONIFIED;
		} else {
		    return Frame.NORMAL;
		}
	    }

	    @Override
	    public synchronized void setExtendedState(int state) {
		try {
		    switch (state) {
		    case Frame.MAXIMIZED_HORIZ:
		    case Frame.MAXIMIZED_VERT:
		    case Frame.MAXIMIZED_BOTH:
			frame.setMaximum(true);
			break;
		    case Frame.ICONIFIED:
			frame.setIcon(true);
			break;
		    case Frame.NORMAL:
			frame.setIcon(false);
			frame.setMaximum(false);
			break;
		    }
		} catch (PropertyVetoException e) {
		    e.printStackTrace();
		}
	    }

	    @Override
	    public synchronized void addWindowStateListener(
		    final WindowStateListener l) {
		final Frame source = this;
		frame.addInternalFrameListener(new InternalFrameAdapter() {

		    @Override
		    public void internalFrameIconified(InternalFrameEvent e) {
			l.windowStateChanged(new WindowEvent(source,
				WindowEvent.WINDOW_ICONIFIED));
		    }

		    @Override
		    public void internalFrameDeiconified(InternalFrameEvent e) {
			l.windowStateChanged(new WindowEvent(source,
				WindowEvent.WINDOW_DEICONIFIED));
		    }

		});
	    }

	    @Override
	    public synchronized void removeWindowStateListener(
		    WindowStateListener l) {
		super.removeWindowStateListener(l);
	    }

	    @Override
	    public GraphicsConfiguration getGraphicsConfiguration() {
		return frame.getGraphicsConfiguration();
	    }

	    public Point getLocation() {
		return frame.getLocation();
	    }

	    @Override
	    public void setLocation(Point p) {
		frame.setLocation(p);
	    }

	    @Override
	    public Dimension getSize() {
		return frame.getSize();
	    }

	    @Override
	    public void setSize(Dimension size) {
		frame.setSize(size);
	    }

	    @Override
	    public synchronized void addComponentListener(ComponentListener l) {
		frame.addComponentListener(l);
	    }

	    @Override
	    public synchronized void removeComponentListener(ComponentListener l) {
		frame.addComponentListener(l);
	    }

	});
	setPathName(pathName);
    }

    public Frame getFrame() {
	return frame;
    }

    private void setFrame(Frame frame) {
	if (frame == null)
	    throw new IllegalArgumentException("frame must not be null");
	this.frame = frame;
    }

    public String getPathName() {
	return pathName;
    }

    public void setPathName(String pathName) {
	this.pathName = pathName;
    }

    public void restoreAll() {
	restoreSize();
	restoreLocation();
	restoreExtendedState();
    }

    public void restoreSize() {
	Dimension size = getPreferredSize();
	if (size != null)
	    frame.setSize(size);
    }

    public Dimension getPreferredSize() {
	Preferences prefs = getPreferences();
	int def = 0;
	int width = prefs.getInt(KEY_WIDTH, def);
	int height = prefs.getInt(KEY_HEIGHT, def);
	return new Dimension(width, height);
    }

    public void restoreLocation() {
	Dimension size = frame.getSize();
	Point location = getPreferredLocation();
	if (isOffScreen(size, location)) {
	    GraphicsConfiguration gc = frame.getGraphicsConfiguration();
	    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
	    location = new Point(insets.left, insets.top);
	}
	frame.setLocation(location);
    }

    public Point getPreferredLocation() {
	Preferences prefs = getPreferences();
	int def = 0;
	int x = prefs.getInt(KEY_X, def);
	int y = prefs.getInt(KEY_Y, def);
	return new Point(x, y);
    }

    private boolean isOffScreen(Dimension size, Point location) {
	Rectangle bounds = getGraphicsBounds();
	int padding = 10;
	bounds.x -= (size.width - padding);
	bounds.y -= padding;
	bounds.width += (size.width - padding);
	bounds.height += padding;
	// window is off screen
	return !bounds.contains(location);
    }

    public final Rectangle getGraphicsBounds() {
	GraphicsEnvironment environment = GraphicsEnvironment
		.getLocalGraphicsEnvironment();
	Rectangle r = null;
	for (GraphicsDevice device : environment.getScreenDevices()) {
	    GraphicsConfiguration config = device.getDefaultConfiguration();
	    if (r == null) {
		r = config.getBounds();
	    } else {
		r.add(config.getBounds());
	    }
	}
	return r;
    }

    public int getPreferredExtendedState() {
	Preferences prefs = getPreferences();
	return prefs.getInt(KEY_EXTENDED_STATE, Frame.NORMAL);
    }

    public void restoreExtendedState() {
	int state = getPreferredExtendedState();
	frame.setExtendedState(state);
    }

    public void install() {
	windowStateListener = new WindowStateListener() {

	    public void windowStateChanged(WindowEvent e) {
		Object source = e.getSource();
		if (source instanceof JFrame) {
		    JFrame frame = (JFrame) source;
		    int extendedState = frame.getExtendedState();
		    if (extendedState == JFrame.ICONIFIED)
			return;

		    Preferences prefs = getPreferences();
		    prefs.putInt(KEY_EXTENDED_STATE, extendedState);
		}
	    }

	};
	frame.addWindowStateListener(windowStateListener);

	componentListener = new ComponentAdapter() {

	    @Override
	    public void componentResized(ComponentEvent e) {
		Preferences prefs = getPreferences();
		Dimension size = frame.getSize();
		prefs.putInt(KEY_WIDTH, size.width);
		prefs.putInt(KEY_HEIGHT, size.height);
	    }

	    @Override
	    public void componentMoved(ComponentEvent e) {
		Preferences prefs = getPreferences();
		Point location = frame.getLocation();
		prefs.putInt(KEY_X, location.x);
		prefs.putInt(KEY_Y, location.y);
	    }

	};
	frame.addComponentListener(componentListener);
    }

    public void uninstall() {
	frame.removeWindowStateListener(windowStateListener);
	frame.removeComponentListener(componentListener);
    }

    public boolean isFirst() {
	Preferences prefs = getPreferences();
	try {
	    List<String> pref = Arrays.asList(prefs.keys());
	    for (String key : keys()) {
		if (pref.contains(key)) {
		    return false;
		}
	    }
	} catch (BackingStoreException ex) {
	    ex.printStackTrace();
	}
	return true;
    }

    public static String[] keys() {
	return new String[] { KEY_EXTENDED_STATE, KEY_WIDTH, KEY_HEIGHT, KEY_X,
		KEY_Y };
    }

    private Preferences getPreferences() {
	Preferences prefs = Preferences.userNodeForPackage(getClass());
	String pathName = getPathName();
	return (pathName == null) ? prefs : prefs.node(pathName);
    }

}