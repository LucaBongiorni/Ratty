package de.sogomn.rat.server.cmd;

import de.sogomn.rat.server.cmd.CommandLineReader.Command;

public interface ICommandLineListener {
	
	void commandInput(final Command command);
	
}
