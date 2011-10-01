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

import java.awt.EventQueue;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {

    public static void main(final String[] args) {
	// setPreferredLookAndFeel();
	EventQueue.invokeLater(new Runnable() {

	    public void run() {
		TreeFrame gui = new TreeFrame();
		gui.setVisible(true);

		if (args.length > 0) {
		    File file = new File(args[0]);
		    if (file.canRead())
			gui.doImport(file);
		}
	    }

	});

	Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	    @Override
	    public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
		
		String message = e.getMessage();
		String title = "Error";
		JOptionPane.showMessageDialog(null, message, title,
			JOptionPane.ERROR_MESSAGE);
	    }
	});
    }

    @SuppressWarnings("unused")
    private static void setPreferredLookAndFeel() {
	String[] lafs = {
		// "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel",
		UIManager.getSystemLookAndFeelClassName(),
		UIManager.getCrossPlatformLookAndFeelClassName() };
	for (String laf : lafs) {
	    try {
		UIManager.setLookAndFeel(laf);
		break;
	    } catch (Exception e) {
		// e.printStackTrace();
	    }
	}
    }

}