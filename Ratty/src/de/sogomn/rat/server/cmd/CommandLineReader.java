package de.sogomn.rat.server.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import de.sogomn.engine.util.AbstractListenerContainer;

public final class CommandLineReader extends AbstractListenerContainer<ICommandLineListener> {
	
	private BufferedReader reader;
	private Thread thread;
	
	private static final String ARGUMENT_SEPARATOR = " ";
	
	public CommandLineReader() {
		final InputStreamReader inReader = new InputStreamReader(System.in);
		
		reader = new BufferedReader(inReader);
	}
	
	private String readLine() {
		try {
			final String line = reader.readLine();
			
			return line;
		} catch (final IOException ex) {
			return null;
		}
	}
	
	public void start() {
		final Runnable runnable = () -> {
			while (true) {
				final String line = readLine();
				
				if (line != null) {
					final Command command = Command.parse(line);
					
					notifyListeners(listener -> listener.commandInput(command));
				}
			}
		};
		
		thread = new Thread(runnable);
		thread.start();
	}
	
	public void stop() {
		thread.interrupt();
		thread = null;
	}
	
	public static final class Command {
		
		private final String command;
		private final String[] arguments;
		
		public static final Command EMPTY = new Command("");
		
		public Command(final String command, final String... arguments) {
			this.command = command;
			this.arguments = arguments;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null || getClass() != obj.getClass() || command == null) {
				return false;
			}
			
			final Command other = (Command)obj;
			final boolean equals = command.equalsIgnoreCase(other.command);
			
			return equals;
		}
		
		public boolean equals(final String string) {
			if (command == null || string == null) {
				return false;
			}
			
			return command.equalsIgnoreCase(string);
		}
		
		@Override
		public String toString() {
			return command;
		}
		
		public String argument(final int index) {
			if (index < 0 || index > arguments.length - 1) {
				return null;
			}
			
			return arguments[index];
		}
		
		public static Command parse(final String line) {
			final String[] parts = line.split(ARGUMENT_SEPARATOR);
			
			if (parts.length == 0) {
				return EMPTY;
			}
			
			final String commandString = parts[0];
			final String[] arguments = Stream
					.of(parts)
					.skip(1)
					.filter(part -> part != null && !part.isEmpty())
					.toArray(String[]::new);
			final Command command = new Command(commandString, arguments);
			
			return command;
		}
		
	}
	
}
