package de.sogomn.rat;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import de.sogomn.engine.net.TCPConnection;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.PacketType;


public final class ActiveClient extends TCPConnection {
	
	private LinkedBlockingQueue<IPacket> packetQueue;
	
	private Thread sender, reader;
	
	private IClientObserver observer;
	
	public ActiveClient(final String address, final int port) {
		super(address, port);
		
		packetQueue = new LinkedBlockingQueue<IPacket>();
	}
	
	public ActiveClient(final Socket socket) {
		super(socket);
		
		packetQueue = new LinkedBlockingQueue<IPacket>();
	}
	
	private IPacket nextPacket() {
		try {
			final IPacket packet = packetQueue.take();
			
			return packet;
		} catch (final InterruptedException ex) {
			return null;
		}
	}
	
	private void sendPacket(final IPacket packet) {
		final byte id = PacketType.getId(packet);
		
		if (id != 0) {
			writeByte(id);
			packet.send(this);
		}
	}
	
	private IPacket readPacket() {
		final byte id = readByte();
		final Class<? extends IPacket> packetClass = PacketType.getClass(id);
		
		if (packetClass == null) {
			return null;
		}
		
		try {
			final IPacket packet = packetClass.newInstance();
			
			packet.receive(this);
			
			return packet;
		} catch (final InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	@Override
	public void close() {
		super.close();
		
		if (sender != null) {
			sender.interrupt();
			sender = null;
		}
		
		if (reader != null) {
			reader.interrupt();
			reader = null;
		}
		
		if (packetQueue != null) {
			packetQueue.clear();
		}
		
		if (observer != null) {
			observer.clientDisconnected(this);
		}
	}
	
	public void start() {
		final Runnable sendingRunnable = () -> {
			while (isOpen()) {
				final IPacket packet = nextPacket();
				
				if (packet != null) {
					sendPacket(packet);
				}
			}
		};
		
		final Runnable readingRunnable = () -> {
			while (isOpen()) {
				final IPacket packet = readPacket();
				
				if (observer != null && packet != null) {
					observer.packetReceived(this, packet);
				}
			}
		};
		
		sender = new Thread(sendingRunnable);
		reader = new Thread(readingRunnable);
		
		sender.start();
		reader.start();
	}
	
	public void addPacket(final IPacket packet) {
		packetQueue.add(packet);
	}
	
	public void removePacket(final IPacket packet) {
		packetQueue.remove(packet);
	}
	
	public void setObserver(final IClientObserver observer) {
		this.observer = observer;
	}
	
	public boolean isIdling() {
		return packetQueue.isEmpty();
	}
	
}
