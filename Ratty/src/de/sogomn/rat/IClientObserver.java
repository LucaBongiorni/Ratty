package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;

public interface IClientObserver {
	
	void packetReceived(final ActiveClient client, final IPacket packet);
	
	void disconnected(final ActiveClient client);
	
}
