package de.sogomn.rat.server.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.sogomn.engine.util.AbstractListenerContainer;

public final class CommandLineReader extends AbstractListenerContainer<ICommandLineListener> {
	
	private BufferedReader reader;
	private Thread thread;
	
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
		
		private static final char ARGUMENT_SEPARATOR = ' ';
		private static final char STRING_LITERAL = '\"';
		
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
			if (line == null || line.isEmpty()) {
				return EMPTY;
			}
			
			final int length = line.length();
			final StringBuilder currentArgument = new StringBuilder();
			final ArrayList<String> arguments = new ArrayList<String>();
			
			boolean string = false;
			
			for (int i = 0; i < length; i++) {
				final char c = line.charAt(i);
				
				if (c == STRING_LITERAL) {
					string = !string;
				} else if (c == ARGUMENT_SEPARATOR && !string) {
					final String argument = currentArgument.toString();
					
					arguments.add(argument);
					currentArgument.setLength(0);
				} else  {
					currentArgument.append(c);
				}
			}
			
			final String argument = currentArgument.toString();
			
			arguments.add(argument);
			
			final String commandString = arguments.get(0);
			final String[] argumentArray = arguments
					.stream()
					.skip(1)
					.filter(arg -> arg != null && !arg.isEmpty())
					.toArray(String[]::new);
			final Command command = new Command(commandString, argumentArray);
			
			return command;
		}
		
	}
	
}
