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
import java.awt.event.KeyEvent;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.TreePath;

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
import org.jnbt.NBTUtils;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import com.nbt.repo.Repository;
import com.tag.Hyperlink;
import com.tag.ImageFactory;

@SuppressWarnings("serial")
public class DataFrame extends JInternalFrame implements Repository {

	public static final String KEY_FILE = "file";

	private Repository repository;

	private JPanel contentPane;
	private NBTTreeTable treeTable;
	private JScrollPane scrollPane;

	protected Action saveAction;
	protected Action refreshAction;

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

	public DataFrame(String title, Repository repo) {
		super(title, true, false, true, true);
		setRepository(repo);

		createActions();
		initComponents();

		updateActions();
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException("repository must not be null");
		this.repository = repository;
	}

	private void createActions() {
		saveAction = new NBTAction("Save", "Save", "Save", KeyEvent.VK_S) {

			{
				putValue(ACCELERATOR_KEY,
						KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				doExport();
			}

		};

		refreshAction = new NBTAction("Refresh", "Refresh", "Refresh",
				KeyEvent.VK_F5) {

			{
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F5"));
			}

			public void actionPerformed(ActionEvent e) {
				// TODO: this should check to see if any changes have been made before exiting
				doImport();
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
					ListTag listTag = (ListTag) parentLast;
					List<Tag<?>> list = listTag.getValue();
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

				CompoundTag root = model.getRoot();
				updateTreeTable(root);

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
				Object[] items = {
						NBTConstants.TYPE_BYTE, NBTConstants.TYPE_SHORT,
						NBTConstants.TYPE_INT, NBTConstants.TYPE_LONG,
						NBTConstants.TYPE_FLOAT, NBTConstants.TYPE_DOUBLE,
						NBTConstants.TYPE_BYTE_ARRAY, NBTConstants.TYPE_STRING,
						NBTConstants.TYPE_LIST, NBTConstants.TYPE_COMPOUND
				};
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
				Object[] message = {
						new JLabel("Please select a type."), comboBox
				};
				String title = "Title goes here";
				int result = JOptionPane.showOptionDialog(DataFrame.this,
						message, title, JOptionPane.OK_CANCEL_OPTION,
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

		helpAction = new NBTAction("About NBT Pro", "Help", "About NBT Pro",
				KeyEvent.VK_F1) {

			{
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
			}

			public void actionPerformed(ActionEvent e) {
				Object[] message = {
						new JLabel("NBT Pro v1.0.0"),
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

				};
				String title = "About";
				JOptionPane.showMessageDialog(DataFrame.this, message, title,
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
		setTitle("Data Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
		JToolBar toolBar = createToolBar();

		contentPane = new JPanel();
		setContentPane(contentPane);

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
						.addComponent(toolBar, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addContainerGap()));

		contentPane.setLayout(gl_contentPane);

		int width = 440, height = 400;
		setMinimumSize(new Dimension(width, height));

		pack();
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menuFile.add(new JMenuItem(saveAction));
		menuFile.add(new JMenuItem(refreshAction));
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

		toolBar.add(new ToolBarButton(saveAction));
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

	public void doImport() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		setCursor(waitCursor);

		new SwingWorker<Tag<?>, Void>() {

			@Override
			protected Tag<?> doInBackground() throws Exception {
				return repository.load();
			}

			@Override
			protected void done() {
				Tag<?> tag = null;
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

				if (tag instanceof CompoundTag) {
					CompoundTag compoundTag = (CompoundTag) tag;
					updateTreeTable(compoundTag);
				}

				Cursor defaultCursor = Cursor.getDefaultCursor();
				setCursor(defaultCursor);
			}

		}.execute();
	}

	public void doExport() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		setCursor(waitCursor);

		NBTTreeTableModel model = treeTable.getTreeTableModel();
		final CompoundTag tag = model.getRoot();

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				repository.save(tag);
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

		}.execute();
	}

	public void doLoad() throws IOException {
		Tag<?> tag = load();
		updateTreeTable(tag);
	}
	
	@Override
	public Tag<?> load() throws IOException {
		Repository repo = getRepository();
		return repo.load();
	}

	public void doSave() throws IOException {
		NBTTreeTableModel model = treeTable.getTreeTableModel();
		CompoundTag tag = model.getRoot();
		save(tag);
	}

	@Override
	public void save(Tag<?> tag) throws IOException {
		Repository repo = getRepository();
		repo.save(tag);
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
			List<Tag<?>> list = listTag.getValue();
			list.add(tag);
		} else if (last instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag) last;
			Map<String, Tag<?>> list = compoundTag.getValue();
			list.put(tag.getName(), tag);
		} else {
			return;
		}

		// TODO: find a more elegant way to add nodes
		NBTTreeTableModel model = treeTable.getTreeTableModel();
		CompoundTag root = model.getRoot();
		updateTreeTable(root);

		path = treeTable.getPathForNode(scroll);
		row = treeTable.getRowForPath(path);
		if (row != -1) {
			treeTable.setRowSelectionInterval(row, row);
			treeTable.scrollPathToVisible(path);
		}
	}

	protected void updateTreeTable(Tag<?> tag) {
		treeTable = new NBTTreeTable(tag);
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
		JOptionPane.showMessageDialog(DataFrame.this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public Preferences getPreferences() {
		return Preferences.userNodeForPackage(getClass());
	}

}