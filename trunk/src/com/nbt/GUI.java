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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.NBTConstants;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.NBTUtils;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import com.tag.FramePreferences;
import com.tag.Hyperlink;
import com.tag.ImageFactory;
import com.tag.SwingWorkerUnlimited;
import com.terrain.Block;
import com.terrain.Region;
import com.terrain.World;
import com.terrain.WorldDirectory;
import com.terrain.WorldRegion;

// TODO: change (instanceof Integer) to ByteWrapper
@SuppressWarnings("serial")
public class GUI extends JFrame {

    public static final String TITLE = "NBT Kit";
    public static final String VERSION = "v1.0.0";

    public static final String KEY_FILE = "file";

    public static final String EXT_DAT = "dat";
    public static final String EXT_DAT_OLD = "dat_old";
    public static final String EXT_MCR = "mcr";

    private JPanel contentPane;
    private JTextField textFile;
    private JButton btnBrowse;
    private NBTTreeTable treeTable;
    private JScrollPane scrollPane;

    protected Action newAction;
    protected Action openAction;
    protected Action saveAction;
    protected Action saveAsAction;
    protected Action refreshAction;
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

    /**
     * Create the frame.
     */
    public GUI() {
	createActions();
	initComponents();

	restoreFile();
	updateActions();

	FramePreferences prefs = new FramePreferences(this, getTitle());
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
		updateTreeTable(new CompoundTag(""));
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
		JFileChooser fc = createFileChooser();
		switch (fc.showOpenDialog(GUI.this)) {
		case JFileChooser.APPROVE_OPTION:
		    File file = fc.getSelectedFile();
		    Preferences prefs = getPreferences();
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
		String path = textFile.getText();
		File file = new File(path);
		if (file.canWrite()) {
		    doExport(file);
		} else {
		    saveAsAction.actionPerformed(e);
		}
	    }

	};

	saveAsAction = new NBTAction("Save As...", "SaveAs", "Save As...",
		KeyEvent.VK_UNDEFINED) {

	    public void actionPerformed(ActionEvent e) {
		JFileChooser fc = createFileChooser();
		switch (fc.showSaveDialog(GUI.this)) {
		case JFileChooser.APPROVE_OPTION:
		    File file = fc.getSelectedFile();
		    Preferences prefs = getPreferences();
		    prefs.put(KEY_FILE, file.getAbsolutePath());
		    doExport(file);
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
		String path = textFile.getText();
		File file = new File(path);
		if (file.canRead())
		    doImport(file);
		else
		    showErrorDialog("The file could not be read.");
	    }

	};

	exitAction = new NBTAction("Exit", "Exit", KeyEvent.VK_ESCAPE) {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO: this should check to see if any changes have been made
		// before exiting
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
		int row = treeTable.getSelectedRow();
		TreePath path = treeTable.getPathForRow(row);
		Object last = path.getLastPathComponent();
		TreePath parentPath = path.getParentPath();
		Object parentLast = parentPath.getLastPathComponent();
		NBTTreeTableModel model = treeTable.getTreeTableModel();
		int index = model.getIndexOfChild(parentLast, last);
		if (parentLast instanceof ByteArrayTag) {
		    ByteArrayTag baTag = (ByteArrayTag) parentLast;
		    byte[] value = baTag.getValue();
		    Byte[] bytes = ArrayUtils.toObject(value);
		    List<Byte> list = new ArrayList<Byte>(Arrays.asList(bytes));
		    index = (Integer) last;
		    list.remove(index);
		    bytes = new Byte[list.size()];
		    list.toArray(bytes);
		    value = ArrayUtils.toPrimitive(bytes);
		    baTag.setValue(value);
		} else if (parentLast instanceof ListTag) {
		    ListTag<? extends Tag<?>> listTag = (ListTag) parentLast;
		    List<? extends Tag<?>> list = listTag.getValue();
		    if (index != -1)
			list.remove(index);
		} else if (parentLast instanceof CompoundTag) {
		    CompoundTag compoundTag = (CompoundTag) parentLast;
		    Map<String, Tag<?>> map = compoundTag.getValue();
		    int i = 0;
		    for (String key : map.keySet()) {
			if (i++ == index - 1) {
			    map.remove(key);
			    break;
			}
		    }
		}

		updateTreeTable();

		path = treeTable.getPathForRow(row);
		if (path != null)
		    treeTable.setRowSelectionInterval(row, row);
	    }

	};

	addByteAction = new NBTAction("Add Byte", NBTConstants.TYPE_BYTE,
		"Add Byte", KeyEvent.VK_1) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('1', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new ByteTag("new byte", (byte) 0));
	    }

	};

	addShortAction = new NBTAction("Add Short", NBTConstants.TYPE_SHORT,
		"Add Short", KeyEvent.VK_2) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('2', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new ShortTag("new short", (short) 0));
	    }

	};

	addIntAction = new NBTAction("Add Integer", NBTConstants.TYPE_INT,
		"Add Integer", KeyEvent.VK_3) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('3', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new IntTag("new int", 0));
	    }

	};

	addLongAction = new NBTAction("Add Long", NBTConstants.TYPE_LONG,
		"Add Long", KeyEvent.VK_4) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('4', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new LongTag("new long", 0));
	    }

	};

	addFloatAction = new NBTAction("Add Float", NBTConstants.TYPE_FLOAT,
		"Add Float", KeyEvent.VK_5) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('5', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new FloatTag("new float", 0));
	    }

	};

	addDoubleAction = new NBTAction("Add Double", NBTConstants.TYPE_DOUBLE,
		"Add Double", KeyEvent.VK_6) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('6', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new DoubleTag("new double", 0));
	    }

	};

	addByteArrayAction = new NBTAction("Add Byte Array",
		NBTConstants.TYPE_BYTE_ARRAY, "Add Byte Array", KeyEvent.VK_7) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('7', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new ByteArrayTag("new byte array"));
	    }

	};

	addStringAction = new NBTAction("Add String", NBTConstants.TYPE_STRING,
		"Add String", KeyEvent.VK_8) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('8', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		addTag(new StringTag("new string", "..."));
	    }

	};

	addListAction = new NBTAction("Add List Tag", NBTConstants.TYPE_LIST,
		"Add List Tag", KeyEvent.VK_9) {

	    {
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke('9', Event.CTRL_MASK));
	    }

	    public void actionPerformed(ActionEvent e) {
		Class<? extends Tag> type = queryType();
		if (type != null)
		    addTag(new ListTag("new list", null, type));
	    }

	    private Class<? extends Tag> queryType() {
		Object[] items = { NBTConstants.TYPE_BYTE,
			NBTConstants.TYPE_SHORT, NBTConstants.TYPE_INT,
			NBTConstants.TYPE_LONG, NBTConstants.TYPE_FLOAT,
			NBTConstants.TYPE_DOUBLE, NBTConstants.TYPE_BYTE_ARRAY,
			NBTConstants.TYPE_STRING, NBTConstants.TYPE_LIST,
			NBTConstants.TYPE_COMPOUND };
		JComboBox comboBox = new JComboBox(new DefaultComboBoxModel(
			items));
		comboBox.setRenderer(new DefaultListCellRenderer() {

		    @Override
		    public Component getListCellRendererComponent(JList list,
			    Object value, int index, boolean isSelected,
			    boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

			if (value instanceof Integer) {
			    Integer i = (Integer) value;
			    Class<? extends Tag> c = NBTUtils.getTypeClass(i);
			    String name = NBTUtils.getTypeName(c);
			    setText(name);
			}

			return this;
		    }

		});
		Object[] message = { new JLabel("Please select a type."),
			comboBox };
		String title = "Title goes here";
		int result = JOptionPane.showOptionDialog(GUI.this, message,
			title, JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE, null, null, null);
		switch (result) {
		case JOptionPane.OK_OPTION:
		    ComboBoxModel model = comboBox.getModel();
		    Object item = model.getSelectedItem();
		    if (item instanceof Integer) {
			Integer i = (Integer) item;
			return NBTUtils.getTypeClass(i);
		    }
		}
		return null;
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

	String name = "About " + TITLE;
	helpAction = new NBTAction(name, "Help", name, KeyEvent.VK_F1) {

	    {
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
	    }

	    public void actionPerformed(ActionEvent e) {
		Object[] message = {
			new JLabel(TITLE + " " + VERSION),
			new JLabel(
				"\u00A9 Copyright Taggart Spilman 2011.  All rights reserved."),
			new Hyperlink(
				"<html><a href=\"#\">NamedBinaryTag.com</a></html>",
				"http://www.namedbinarytag.com"),
			new Hyperlink("<html><a href=\"#\">Contact</a></html>",
				"mailto:tagadvance@gmail.com"),
			new JLabel(" "),
			new Hyperlink(
				"<html><a href=\"#\">JNBT was written by Graham Edgecombe</a></html>",
				"http://jnbt.sf.net"),
			new Hyperlink(
				"<html><a href=\"#\">Available open-source under the BSD license</a></html>",
				"http://jnbt.sourceforge.net/LICENSE.TXT"),
			new JLabel(" "),
			new JLabel(
				"This product includes software developed by"),
			new Hyperlink(
				"<html><a href=\"#\">The Apache Software Foundation</a>.</html>",
				"http://www.apache.org"),
			new JLabel(" "),
			new JLabel("Default texture pack:"),
			new Hyperlink(
				"<html><a href=\"#\">SOLID COLOUR. SOLID STYLE.</a></html>",
				"http://www.minecraftforum.net/topic/72253-solid-colour-solid-style/"),
			new JLabel(
				"Bundled with the permission of Trigger_Proximity."),

		};
		String title = "About";
		JOptionPane.showMessageDialog(GUI.this, message, title,
			JOptionPane.INFORMATION_MESSAGE);
	    }

	};

    }

    protected void updateActions() {
	Map<Integer, Action> actionMap = new LinkedHashMap<Integer, Action>();
	actionMap.put(NBTConstants.TYPE_BYTE, addByteAction);
	actionMap.put(NBTConstants.TYPE_SHORT, addShortAction);
	actionMap.put(NBTConstants.TYPE_INT, addIntAction);
	actionMap.put(NBTConstants.TYPE_LONG, addLongAction);
	actionMap.put(NBTConstants.TYPE_FLOAT, addFloatAction);
	actionMap.put(NBTConstants.TYPE_DOUBLE, addDoubleAction);
	actionMap.put(NBTConstants.TYPE_BYTE_ARRAY, addByteArrayAction);
	actionMap.put(NBTConstants.TYPE_STRING, addStringAction);
	actionMap.put(NBTConstants.TYPE_LIST, addListAction);
	actionMap.put(NBTConstants.TYPE_COMPOUND, addCompoundAction);
	for (Action action : actionMap.values())
	    action.setEnabled(false);

	if (treeTable == null)
	    return;

	int row = treeTable.getSelectedRow();
	deleteAction.setEnabled(row > 0);
	if (row == -1)
	    return;
	TreePath path = treeTable.getPathForRow(row);
	Object last = path.getLastPathComponent();

	if (last instanceof ByteArrayTag || last instanceof Integer) {
	    addByteAction.setEnabled(true);
	} else if (last instanceof ListTag) {
	    ListTag list = (ListTag) last;
	    @SuppressWarnings("unchecked")
	    Class<Tag<?>> c = (Class<Tag<?>>) list.getType();
	    int type = NBTUtils.getTypeCode(c);
	    Action action = actionMap.get(type);
	    action.setEnabled(true);
	} else if (last instanceof CompoundTag) {
	    for (Action action : actionMap.values())
		action.setEnabled(true);
	}
    }

    private void initComponents() {
	setTitle(TITLE);
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
	btnBrowse.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		openAction.actionPerformed(e);
	    }

	});
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
					// .addContainerGap()
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

	int width = 440, height = 400;
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
	menuFile.add(new JMenuItem(refreshAction));
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
	toolBar.add(new ToolBarButton(refreshAction));
	toolBar.addSeparator();

	toolBar.add(new ToolBarButton(deleteAction));
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

    protected JFileChooser createFileChooser() {
	JFileChooser fc = new JFileChooser();
	fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	String description = "named binary tag";
	FileFilter filter = new FileNameExtensionFilter(description, "mcr",
		"dat", "dat_old");
	fc.setFileFilter(filter);
	Preferences prefs = getPreferences();
	String exportFile = prefs.get(KEY_FILE, null);
	if (exportFile == null) {
	    File cwd = new File(".");
	    fc.setCurrentDirectory(cwd);
	} else {
	    File selectedFile = new File(exportFile);
	    fc.setSelectedFile(selectedFile);
	}
	return fc;
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
	if (file.isDirectory()) {
	    doImportDirectory(file);
	} else {
	    doImportFile(file);
	}
    }

    public void doImportFile(File file) {
	String filename = file.getName();
	String extension = FilenameUtils.getExtension(filename);
	if (EXT_DAT.equals(extension) || EXT_DAT_OLD.equals(extension)) {
	    doImportDat(file);
	} else if (EXT_MCR.equals(extension)) {
	    doImportMCR(file);
	} else {
	    showErrorDialog("Unknown file extension.");
	}
    }

    public void doImportDat(final File file) {
	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	setCursor(waitCursor);

	SwingWorkerUnlimited.execure(new SwingWorker<CompoundTag, Void>() {

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
		textFile.setText(file.getAbsolutePath());

		updateTreeTable(tag);

		Cursor defaultCursor = Cursor.getDefaultCursor();
		setCursor(defaultCursor);
	    }

	});
    }

    public void doImportMCR(final File file) {
	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	setCursor(waitCursor);

	SwingWorkerUnlimited.execure(new SwingWorker<Region, Void>() {

	    @Override
	    protected Region doInBackground() throws Exception {
		WorldRegion region = new WorldRegion(file);
		region.getChunks(); // load from disk
		return region;
	    }

	    @Override
	    protected void done() {
		Region region = null;
		try {
		    region = get();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} catch (ExecutionException e) {
		    e.printStackTrace();
		    Throwable cause = ExceptionUtils.getRootCause(e);
		    showErrorDialog(cause.getMessage());
		    return;
		}
		textFile.setText(file.getAbsolutePath());

		updateTreeTable(region);

		Cursor defaultCursor = Cursor.getDefaultCursor();
		setCursor(defaultCursor);
	    }

	});
    }

    public void doImportDirectory(final File base) {
	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	setCursor(waitCursor);

	SwingWorkerUnlimited.execure(new SwingWorker<WorldDirectory, Void>() {

	    @Override
	    protected WorldDirectory doInBackground() throws Exception {
		return new WorldDirectory(base);
	    }

	    @Override
	    protected void done() {
		WorldDirectory world = null;
		try {
		    world = get();

		    createAndShowTileCanvas(world);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} catch (ExecutionException e) {
		    e.printStackTrace();
		    Throwable cause = ExceptionUtils.getRootCause(e);
		    showErrorDialog(cause.getMessage());
		    return;
		}
		textFile.setText(base.getAbsolutePath());

		updateTreeTable(world);

		Cursor defaultCursor = Cursor.getDefaultCursor();
		setCursor(defaultCursor);
	    }

	});
    }

    private void createAndShowTileCanvas(World world) {
	JFrame frame = new JFrame("World Editor");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	TileCanvas tileCanvas = new TileCanvas(world) {
	    @Override
	    protected void blockClicked(Block block) {
		// TODO: fix this
		TreePath path = treeTable.getPathForNode(block);
		if (path != null) {
		    treeTable.expandPath(path);
		    treeTable.scrollPathToVisible(path);
		    System.err.println("path found");
		} else
		    System.err.println("no path found");
	    }
	};
	tileCanvas.setTileWidth(32);
	tileCanvas.setTileHeight(32);
	tileCanvas.setAltitude(70);
	frame.add(tileCanvas);
	frame.pack();
	frame.setVisible(true);
    }

    public void doExport(final File file) {
	if (true) {
	    // TODO: fix this
	    showErrorDialog("export temporarily disabled");
	    return;
	}

	textFile.setText(file.getAbsolutePath());

	Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	setCursor(waitCursor);

	NBTTreeTableModel model = treeTable.getTreeTableModel();
	final Object root = model.getRoot();

	SwingWorkerUnlimited.execure(new SwingWorker<Void, Void>() {

	    @Override
	    protected Void doInBackground() throws Exception {
		NBTOutputStream ns = null;
		try {
		    ns = new NBTOutputStream(new FileOutputStream(file));
		    // ns.writeTag(root);
		} finally {
		    IOUtils.closeQuietly(ns);
		}
		return null;
	    }

	    @Override
	    protected void done() {
		Cursor defaultCursor = Cursor.getDefaultCursor();
		setCursor(defaultCursor);

		try {
		    get();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} catch (ExecutionException e) {
		    e.printStackTrace();
		    Throwable cause = ExceptionUtils.getRootCause(e);
		    showErrorDialog(cause.getMessage());
		    return;
		}
	    }

	});
    }

    public void addTag(Tag<?> tag) {
	if (treeTable == null) {
	    showErrorDialog("Tree is null");
	    return;
	}

	Object scroll = tag;

	int row = treeTable.getSelectedRow();
	TreePath path = treeTable.getPathForRow(row);
	Object last = path.getLastPathComponent();
	if (last instanceof ByteArrayTag) {
	    ByteArrayTag baTag = (ByteArrayTag) last;
	    byte[] value = baTag.getValue();
	    byte[] newValue = new byte[value.length + 1];
	    System.arraycopy(value, 0, newValue, 0, value.length);
	    baTag.setValue(newValue);
	} else if (last instanceof Integer) {
	    int index = (Integer) last + 1;
	    TreePath parentPath = path.getParentPath();
	    Object parentLast = parentPath.getLastPathComponent();
	    if (parentLast instanceof ByteArrayTag) {
		ByteArrayTag baTag = (ByteArrayTag) parentLast;
		byte[] value = baTag.getValue();
		Byte[] bytes = ArrayUtils.toObject(value);
		List<Byte> list = new ArrayList<Byte>(Arrays.asList(bytes));
		list.add(index, (byte) 0);
		bytes = list.toArray(bytes);
		value = ArrayUtils.toPrimitive(bytes);
		baTag.setValue(value);
		scroll = index;
	    }
	} else if (last instanceof ListTag) {
	    ListTag listTag = (ListTag) last;
	    List list = (List) listTag.getValue();
	    list.add(tag);
	} else if (last instanceof CompoundTag) {
	    CompoundTag compoundTag = (CompoundTag) last;
	    Map<String, Tag<?>> list = compoundTag.getValue();
	    list.put(tag.getName(), tag);
	} else {
	    return;
	}

	updateTreeTable();

	path = treeTable.getPathForNode(scroll);
	row = treeTable.getRowForPath(path);
	if (row != -1) {
	    treeTable.setRowSelectionInterval(row, row);
	    treeTable.scrollPathToVisible(path);
	}
    }

    protected void updateTreeTable() {
	// TODO: find a more elegant way to add nodes
	NBTTreeTableModel model = treeTable.getTreeTableModel();
	Object root = model.getRoot();
	updateTreeTable(root);
    }

    protected void updateTreeTable(Object root) {
	treeTable = new NBTTreeTable(root);
	treeTable.addTreeSelectionListener(new TreeSelectionListener() {

	    @Override
	    public void valueChanged(TreeSelectionEvent e) {
		updateActions();
	    }

	});
	scrollPane.setViewportView(treeTable);

	updateActions();
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