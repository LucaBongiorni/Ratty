package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;



public interface IPacket {
	
	void send(final ActiveConnection client);
	
	void receive(final ActiveConnection client);
	
	void execute(final ActiveConnection client);
	
}
