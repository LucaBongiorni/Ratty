package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;

public final class PingPacket extends AbstractPingPongPacket {
	
	private long milliseconds;
	
	public PingPacket() {
		type = REQUEST;
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		final long time = System.currentTimeMillis();
		
		connection.writeLong(time);
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		connection.writeLong(milliseconds);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		final long time = connection.readLong();
		
		milliseconds = System.currentTimeMillis() - time;
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		milliseconds = connection.readLong();
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		type = DATA;
		
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
		//...
	}
	
	public long getMilliseconds() {
		return milliseconds;
	}
	
}
