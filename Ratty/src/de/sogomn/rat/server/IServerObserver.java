package de.sogomn.rat.server;

import de.sogomn.rat.ActiveConnection;

public interface IServerObserver {
	
	void connected(final ActiveServer server, final ActiveConnection connection);
	
	void closed(final ActiveServer server);
	
}
