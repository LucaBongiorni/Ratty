package de.sogomn.rat.packet;

import de.sogomn.rat.ActiveConnection;



public interface IPacket {
	
	void send(final ActiveConnection connection);
	
	void receive(final ActiveConnection connection);
	
	void execute(final ActiveConnection connection);
	
}
