package de.sogomn.rat.server.gui;

import de.sogomn.rat.ActiveClient;

public final class ServerClient {
	
	private String name, os, version;
	private boolean loggedIn;
	
	private boolean streamingDesktop;
	private boolean streamingVoice;
	
	private DisplayPanel displayPanel;
	private FileTreePanel treePanel;
	
	final ActiveClient client;
	
	public ServerClient(final ActiveClient client) {
		this.client = client;
		
		displayPanel = new DisplayPanel();
		treePanel = new FileTreePanel();
	}
	
	public void logIn(final String name, final String os, final String version) {
		this.name = name;
		this.os = os;
		this.version = version;
		
		loggedIn = true;
		
		displayPanel.setTitle(name);
		treePanel.setTitle(name);
	}
	
	public void setStreamingDesktop(final boolean streamingDesktop) {
		this.streamingDesktop = streamingDesktop;
	}
	
	public void setStreamingVoice(final boolean streamingVoice) {
		this.streamingVoice = streamingVoice;
	}
	
	public void setController(final IGuiController controller) {
		displayPanel.setController(controller);
		treePanel.setController(controller);
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return client.getAddress();
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
	
	public boolean isStreamingVoice() {
		return streamingVoice;
	}
	
	public DisplayPanel getDisplayPanel() {
		return displayPanel;
	}
	
	public FileTreePanel getTreePanel() {
		return treePanel;
	}
	
}
