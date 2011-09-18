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
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

public class WindowPreferences {

	static final String KEY_EXTENDED_STATE = "windowExtendedState";
	static final String KEY_WIDTH = "windowDimensionWidth";
	static final String KEY_HEIGHT = "windowDimensionHeight";
	static final String KEY_X = "windowLocationX";
	static final String KEY_Y = "windowLocationY";

	private Window window;
	private String pathName;
	private WindowStateListener windowStateListener;
	private ComponentListener componentListener;

	public WindowPreferences(Window window) {
		this(window, null);
	}

	public WindowPreferences(Window window, String pathName) {
		setWindow(window);
		setPathName(pathName);
	}

	public Window getWindow() {
		return window;
	}

	private void setWindow(Window window) {
		if (window == null)
			throw new IllegalArgumentException("window must not be null");
		this.window = window;
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
			window.setSize(size);
	}

	public Dimension getPreferredSize() {
		Preferences prefs = getPreferences();
		int def = 0;
		int width = prefs.getInt(KEY_WIDTH, def);
		int height = prefs.getInt(KEY_HEIGHT, def);
		return new Dimension(width, height);
	}

	public void restoreLocation() {
		Dimension size = window.getSize();
		Point location = getPreferredLocation();
		if (isOffScreen(size, location)) {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
			location = new Point(insets.left, insets.top);
		}
		window.setLocation(location);
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
		if (window instanceof Frame) {
			Frame frame = (Frame) window;
			int state = getPreferredExtendedState();
			frame.setExtendedState(state);
		}
	}

	public void install() {
		windowStateListener = new WindowStateListener() {

			public void windowStateChanged(WindowEvent e) {
				Object source = e.getSource();
				if (source instanceof JFrame) {
					JFrame frame = (JFrame) source;
					int extendedState = frame.getExtendedState();
					if (extendedState == JFrame.ICONIFIED) {
						return;
					}
					Preferences prefs = getPreferences();
					prefs.putInt(KEY_EXTENDED_STATE, extendedState);
				}
			}

		};
		window.addWindowStateListener(windowStateListener);

		componentListener = new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				Preferences prefs = getPreferences();
				Dimension size = window.getSize();
				prefs.putInt(KEY_WIDTH, size.width);
				prefs.putInt(KEY_HEIGHT, size.height);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				Preferences prefs = getPreferences();
				Point location = window.getLocation();
				prefs.putInt(KEY_X, location.x);
				prefs.putInt(KEY_Y, location.y);
			}

		};
		window.addComponentListener(componentListener);
	}

	public void uninstall() {
		window.removeWindowStateListener(windowStateListener);
		window.removeComponentListener(componentListener);
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
		return new String[] {
				KEY_EXTENDED_STATE, KEY_WIDTH, KEY_HEIGHT, KEY_X, KEY_Y
		};
	}

	private Preferences getPreferences() {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		String pathName = getPathName();
		return (pathName == null) ? prefs : prefs.node(pathName);
	}

}