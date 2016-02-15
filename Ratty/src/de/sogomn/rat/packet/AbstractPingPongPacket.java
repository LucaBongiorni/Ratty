package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;

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
	
	protected abstract void sendRequest(final ActiveConnection connection);
	
	protected abstract void sendData(final ActiveConnection connection);
	
	protected abstract void receiveRequest(final ActiveConnection connection);
	
	protected abstract void receiveData(final ActiveConnection connection);
	
	protected abstract void executeRequest(final ActiveConnection connection);
	
	protected abstract void executeData(final ActiveConnection connection);
	
	@Override
	public final void send(final ActiveConnection connection) {
		connection.writeByte(type);
		
		if (type == REQUEST) {
			sendRequest(connection);
		} else if (type == DATA) {
			sendData(connection);
		}
	}
	
	@Override
	public final void receive(final ActiveConnection connection) {
		type = connection.readByte();
		
		if (type == REQUEST) {
			receiveRequest(connection);
		} else if (type == DATA) {
			receiveData(connection);
		}
	}
	
	@Override
	public final void execute(final ActiveConnection connection) {
		if (type == REQUEST) {
			executeRequest(connection);
		} else if (type == DATA) {
			executeData(connection);
		}
	}
	
	public final byte getType() {
		return type;
	}
	
}
