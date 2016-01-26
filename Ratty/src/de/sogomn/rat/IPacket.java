package de.sogomn.rat;



public interface IPacket {
	
	public abstract void send(final ActiveClient client);
	
	public abstract void receive(final ActiveClient client);
	
	public abstract void execute();
	
}
