package de.sogomn.rat.packet;

import de.sogomn.engine.fx.Sound;
import de.sogomn.rat.ActiveConnection;

public final class VoicePacket extends AbstractPingPongPacket {
	
	private byte[] data;
	
	public VoicePacket() {
		type = REQUEST;
		data = new byte[0];
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		connection.writeInt(data.length);
		connection.write(data);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		final int length = connection.readInt();
		
		data = new byte[length];
		
		connection.read(data);
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		type = DATA;
		
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
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
