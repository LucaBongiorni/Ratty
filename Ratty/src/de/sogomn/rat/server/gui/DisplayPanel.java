package de.sogomn.rat.server.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class DisplayPanel {
	
	private Screen screen;
	private BufferedImage image;
	
	private IGuiController controller;
	
	private static final int SCREEN_WIDTH = 1920 / 2;
	private static final int SCREEN_HEIGHT = 1080 / 2;
	
	public DisplayPanel() {
		//...
	}
	
	private Screen createScreen(final int width, final int height) {
		final Screen screen = new Screen(width, height);
		
		screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
		screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.setBackgroundColor(Color.BLACK);
		screen.addListener(g -> {
			g.drawImage(image, 0, 0, null);
		});
		
		return screen;
	}
	
	private void drawToScreenImage(final BufferedImage imagePart, final int x, final int y) {
		final Graphics2D g = image.createGraphics();
		
		ImageUtils.applyHighGraphics(g);
		
		g.drawImage(imagePart, x, y, null);
		g.dispose();
	}
	
	public void openScreen(final int width, final int height) {
		if (screen == null || screen.getInitialWidth() != width || screen.getInitialHeight() != height || !screen.isOpen()) {
			if (screen != null) {
				screen.close();
			}
			
			screen = createScreen(width, height);
		}
		
		screen.show();
		screen.redraw();
	}
	
	public void showImage(final BufferedImage image) {
		this.image = image;
		
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		openScreen(width, height);
	}
	
	public void showFrame(final IFrame frame, final int screenWidth, final int screenHeight) {
		if (image == null || image.getWidth() != screenWidth || image.getHeight() != screenHeight) {
			image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		}
		
		drawToScreenImage(frame.image, frame.x, frame.y);
		openScreen(screenWidth, screenHeight);
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
}
