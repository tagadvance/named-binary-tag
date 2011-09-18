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

import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jnbt.CompoundTag;
import org.jnbt.NBTConstants;
import org.jnbt.NBTInputStream;

import com.tag.ImageFactory;
import com.tag.WindowPreferences;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	public static final String KEY_FILE = "file";

	private JPanel contentPane;
	private JTextField textFile;
	private JButton btnBrowse;
	private NBTTreeTable treeTable;
	private JScrollPane scrollPane;

	protected Action newAction;
	protected Action openAction;
	protected Action saveAction;
	protected Action saveAsAction;
	protected Action exitAction;

	protected Action cutAction;
	protected Action copyAction;
	protected Action pasteAction;
	protected Action deleteAction;

	protected Action addByteAction;
	protected Action addShortAction;
	protected Action addIntAction;
	protected Action addLongAction;
	protected Action addFloatAction;
	protected Action addDoubleAction;
	protected Action addByteArrayAction;
	protected Action addStringAction;
	protected Action addListAction;
	protected Action addCompoundAction;

	protected Action helpAction;

	public static void main(String[] args) {
		//setPreferredLookAndFeel();
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				GUI frame = new GUI();
				frame.setVisible(true);
			}

		});
	}

	@SuppressWarnings("unused")
	private static void setPreferredLookAndFeel() {
		String[] lafs = {
				"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel",
				UIManager.getSystemLookAndFeelClassName(),
				UIManager.getCrossPlatformLookAndFeelClassName()
		};
		for (String laf : lafs) {
			try {
				UIManager.setLookAndFeel(laf);
				break;
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		createActions();
		initComponents();
		initListeners();

		restoreFile();

		WindowPreferences prefs = new WindowPreferences(this, getTitle());
		prefs.restoreAll();
		prefs.install();
	}

	private void createActions() {
		newAction = new NBTAction("New", "New", "New", KeyEvent.VK_N) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				treeTable = new NBTTreeTable(new CompoundTag(""));
				scrollPane.setViewportView(treeTable);
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
				fc.setAcceptAllFileFilterUsed(false);
				String description = "named binary tag";
				FileFilter filter = new FileNameExtensionFilter(description,
						"mcr", "dat", "dat_old");
				fc.setFileFilter(filter);
				Preferences prefs = getPreferences();
				String exportFile = prefs.get(KEY_FILE, null);
				File selectedFile = (exportFile == null ? new File(".")
						: new File(exportFile));
				fc.setSelectedFile(selectedFile);
				switch (fc.showOpenDialog(GUI.this)) {
					case JFileChooser.APPROVE_OPTION:
						File file = fc.getSelectedFile();
						prefs.put(KEY_FILE, file.getAbsolutePath());
						doImport(file);
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
			}

		};

		saveAsAction = new NBTAction("Save As...", "SaveAs", "Save As...",
				KeyEvent.VK_UNDEFINED) {

			public void actionPerformed(ActionEvent e) {

			}

		};

		exitAction = new NBTAction("Exit", "Exit", KeyEvent.VK_ESCAPE) {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		};

		cutAction = new DefaultEditorKit.CutAction() {

			{
				String name = "Cut";
				putValue(NAME, name);
				putValue(SHORT_DESCRIPTION, name);
				putValue(MNEMONIC_KEY, KeyEvent.VK_X);
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('X', Event.CTRL_MASK));

				ImageFactory factory = new ImageFactory();
				try {
					putValue(
							SMALL_ICON,
							new ImageIcon(factory.readImage(name,
									NBTAction.smallIconSize)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					putValue(
							LARGE_ICON_KEY,
							new ImageIcon(factory.readImage(name,
									NBTAction.largeIconSize)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		copyAction = new DefaultEditorKit.CopyAction() {

			{
				String name = "Copy";
				putValue(NAME, name);
				putValue(SHORT_DESCRIPTION, name);
				putValue(MNEMONIC_KEY, KeyEvent.VK_C);
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('C', Event.CTRL_MASK));

				ImageFactory factory = new ImageFactory();
				try {
					putValue(
							SMALL_ICON,
							new ImageIcon(factory.readImage(name,
									NBTAction.smallIconSize)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					putValue(
							LARGE_ICON_KEY,
							new ImageIcon(factory.readImage(name,
									NBTAction.largeIconSize)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		pasteAction = new DefaultEditorKit.CutAction() {

			{
				String name = "Paste";
				putValue(NAME, name);
				putValue(SHORT_DESCRIPTION, name);
				putValue(MNEMONIC_KEY, KeyEvent.VK_V);
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('V', Event.CTRL_MASK));

				ImageFactory factory = new ImageFactory();
				try {
					putValue(
							SMALL_ICON,
							new ImageIcon(factory.readImage(name,
									NBTAction.smallIconSize)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					putValue(
							LARGE_ICON_KEY,
							new ImageIcon(factory.readImage(name,
									NBTAction.largeIconSize)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};

		deleteAction = new NBTAction("Delete", "Delete", "Delete",
				KeyEvent.VK_DELETE) {

			{
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
			}

			public void actionPerformed(ActionEvent e) {
				System.out.println("delete");
			}

		};

		addByteAction = new NBTAction("Add Byte", NBTConstants.TYPE_BYTE,
				"Add Byte", KeyEvent.VK_1) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('1', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {
				System.out.println("add byte");
			}

		};

		addShortAction = new NBTAction("Add Short", NBTConstants.TYPE_SHORT,
				"Add Short", KeyEvent.VK_2) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('2', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addIntAction = new NBTAction("Add Integer", NBTConstants.TYPE_INT,
				"Add Integer", KeyEvent.VK_3) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('3', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addLongAction = new NBTAction("Add Long", NBTConstants.TYPE_LONG,
				"Add Long", KeyEvent.VK_4) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('4', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addFloatAction = new NBTAction("Add Float", NBTConstants.TYPE_FLOAT,
				"Add Float", KeyEvent.VK_5) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('5', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addDoubleAction = new NBTAction("Add Double", NBTConstants.TYPE_DOUBLE,
				"Add Double", KeyEvent.VK_6) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('6', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addByteArrayAction = new NBTAction("Add Byte Array",
				NBTConstants.TYPE_BYTE_ARRAY, "Add Byte Array", KeyEvent.VK_7) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('7', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addStringAction = new NBTAction("Add String", NBTConstants.TYPE_STRING,
				"Add String", KeyEvent.VK_8) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('8', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addListAction = new NBTAction("Add List Tag", NBTConstants.TYPE_LIST,
				"Add List Tag", KeyEvent.VK_9) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('9', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		addCompoundAction = new NBTAction("Add Compound Tag",
				NBTConstants.TYPE_COMPOUND, "Add Compound Tag", KeyEvent.VK_0) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('0', Event.CTRL_MASK));
			}

			public void actionPerformed(ActionEvent e) {

			}

		};

		helpAction = new NBTAction("Help", "Help", "Help", KeyEvent.VK_F1) {

			{
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
			}

			public void actionPerformed(ActionEvent e) {
				System.out.println("help?");
			}

		};

	}

	private void initComponents() {
		setTitle("NBT Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
		JToolBar toolBar = createToolBar();

		contentPane = new JPanel();
		setContentPane(contentPane);

		JPanel browsePanel = new JPanel();
		Border border = new TitledBorder(null, "Location");
		browsePanel.setBorder(border);

		textFile = new JTextField();
		textFile.setEditable(false);
		btnBrowse = new JButton("Browse");
		scrollPane = new JScrollPane();

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								Alignment.LEADING,
								gl_contentPane
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																browsePanel,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																scrollPane,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE,
																Short.MAX_VALUE))
										.addContainerGap())
						.addComponent(toolBar, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addComponent(toolBar)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(browsePanel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addContainerGap()));

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

		int width = 400, height = 300;
		setMinimumSize(new Dimension(width, height));

		pack();
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menuFile.add(new JMenuItem(newAction));
		menuFile.add(new JMenuItem(openAction));
		menuFile.add(new JMenuItem(saveAction));
		menuFile.add(new JMenuItem(saveAsAction));
		menuFile.add(new JMenuItem(exitAction));
		menuBar.add(menuFile);

		JMenu menuEdit = new JMenu("Edit");
		menuEdit.add(new JMenuItem(cutAction));
		menuEdit.add(new JMenuItem(copyAction));
		menuEdit.add(new JMenuItem(pasteAction));
		menuEdit.addSeparator();
		menuEdit.add(new JMenuItem(deleteAction));
		menuEdit.addSeparator();
		menuEdit.add(new JMenuItem(addByteAction));
		menuEdit.add(new JMenuItem(addShortAction));
		menuEdit.add(new JMenuItem(addIntAction));
		menuEdit.add(new JMenuItem(addLongAction));
		menuEdit.add(new JMenuItem(addFloatAction));
		menuEdit.add(new JMenuItem(addDoubleAction));
		menuEdit.add(new JMenuItem(addByteArrayAction));
		menuEdit.add(new JMenuItem(addStringAction));
		menuEdit.add(new JMenuItem(addListAction));
		menuEdit.add(new JMenuItem(addCompoundAction));
		menuBar.add(menuEdit);

		JMenu menuHelp = new JMenu("Help");
		menuHelp.add(new JMenuItem(helpAction));
		menuBar.add(menuHelp);

		return menuBar;
	}

	protected JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new ToolBarButton(newAction));
		toolBar.add(new ToolBarButton(openAction));
		toolBar.add(new ToolBarButton(saveAction));
		toolBar.add(new ToolBarButton(saveAsAction));
		toolBar.add(new ToolBarButton(exitAction));
		toolBar.addSeparator();

		toolBar.add(new ToolBarButton(addByteAction));
		toolBar.add(new ToolBarButton(addShortAction));
		toolBar.add(new ToolBarButton(addIntAction));
		toolBar.add(new ToolBarButton(addLongAction));
		toolBar.add(new ToolBarButton(addFloatAction));
		toolBar.add(new ToolBarButton(addDoubleAction));
		toolBar.add(new ToolBarButton(addByteArrayAction));
		toolBar.add(new ToolBarButton(addStringAction));
		toolBar.add(new ToolBarButton(addListAction));
		toolBar.add(new ToolBarButton(addCompoundAction));

		return toolBar;
	}

	private void initListeners() {
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openAction.actionPerformed(e);
			}

		});
	}

	private void restoreFile() {
		Preferences prefs = getPreferences();
		String pathname = prefs.get(KEY_FILE, null);
		if (pathname != null) {
			File file = new File(pathname);
			doImport(file);
		}
	}

	public void doImport(final File file) {
		textFile.setText(file.getAbsolutePath());

		new SwingWorker<CompoundTag, Void>() {

			@Override
			protected CompoundTag doInBackground() throws Exception {
				NBTInputStream ns = null;
				try {
					ns = new NBTInputStream(new FileInputStream(file));
					return (CompoundTag) ns.readTag();
				} finally {
					IOUtils.closeQuietly(ns);
				}
			}

			@Override
			protected void done() {
				CompoundTag tag = null;
				try {
					tag = get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
					Throwable cause = ExceptionUtils.getRootCause(e);
					showErrorDialog(cause.getMessage());
					return;
				}
				treeTable = new NBTTreeTable(tag);
				scrollPane.setViewportView(treeTable);
			}

		}.execute();
	}

	public void showErrorDialog(String message) {
		String title = "Error";
		JOptionPane.showMessageDialog(GUI.this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public Preferences getPreferences() {
		return Preferences.userNodeForPackage(getClass());
	}

}