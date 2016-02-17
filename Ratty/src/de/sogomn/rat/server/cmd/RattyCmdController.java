package de.sogomn.rat.server.cmd;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.Ratty;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.server.AbstractRattyController;
import de.sogomn.rat.server.cmd.CommandLineReader.Command;

public final class RattyCmdController extends AbstractRattyController implements ICommandLineListener {
	
	private CommandLineReader reader;
	
	private static final String FREE = "Free";
	private static final String EXIT = "Exit";
	private static final String LIST = "List";
	private static final String POPUP = "Popup";
	private static final String HELP = "Help";
	
	private static final String HELP_STRING = Ratty.LANGUAGE.getString("cmd.help");
	
	public RattyCmdController() {
		reader = new CommandLineReader();
		
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
	 * HANDLING
	 * ==================================================
	 */
	
	private void freeClient(final Command command) {
		final String input = command.argument(0);
		final ActiveConnection connection = getConnection(input);
		
		if (connection != null) {
			final FreePacket packet = new FreePacket();
			
			connection.addPacket(packet);
		}
	}
	
	private void listClients() {
		connections.stream().forEach(connection -> {
			final int index = connections.indexOf(connection);
			
			System.out.println(index + ": " + connection.getAddress());
		});
	}
	
	private void sendPopup(final Command command) {
		final String connectionInput = command.argument(0);
		final ActiveConnection connection = getConnection(connectionInput);
		final String message = command.argument(1);
		
		if (connection != null && message != null) {
			final PopupPacket packet = new PopupPacket();
			
			connection.addPacket(packet);
		}
	}
	
	/*
	 * ==================================================
	 * HANDLING END
	 * ==================================================
	 */
	
	@Override
	public void packetReceived(final ActiveConnection connection, final IPacket packet) {
		//...
	}
	
	@Override
	public void commandInput(final Command command) {
		if (command.equals(EXIT)) {
			System.exit(0);
		} else if (command.equals(FREE)) {
			freeClient(command);
		} else if (command.equals(LIST)) {
			listClients();
		} else if (command.equals(POPUP)) {
			sendPopup(command);
		} else if (command.equals(HELP)) {
			System.out.println(HELP_STRING);
		}
	}
	
}
