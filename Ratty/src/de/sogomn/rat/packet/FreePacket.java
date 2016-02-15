package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;

public final class FreePacket implements IPacket {
	
	public FreePacket() {
		//...
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		//...
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		//...
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		connection.setObserver(null);
		connection.close();
		
		System.exit(0);
	}
	
}
