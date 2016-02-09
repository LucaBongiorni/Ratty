package de.sogomn.rat.packet;

import de.sogomn.engine.fx.Sound;
import de.sogomn.rat.ActiveClient;

public final class VoicePacket extends AbstractPingPongPacket {
	
	private byte[] data;
	
	public VoicePacket() {
		type = REQUEST;
		data = new byte[0];
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
		client.writeInt(data.length);
		client.write(data);
	}
	
	@Override
	protected void receiveRequest(final ActiveClient client) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
		final int length = client.readInt();
		
		data = new byte[length];
		
		client.read(data);
	}
	
	@Override
	protected void executeRequest(final ActiveClient client) {
		type = DATA;
		
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		final Sound sound = Sound.loadSound(data);
		
		sound.play();
	}
	
	public void setData(final byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Sound getSound() {
		final Sound sound = Sound.loadSound(data);
		
		return sound;
	}
	
}
