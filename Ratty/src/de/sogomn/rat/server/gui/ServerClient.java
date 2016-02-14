package de.sogomn.rat.server.gui;

import de.sogomn.rat.ActiveConnection;

public final class ServerClient {
	
	private boolean loggedIn;
	
	private String name, os, version;
	private boolean streamingDesktop, streamingVoice;
	
	private DisplayController displayPanel;
	private FileTreePanel treePanel;
	
	final ActiveConnection connection;
	
	public ServerClient(final ActiveConnection connection) {
		this.connection = connection;
		
		displayPanel = new DisplayController(connection);
		treePanel = new FileTreePanel();
	}
	
	public void logIn(final String name, final String os, final String version) {
		this.name = name;
		this.os = os;
		this.version = version;
		
		loggedIn = true;
		
		treePanel.setTitle(name);
	}
	
	public void setStreamingDesktop(final boolean streamingDesktop) {
		this.streamingDesktop = streamingDesktop;
	}
	
	public void setStreamingVoice(final boolean streamingVoice) {
		this.streamingVoice = streamingVoice;
	}
	
	public void setController(final IGuiController controller) {
		treePanel.setController(controller);
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return connection.getAddress();
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
	
	public DisplayController getDisplayPanel() {
		return displayPanel;
	}
	
	public FileTreePanel getTreePanel() {
		return treePanel;
	}
	
}
