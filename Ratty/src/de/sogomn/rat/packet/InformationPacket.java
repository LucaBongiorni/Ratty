package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;

public final class InformationPacket implements IPacket {
	
	private String name, os, version;
	
	public InformationPacket(final String name, final String os, final String version) {
		this.name = name;
		this.os = os;
		this.version = version;
	}
	
	public InformationPacket() {
		this("", "", "");
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeUTF(name);
		client.writeUTF(os);
		client.writeUTF(version);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		name = client.readUTF();
		os = client.readUTF();
		version = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		//...
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
