package de.sogomn.rat.server;

import de.sogomn.rat.ActiveClient;

public interface IServerObserver {
	
	void clientConnected(final ActiveServer server, final ActiveClient client);
	
	void closed(final ActiveServer server);
	
}
