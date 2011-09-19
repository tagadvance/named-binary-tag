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

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.nbt.repo.FileRepository;
import com.nbt.repo.Repository;
import com.tag.FramePreferences;

@SuppressWarnings("serial")
public class Workspace extends JFrame {

	private JDesktopPane desktopPane;
	private File worldFile;

	public Workspace(String title) throws HeadlessException {
		super(title);
		initComponents();
	}

	private void initComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.desktopPane = createDesktopPane();
		setContentPane(desktopPane);
		
		BrowseFrame browser = new BrowseFrame(this);
		FramePreferences browserPreferences = new FramePreferences(browser,
				"browser");
		browserPreferences.restoreAll();
		browserPreferences.install();
		browser.setVisible(true);
		desktopPane.add(browser);
	}

	protected JDesktopPane createDesktopPane() {
		return new JDesktopPane();
	}

	public void doImport() {
		for (Component component : getComponents()) {
			if (component instanceof DataFrame) {
				remove(component);

				DataFrame frame = (DataFrame) component;
				try {
					frame.doLoad();
				} catch (IOException e) {
					e.printStackTrace();

					String message = e.getMessage();
					showErrorDialog(message);
				}
			}
		}

		File dir = getWorldFile();
		Repository repo = new FileRepository(new File(dir, "level.dat"));
		DataFrame dataFrame = new DataFrame("level.dat", repo);
		dataFrame.doImport();
		FramePreferences preferences = new FramePreferences(dataFrame,
				"level.dat");
		preferences.restoreAll();
		preferences.install();
		dataFrame.setVisible(true);
		add(dataFrame);
	}

	public void doExport() {
		for (Component component : getComponents()) {
			if (component instanceof DataFrame) {
				DataFrame frame = (DataFrame) component;
				try {
					frame.doSave();
				} catch (IOException e) {
					e.printStackTrace();

					String message = e.getMessage();
					showErrorDialog(message);
				}
			}
		}
	}

	public File getWorldFile() {
		return worldFile;
	}

	public void setWorldDir(File file) {
		if (file == null)
			throw new IllegalArgumentException("file must not be null");
		this.worldFile = file;
		
		doImport();
	}

	public void showErrorDialog(String message) {
		String title = "Error";
		JOptionPane.showMessageDialog(Workspace.this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

}
