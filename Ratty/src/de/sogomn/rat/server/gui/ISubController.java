package de.sogomn.rat.server.gui;

import de.sogomn.rat.packet.IPacket;

public interface ISubController extends IGuiController {
	
	void handlePacket(final IPacket packet);
	
}
