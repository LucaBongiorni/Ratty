package de.sogomn.rat.util;

import java.awt.image.BufferedImage;

public final class FrameEncoder {
	
	private static final int PIXEL_SKIPPING = 2;
	private static final int TOLERANCE = 3;
	
	private FrameEncoder() {
		//...
	}
	
	private static int getColorDifference(final int rgbOne, final int rgbTwo) {
		final int redOne = (rgbOne >> 16) & 0xff;
		final int greenOne = (rgbOne >> 8) & 0xff;
		final int blueOne = rgbOne & 0xff;
		final int redTwo = (rgbTwo >> 16) & 0xff;
		final int greenTwo = (rgbTwo >> 8) & 0xff;
		final int blueTwo = rgbTwo & 0xff;
		final int redDifference = Math.abs(redTwo - redOne);
		final int greenDifference = Math.abs(greenTwo - greenOne);
		final int blueDifference = Math.abs(blueTwo - blueOne);
		final int difference = (redDifference + greenDifference + blueDifference) / 3;
		
		return difference;
	}
	
	public static IFrame getIFrame(final BufferedImage previous, final BufferedImage next) {
		final int width = previous.getWidth();
		final int height = previous.getHeight();
		
		if (next.getWidth() != width || next.getHeight() != height) {
			return null;
		}
		
		int frameX = width;
		int frameY = height;
		int frameWidth = 0;
		int frameHeight = 0;
		
		for (int x = 0; x < width; x += PIXEL_SKIPPING) {
			for (int y = 0; y < height; y += PIXEL_SKIPPING) {
				final int previousRgb = previous.getRGB(x, y);
				final int nextRgb = next.getRGB(x, y);
				final int difference = getColorDifference(previousRgb, nextRgb);
				
				if (difference <= TOLERANCE) {
					continue;
				}
				
				frameX = Math.min(frameX, x);
				frameY = Math.min(frameY, y);
				frameWidth = Math.max(frameWidth, x);
				frameHeight = Math.max(frameHeight, y);
			}
		}
		
		frameWidth -= frameX;
		frameHeight -= frameY;
		
		final BufferedImage image = next.getSubimage(frameX, frameY, frameWidth, frameHeight);
		final IFrame frame = new IFrame(frameX, frameY, image);
		
		return frame;
	}
	
	public static final class IFrame {
		
		public final int x, y;
		public final BufferedImage image;
		
		IFrame(final int x, final int y, final BufferedImage image) {
			this.x = x;
			this.y = y;
			this.image = image;
		}
		
	}
	
}
