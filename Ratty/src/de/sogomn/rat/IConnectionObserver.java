package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;

public interface IConnectionObserver {
	
	void packetReceived(final ActiveConnection connection, final IPacket packet);
	
	void disconnected(final ActiveConnection connection);
	
}
