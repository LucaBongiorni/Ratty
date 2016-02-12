package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;

public final class FreePacket implements IPacket {
	
	public FreePacket() {
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
		client.setObserver(null);
		client.close();
		
		System.exit(0);
	}
	
}
