package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;

public final class FreePacket implements IPacket {
	
	public FreePacket() {
		//...
	}
	
	@Override
	public void send(final ActiveConnection client) {
		//...
	}
	
	@Override
	public void receive(final ActiveConnection client) {
		//...
	}
	
	@Override
	public void execute(final ActiveConnection client) {
		client.setObserver(null);
		client.close();
		
		System.exit(0);
	}
	
}
