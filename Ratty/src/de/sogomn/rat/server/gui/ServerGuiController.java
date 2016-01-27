package de.sogomn.rat.server.gui;

import java.util.ArrayList;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.IClientObserver;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.IServerObserver;

public final class ServerGuiController implements IServerObserver, IClientObserver, IGuiController {
	
	private ArrayList<ActiveClient> clients;
	
	public ServerGuiController() {
		clients = new ArrayList<ActiveClient>();
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		packet.execute();
	}
	
	@Override
	public void disconnected(final ActiveClient client) {
		client.setObserver(null);
		client.close();
		clients.remove(client);
	}
	
	@Override
	public void clientConnected(final ActiveServer server, final ActiveClient client) {
		client.setObserver(this);
		clients.add(client);
		client.start();
	}
	
	@Override
	public void closed(final ActiveServer server) {
		System.out.println("Server closed");
	}
	
	@Override
	public void userInput(final String actionCommand) {
		System.out.println("User input: " + actionCommand);
	}
	
}
