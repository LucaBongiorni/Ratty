package de.sogomn.rat;

import java.net.Socket;

import de.sogomn.engine.net.TCPConnection;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.PacketType;


public final class ActiveClient extends TCPConnection {
	
	private Thread thread;
	
	private IClientObserver observer;
	
	public ActiveClient(final String address, final int port) {
		super(address, port);
	}
	
	public ActiveClient(final Socket socket) {
		super(socket);
	}
	
	@Override
	public void close() {
		super.close();
		
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
		
		if (observer != null) {
			observer.disconnected(this);
		}
	}
	
	public void start() {
		final Runnable runnable = () -> {
			while (isOpen()) {
				final IPacket packet = readPacket();
				
				if (observer != null && packet != null) {
					observer.packetReceived(this, packet);
				}
			}
		};
		
		thread = new Thread(runnable);
		
		thread.start();
	}
	
	public void sendPacket(final IPacket packet) {
		final byte id = PacketType.getId(packet.getClass());
		
		if (id != 0) {
			writeByte(id);
			packet.send(this);
		}
	}
	
	public IPacket readPacket() {
		final byte id = readByte();
		
		if (id == 0) {
			return null;
		}
		
		final Class<? extends IPacket> packetClass = PacketType.getClass(id);
		
		try {
			final IPacket packet = packetClass.newInstance();
			
			packet.receive(this);
			
			return packet;
		} catch (final InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public void setObserver(final IClientObserver observer) {
		this.observer = observer;
	}
	
}
