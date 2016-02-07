package de.sogomn.rat.server.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.sogomn.engine.IMouseListener;
import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class DisplayPanel {
	
	private String title;
	private Screen screen;
	private BufferedImage image;
	
	private int lastXPos, lastYPos;
	private int lastButtonHit;
	private int lastKeyHit;
	private IGuiController controller;
	
	private static final int SCREEN_WIDTH = 1920 / 2;
	private static final int SCREEN_HEIGHT = 1080 / 2;
	
	public static final String MOUSE_PRESSED = "Mouse pressed";
	public static final String MOUSE_RELEASED = "Mouse released";
	public static final String KEY_PRESSED = "Key pressed";
	public static final String KEY_RELEASED = "Key released";
	
	public DisplayPanel() {
		//...
	}
	
	private Screen createScreen(final int width, final int height) {
		final Screen screen = new Screen(width, height);
		
		final IMouseListener mouseListener = new IMouseListener() {
			@Override
			public void mouseEvent(final int x, final int y, final int button, final boolean flag) {
				mouseEventPerformed(x, y, button, flag);
			}
			
			@Override
			public void mouseMotionEvent(final int x, final int y, final int modifiers) {
				//...
			}
			
			@Override
			public void mouseWheelEvent(final int x, final int y, final int rotation) {
				//...
			}
		};
		
		screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
		screen.setTitle(title);
		screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.setBackgroundColor(Color.BLACK);
		screen.addMouseListener(mouseListener);
		screen.addKeyboardListener(this::keyEventPerformed);
		screen.addListener(g -> {
			g.drawImage(image, 0, 0, null);
		});
		
		return screen;
	}
	
	private void mouseEventPerformed(final int x, final int y, final int button, final boolean flag) {
		lastXPos = x;
		lastYPos = y;
		
		if (button == MouseEvent.BUTTON1) {
			lastButtonHit = MouseEvent.BUTTON1_DOWN_MASK;
		} else if (button == MouseEvent.BUTTON2) {
			lastButtonHit = MouseEvent.BUTTON2_DOWN_MASK;
		} else if (button == MouseEvent.BUTTON3) {
			lastButtonHit = MouseEvent.BUTTON3_DOWN_MASK;
		} else {
			lastButtonHit = MouseEvent.NOBUTTON;
		}
		
		if (controller != null) {
			controller.userInput(flag ? MOUSE_PRESSED : MOUSE_RELEASED);
		}
	}
	
	private void keyEventPerformed(final int key, final boolean flag) {
		lastKeyHit = key;
		
		if (controller != null) {
			controller.userInput(flag ? KEY_PRESSED : KEY_RELEASED);
		}
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
	
	public void setTitle(final String title) {
		this.title = title;
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
	public int getLastXPos() {
		return lastXPos;
	}
	
	public int getLastYPos() {
		return lastYPos;
	}
	
	public int getLastButtonHit() {
		return lastButtonHit;
	}
	
	public int getLastKeyHit() {
		return lastKeyHit;
	}
	
}
