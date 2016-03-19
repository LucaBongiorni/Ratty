package de.sogomn.rat.packet;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import de.sogomn.rat.ActiveConnection;

public final class WebsitePacket implements IPacket {
	
	private String address;
	private int amount;
	
	private static final String HTTP_PREFIX = "http://";
	
	public WebsitePacket(final String address, final int amount) {
		this.amount = amount;
		
		final boolean hasPrefix = address.startsWith(HTTP_PREFIX);
		
		if (hasPrefix) {
			this.address = address;
		} else {
			this.address = HTTP_PREFIX + address;
		}
	}
	
	public WebsitePacket(final String address) {
		this(address, 1);
	}
	
	public WebsitePacket() {
		this("");
	}
	
	private void openWebsite(final String address) {
		final boolean desktopSupported = Desktop.isDesktopSupported();
		
		if (desktopSupported) {
			final Desktop desktop = Desktop.getDesktop();
			final boolean canBrowse = desktop.isSupported(Action.BROWSE);
			
			if (canBrowse) {
				try {
					final URI uri = new URI(address);
					
					desktop.browse(uri);
				} catch (final IOException | URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(address);
		connection.writeInt(amount);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		address = connection.readUTF();
		amount = connection.readInt();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		for (int i = 0; i < amount; i++) {
			openWebsite(address);
		}
	}
	
	public String getAddress() {
		return address;
	}
	
}
