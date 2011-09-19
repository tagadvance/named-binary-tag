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

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class BrowseFrame extends JInternalFrame {

	public static final String KEY_FILE = "file";

	private Workspace workspace;

	private JPanel contentPane;
	private JTextField textFile;
	private JButton btnBrowse;

	protected Action newAction;
	protected Action openAction;
	protected Action saveAction;
	protected Action saveAsAction;
	protected Action refreshAction;

	public BrowseFrame(Workspace workspace) {
		super("Browse", true, false, true, true);
		setWorkspace(workspace);

		createActions();
		initComponents();

		restoreFile();
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	protected void setWorkspace(Workspace workspace) {
		if (workspace == null)
			throw new IllegalArgumentException("workspace must not be null");
		this.workspace = workspace;
	}

	private void createActions() {
		newAction = new NBTAction("New", "New", "New", KeyEvent.VK_N) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: New
			}

		};

		openAction = new NBTAction("Open File...", "Open", "Open File...",
				KeyEvent.VK_O) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				Preferences prefs = getPreferences();
				String exportFile = prefs.get(KEY_FILE, null);
				if (exportFile == null) {
					File cwd = new File(".");
					fc.setCurrentDirectory(cwd);
				} else {
					File selectedFile = new File(exportFile);
					fc.setSelectedFile(selectedFile);
				}
				switch (fc.showOpenDialog(BrowseFrame.this)) {
					case JFileChooser.APPROVE_OPTION:
						File dir = fc.getSelectedFile();
						prefs.put(KEY_FILE, dir.getAbsolutePath());
						setDirectory(dir);
						break;
				}
			}

		};

		saveAction = new NBTAction("Save", "Save", "Save", KeyEvent.VK_S) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Workspace workspace = getWorkspace();
				workspace.doExport();
			}

		};

		saveAsAction = new NBTAction("Save As...", "SaveAs", "Save As...",
				KeyEvent.VK_UNDEFINED) {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				Preferences prefs = getPreferences();
				String exportFile = prefs.get(KEY_FILE, null);
				if (exportFile == null) {
					File cwd = new File(".");
					fc.setCurrentDirectory(cwd);
				} else {
					File selectedFile = new File(exportFile);
					fc.setSelectedFile(selectedFile);
				}
				switch (fc.showSaveDialog(BrowseFrame.this)) {
					case JFileChooser.APPROVE_OPTION:
						File dir = fc.getSelectedFile();
						prefs.put(KEY_FILE, dir.getAbsolutePath());
						setDirectory(dir);
						workspace.doExport();
						break;
				}
			}

		};

		refreshAction = new NBTAction("Refresh", "Refresh", "Refresh",
				KeyEvent.VK_F5) {

			{
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F5"));
			}

			public void actionPerformed(ActionEvent e) {
				Workspace workspace = getWorkspace();
				File dir = workspace.getWorldFile();
				if (dir.canRead()) {
					workspace.doImport();
				} else {
					showErrorDialog("The file could not be read.");
				}
			}

		};

	}

	private void initComponents() {
		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);

		contentPane = new JPanel();
		setContentPane(contentPane);

		JPanel browsePanel = new JPanel();
		Border border = new TitledBorder(null, "Location");
		browsePanel.setBorder(border);

		textFile = new JTextField();
		textFile.setEditable(false);
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openAction.actionPerformed(e);
			}

		});

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(
				Alignment.TRAILING).addGroup(
				Alignment.LEADING,
				gl_contentPane
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_contentPane.createParallelGroup(
										Alignment.TRAILING).addComponent(
										browsePanel, Alignment.LEADING,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE,
										Short.MAX_VALUE)).addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPane.createSequentialGroup()
						.addComponent(browsePanel).addContainerGap()));

		GroupLayout gl_browsePanel = new GroupLayout(browsePanel);
		gl_browsePanel.setHorizontalGroup(gl_browsePanel.createParallelGroup(
				Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				gl_browsePanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(textFile, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnBrowse).addContainerGap()));
		gl_browsePanel
				.setVerticalGroup(gl_browsePanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_browsePanel
										.createSequentialGroup()
										//.addContainerGap()
										.addGroup(
												gl_browsePanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																textFile,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE,
																Short.MAX_VALUE)
														.addComponent(btnBrowse))
										.addContainerGap()));
		browsePanel.setLayout(gl_browsePanel);
		contentPane.setLayout(gl_contentPane);

		pack();
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menuFile.add(new JMenuItem(newAction));
		menuFile.add(new JMenuItem(openAction));
		menuFile.add(new JMenuItem(saveAction));
		menuFile.add(new JMenuItem(saveAsAction));
		menuFile.add(new JMenuItem(refreshAction));
		menuBar.add(menuFile);

		return menuBar;
	}

	protected JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new ToolBarButton(newAction));
		toolBar.add(new ToolBarButton(openAction));
		toolBar.add(new ToolBarButton(saveAction));
		toolBar.add(new ToolBarButton(saveAsAction));
		toolBar.add(new ToolBarButton(refreshAction));
		toolBar.addSeparator();

		return toolBar;
	}

	private void restoreFile() {
		Preferences prefs = getPreferences();
		String pathname = prefs.get(KEY_FILE, null);
		if (pathname != null) {
			File dir = new File(pathname);
			setDirectory(dir);
		}
	}
	
	protected void setDirectory(File dir) {
		Workspace workspace = getWorkspace();
		workspace.setWorldDir(dir);
		
		String path = dir.getAbsolutePath();
		textFile.setText(path);
	}

	public void showErrorDialog(String message) {
		String title = "Error";
		JOptionPane.showMessageDialog(BrowseFrame.this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public Preferences getPreferences() {
		return Preferences.userNodeForPackage(getClass());
	}

}