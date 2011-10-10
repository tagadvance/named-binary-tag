package com.terrain;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.event.EventListenerList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class SessionLock {

    public static final String FILE_NAME = "session.lock";

    private File file;
    private long lastModified;

    /**
     * TODO: switch to Watch Service
     * http://download.oracle.com/javase/tutorial/essential/io/notification.html
     */
    private Timer timer;

    protected EventListenerList eventListenerList;

    public SessionLock(final File file) {
	Validate.notNull(file, "file must not be null");
	String name = file.getName();
	if (!FILE_NAME.equals(name))
	    throw new IllegalArgumentException(name);
	this.file = file;

	this.timer = new Timer();
	this.eventListenerList = new EventListenerList();
    }

    public boolean acquireQuietly() {
	try {
	    acquire();
	    return true;
	} catch (IOException e) {
	    // e.printStackTrace();
	}
	return false;
    }

    public void acquire() throws IOException {
	int capacity = 8;
	ByteBuffer buffer = ByteBuffer.allocate(capacity);
	long time = System.currentTimeMillis();
	buffer.putLong(time);
	byte[] data = buffer.array();
	FileUtils.writeByteArrayToFile(file, data);
	this.lastModified = file.lastModified();
	initTimer();
    }

    private void initTimer() {
	long delay = TimeUnit.SECONDS.toMillis(1);
	timer.schedule(new TimerTask() {
	    @Override
	    public void run() {
		long lastModified = file.lastModified();
		if (SessionLock.this.lastModified != lastModified) {
		    cancel();
		    onYield();
		}
	    }
	}, delay, delay);
    }

    public void onYield() {
	fireYieldEvent(new YieldEvent(this));
    }

    public void fireYieldEvent(YieldEvent e) {
	YieldListener[] listeners = eventListenerList
		.getListeners(YieldListener.class);
	ArrayUtils.reverse(listeners);
	for (YieldListener listener : listeners) {
	    listener.onYield(e);
	}
    }

    public void addYieldListener(YieldListener listener) {
	eventListenerList.add(YieldListener.class, listener);
    }

    public void removeYieldListener(YieldListener listener) {
	eventListenerList.remove(YieldListener.class, listener);
    }

    public static interface YieldListener extends EventListener {
	void onYield(YieldEvent e);
    }

    @SuppressWarnings("serial")
    public static class YieldEvent extends EventObject {
	public YieldEvent(Object source) {
	    super(source);
	}
    }

}