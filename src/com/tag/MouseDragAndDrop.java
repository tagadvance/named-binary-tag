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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.commons.lang3.Validate;

public abstract class MouseDragAndDrop implements MouseListener,
	MouseMotionListener {

    private Component component;
    private MouseEvent startEvent;
    private MouseEvent dragEvent;

    public MouseDragAndDrop(Component component) {
	Validate.notNull(component, "component must not be null");
	this.component = component;
    }

    public Component getComponent() {
	return component;
    }

    public MouseEvent getStartEvent() {
	return startEvent;
    }

    public abstract void selected(MouseEvent e);

    public abstract void dragged(MouseEvent e);

    public abstract void dropped(MouseEvent press, MouseEvent release);

    @Override
    public final void mouseDragged(MouseEvent e) {
	prepare(e);
	if (e.isConsumed())
	    return;

	if (dragEvent == null)
	    selected(startEvent);

	dragEvent = e;
	dragged(e);
    }

    protected void prepare(MouseEvent e) {
	int mod = e.getModifiers();
	if ((mod & MouseEvent.BUTTON1_MASK) == 0)
	    e.consume();
    }

    @Override
    public final void mouseMoved(MouseEvent e) {
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
    }

    @Override
    public final void mousePressed(MouseEvent e) {
	prepare(e);
	if (e.isConsumed())
	    return;

	startEvent = e;
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
	if (dragEvent != null)
	    dropped(startEvent, e);

	startEvent = null;
	dragEvent = null;
    }

    @Override
    public final void mouseEntered(MouseEvent e) {
    }

    @Override
    public final void mouseExited(MouseEvent e) {
    }

    public void install() {
	component.addMouseListener(this);
	component.addMouseMotionListener(this);
    }

    public void uninstall() {
	component.removeMouseListener(this);
	component.removeMouseMotionListener(this);
    }

}