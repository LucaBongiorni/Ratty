package de.sogomn.rat.util;

import java.awt.image.BufferedImage;

import de.sogomn.engine.util.ImageUtils;

public final class FrameEncoder {
	
	private FrameEncoder() {
		//...
	}
	
	public static IFrame getIFrame(final BufferedImage previous, final BufferedImage next) {
		final int width = previous.getWidth();
		final int height = previous.getHeight();
		
		if (next.getWidth() != width || next.getHeight() != height) {
			return IFrame.EMPTY;
		}
		
		int frameX = Integer.MAX_VALUE;
		int frameY = Integer.MAX_VALUE;
		int frameWidth = 0;
		int frameHeight = 0;
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final int previousRgb = previous.getRGB(x, y);
				final int nextRgb = next.getRGB(x, y);
				
				if (previousRgb == nextRgb) {
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
		
		if (frameX >= width || frameY >= height || frameWidth <= 0 || frameHeight <= 0) {
			return IFrame.EMPTY;
		}
		
		final BufferedImage image = next.getSubimage(frameX, frameY, frameWidth, frameHeight);
		final IFrame frame = new IFrame(frameX, frameY, image);
		
		return frame;
	}
	
	public static final class IFrame {
		
		public final int x, y;
		public final BufferedImage image;
		
		public static final IFrame EMPTY = new IFrame(0, 0, ImageUtils.EMPTY_IMAGE);
		
		public IFrame(final int x, final int y, final BufferedImage image) {
			this.x = x;
			this.y = y;
			this.image = image;
		}
		
	}
	
}
