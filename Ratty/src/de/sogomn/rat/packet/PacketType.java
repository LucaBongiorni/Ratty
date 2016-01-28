package de.sogomn.rat.packet;

import de.sogomn.rat.packet.request.InformationRequestPacket;
import de.sogomn.rat.packet.request.ScreenshotRequestPacket;

public enum PacketType {
	
	POPUP(1, PopupPacket.class),
	IMAGE(2, ImagePacket.class),
	KEY_EVENT(3, KeyEventPacket.class),
	FREE(4, FreePacket.class),
	INFORMATION(5, InformationPacket.class),
	
	SCREENSHOT(6, ScreenshotRequestPacket.class),
	INFORMATION_REQUEST(7, InformationRequestPacket.class);
	
	public final byte id;
	public final Class<? extends IPacket> clazz;
	
	PacketType(final byte id, final Class<? extends IPacket> clazz) {
		this.id = id;
		this.clazz = clazz;
	}
	
	PacketType(final int id, final Class<? extends IPacket> clazz) {
		this((byte)id, clazz);
	}
	
	public static Class<? extends IPacket> getClass(final byte id) {
		final PacketType[] values = values();
		
		for (final PacketType type : values) {
			if (type.id == id) {
				return type.clazz;
			}
		}
		
		return null;
	}
	
	public static byte getId(final Class<? extends IPacket> clazz) {
		final PacketType[] values = values();
		
		for (final PacketType type : values) {
			if (type.clazz == clazz) {
				return type.id;
			}
		}
		
		return 0;
	}
	
	public static byte getId(final IPacket packet) {
		final Class<? extends IPacket> clazz = packet.getClass();
		
		return getId(clazz);
	}
	
}
