package de.sogomn.rat.server.gui;

public interface IGuiController {
	
	void userInput(final String actionCommand);
	
	void keyboardInput(final int key, final boolean flag);
	
	void mouseInput(final int x, final int y, final int button, final boolean flag);
	
}
