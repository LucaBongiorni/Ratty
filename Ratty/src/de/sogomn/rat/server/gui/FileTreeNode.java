package de.sogomn.rat.server.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public final class FileTreeNode implements MutableTreeNode {
	
	private FileTreeNode parent;
	private ArrayList<FileTreeNode> children;
	
	private String name;
	
	public FileTreeNode(final String name) {
		this.name = name;
		
		children = new ArrayList<FileTreeNode>();
	}
	
	@Override
	public Enumeration<FileTreeNode> children() {
		final Enumeration<FileTreeNode> enumeration = Collections.enumeration(children);
		
		return enumeration;
	}
	
	public FileTreeNode[] getChildren() {
		final FileTreeNode[] childArray = children.stream().toArray(FileTreeNode[]::new);
		
		return childArray;
	}
	
	@Override
	public void insert(final MutableTreeNode child, int index) {
		final boolean fileTreeNode = child instanceof FileTreeNode;
		final int size = children.size();
		
		/*To reverse the order*/
		index = size - index;
		
		if (index < 0 || index > size || !fileTreeNode) {
			return;
		}
		
		final FileTreeNode fileTreeNodeChild = (FileTreeNode)child;
		
		fileTreeNodeChild.setParent(this);
		children.add(index, fileTreeNodeChild);
	}
	
	@Override
	public void remove(final int index) {
		if (index < 0 || index > children.size() - 1) {
			return;
		}
		
		children.remove(index);
	}
	
	@Override
	public void remove(final MutableTreeNode node) {
		children.remove(node);
	}
	
	@Override
	public void removeFromParent() {
		if (parent == null) {
			return;
		}
		
		parent.remove(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public void setParent(final MutableTreeNode newParent) {
		final boolean fileTreeNode = newParent instanceof FileTreeNode;
		
		if (!fileTreeNode) {
			return;
		}
		
		final FileTreeNode fileTreeNodeParent = (FileTreeNode)newParent;
		
		parent = fileTreeNodeParent;
	}
	
	@Override
	public void setUserObject(final Object object) {
		name = String.valueOf(object);
	}
	
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	
	@Override
	public FileTreeNode getChildAt(final int childIndex) {
		if (childIndex < 0 || childIndex > children.size() - 1) {
			return null;
		}
		
		final FileTreeNode child = children.get(childIndex);
		
		return child;
	}
	
	@Override
	public int getChildCount() {
		return children.size();
	}
	
	@Override
	public int getIndex(final TreeNode node) {
		final int index = children.indexOf(node);
		
		return index;
	}
	
	@Override
	public FileTreeNode getParent() {
		return parent;
	}
	
	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}
	
	public String getPath() {
		final StringBuilder builder = new StringBuilder();
		
		FileTreeNode current = this;
		
		while (current != null) {
			final String name = current.getName();
			
			builder.insert(0, name + File.separator);
			
			current = current.getParent();
		}
		
		final String path = builder.toString();
		
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public FileTreeNode getChild(final String name) {
		for (final FileTreeNode child : children) {
			final String childName = child.getName();
			
			if (childName.equals(name)) {
				return child;
			}
		}
		
		return null;
	}
	
	public FileTreeNode getDeepChild(final String... names) {
		FileTreeNode current = this;
		
		for (final String name : names) {
			current = current.getChild(name);
			
			if (current == null) {
				return null;
			}
		}
		
		return current;
	}
	
}
