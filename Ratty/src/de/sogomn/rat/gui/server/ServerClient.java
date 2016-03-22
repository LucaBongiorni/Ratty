package de.sogomn.rat.gui.server;

import javax.swing.ImageIcon;

import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.gui.ChatWindow;
import de.sogomn.rat.gui.DisplayPanel;
import de.sogomn.rat.gui.FileTree;
import de.sogomn.rat.gui.IGuiController;

final class ServerClient extends AbstractListenerContainer<IGuiController> implements IGuiController {
	
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
		
		displayPanel = new DisplayPanel();
		fileTree = new FileTree();
		chat = new ChatWindow();
		
		displayPanel.addListener(this);
		fileTree.addListener(this);
		chat.addListener(this);
	}
	
	@Override
	public void userInput(final String command, final Object source) {
		notifyListeners(controller -> controller.userInput(command, this));
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
