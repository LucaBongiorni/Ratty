package de.sogomn.rat;

public interface IRecorderListener {
	
	void done(final VoiceRecorder source, final byte[] data);
	
}
