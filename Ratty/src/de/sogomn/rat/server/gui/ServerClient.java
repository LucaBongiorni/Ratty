package de.sogomn.rat.server.gui;

import de.sogomn.rat.ActiveConnection;

public final class ServerClient {
	
	private boolean loggedIn;
	
	private String name, os, version;
	private boolean streamingDesktop, streamingVoice;
	
	final ActiveConnection connection;
	final DisplayController displayController;
	final FileTreeController fileTreeController;
	
	public ServerClient(final ActiveConnection connection) {
		this.connection = connection;
		
		displayController = new DisplayController(this);
		fileTreeController = new FileTreeController(this);
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
	
	public void setStreamingVoice(final boolean streamingVoice) {
		this.streamingVoice = streamingVoice;
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
	
}
