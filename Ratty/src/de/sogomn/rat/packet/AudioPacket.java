package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.fx.Sound;
import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;

public final class AudioPacket implements IPacket {
	
	private byte[] data;
	
	public AudioPacket(final File file) {
		data = FileUtils.readExternalData(file);
	}
	
	public AudioPacket(final String path) {
		data = FileUtils.readExternalData(path);
	}
	
	public AudioPacket() {
		data = new byte[0];
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeInt(data.length);
		connection.write(data);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		final int length = connection.readInt();
		
		data = new byte[length];
		
		connection.read(data);
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		final Sound sound = Sound.loadSound(data);
		
		sound.play();
	}
	
	public byte[] getData() {
		return data;
	}
	
}
