package de.sogomn.rat;

public interface IClientObserver {
	
	void packetReceived(final ActiveClient client, final IPacket packet);
	
	void disconnected(final ActiveClient client);
	
}
