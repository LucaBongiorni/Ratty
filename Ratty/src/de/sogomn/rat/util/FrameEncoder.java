package de.sogomn.rat.util;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.sogomn.engine.util.ImageUtils;

public final class FrameEncoder {
	
	private static final int SKIP = 5;
	
	private static final int CELLS_WIDE = 6;
	private static final int CELLS_HIGH = 6;
	private static final IFrame[] EMPTY_ARRAY = new IFrame[0];
	
	private static final int CURSOR_SIZE = 16;
	private static final Stroke CURSOR_STROKE = new BasicStroke(3);
	
	private FrameEncoder() {
		//...
	}
	
	public static BufferedImage takeScreenshot() {
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle screenRect = new Rectangle(screen);
		
		try {
			final Robot robot = new Robot();
			final BufferedImage image = robot.createScreenCapture(screenRect);
			
			return image;
		} catch (final AWTException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static BufferedImage takeScreenshotWithCursor() {
		final BufferedImage image = takeScreenshot();
		
		if (image == null) {
			return null;
		}
		
		final Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		final int mouseX = mousePoint.x - CURSOR_SIZE / 2;
		final int mouseY = mousePoint.y - CURSOR_SIZE / 2;
		
		final Graphics2D g = image.createGraphics();
		
		ImageUtils.applyHighGraphics(g);
		
		g.setStroke(CURSOR_STROKE);
		g.setColor(Color.RED);
		g.drawOval(mouseX, mouseY, CURSOR_SIZE, CURSOR_SIZE);
		g.dispose();
		
		return image;
	}
	
	public static IFrame[] getIFrames(final BufferedImage previous, final BufferedImage next) {
		final int width = previous.getWidth();
		final int height = previous.getHeight();
		
		if (next.getWidth() != width || next.getHeight() != height) {
			return EMPTY_ARRAY;
		}
		
		final int cellWidth = width / CELLS_WIDE;
		final int cellHeight = height / CELLS_HIGH;
		final ArrayList<IFrame> frames = new ArrayList<IFrame>();
		
		
		for (int x = 0; x < CELLS_WIDE; x++) {
			for (int y = 0; y < CELLS_HIGH; y++) {
				final int cellX = x * cellWidth;
				final int cellY = y * cellHeight;
				final int cellEndX = cellX + cellWidth;
				final int cellEndY = cellY + cellHeight;
				
				outer:
				for (int xx = cellX; xx < cellEndX && xx < width; xx += SKIP) {
					for (int yy = cellY; yy < cellEndY && yy < height; yy += SKIP) {
						final int previousRgb = previous.getRGB(xx, yy);
						final int nextRgb = next.getRGB(xx, yy);
						
						if (previousRgb == nextRgb) {
							continue;
						}
						
						final BufferedImage image = next.getSubimage(cellX, cellY, cellWidth, cellHeight);
						final IFrame frame = new IFrame(cellX, cellY, image);
						
						frames.add(frame);
						
						break outer;
					}
				}
				
			}
		}
		
		final IFrame[] framesArray = frames.stream().toArray(IFrame[]::new);
		
		return framesArray;
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
