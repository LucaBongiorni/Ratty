package de.sogomn.rat.gui.server;

import java.io.File;

public interface IRattyGui {
	
	String getInput(final String message);
	
	default String getInput() {
		return getInput(null);
	}
	
	File getFile(final String type);
	
	default File getFile() {
		return getFile(null);
	}
	
	File getSaveFile(final String type);
	
	default File getSaveFile() {
		return getSaveFile(null);
	}
	
	void showMessage(final String message);
	
	void showError(final String message);
	
	boolean showWarning(final String message, final String yes, final String no);
	
	int showOptions(final String message, final String yes, final String no, final String cancel);
	
	void addClient(final ServerClient client);
	
	void removeClient(final ServerClient client);
	
	void update();
	
}
