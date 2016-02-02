package de.sogomn.rat.server.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.ImageUtils;

public final class RattyGui {
	
	private JFrame frame;
	
	private JTable table;
	private DefaultTableModel tableModel;
	private long lastIdClicked;
	private JScrollPane scrollPane;
	
	private JPopupMenu menu;
	
	private IGuiController controller;
	
	private static final Dimension SIZE = new Dimension(800, 400);
	
	private static final String[] HEADERS = {
		"ID",
		"Name",
		"IP address",
		"OS",
		"Version",
		"Streaming"
	};
	
	private static final BufferedImage GUI_ICON_SMALL = ImageUtils.loadImage("/gui_icon.png");
	private static final BufferedImage GUI_ICON_MEDIUM = ImageUtils.scaleImage(ImageUtils.loadImage("/gui_icon.png"), 64, 64);
	private static final BufferedImage GUI_ICON_LARGE = ImageUtils.scaleImage(ImageUtils.loadImage("/gui_icon.png"), 128, 128);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet("/menu_icons.png", 32, 32).getSprites();
	
	public static final ArrayList<BufferedImage> GUI_ICONS = new ArrayList<BufferedImage>(3);
	
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
	
	static {
		GUI_ICONS.add(GUI_ICON_SMALL);
		GUI_ICONS.add(GUI_ICON_MEDIUM);
		GUI_ICONS.add(GUI_ICON_LARGE);
	}
	
	public RattyGui() {
		final DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 365970129123372132L;
			
			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(final int columnIndex) {
				if (columnIndex == 5) {	//Column 5 = Streaming
					return Boolean.class;
				}
				
				return super.getColumnClass(columnIndex);
			}
		};
		
		frame = new JFrame();
		table = new JTable();
		tableModel = model;
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
				final int rowIndex = table.rowAtPoint(mousePoint);
				
				lastIdClicked = (Long)tableModel.getValueAt(rowIndex, 0);
			}
		};
		
		scrollPane.setBorder(null);
		tableModel.setColumnIdentifiers(HEADERS);
		table.setComponentPopupMenu(menu);
		table.addMouseListener(mouseAdapter);
		table.setModel(tableModel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(scrollPane);
		frame.setPreferredSize(SIZE);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setIconImages(GUI_ICONS);
		frame.setVisible(true);
		frame.requestFocus();
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
	
	private int getRowIndex(final long id) {
		final int rows = tableModel.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			final long rowId = (Long)tableModel.getValueAt(i, 0);
			
			if (rowId == id) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void addTableRow(final long id, final String name, final String address, final String os, final String version) {
		final Object[] data = {id, name, address, os, version, false};
		
		tableModel.addRow(data);
	}
	
	public void removeTableRow(final long id) {
		final int rowIndex = getRowIndex(id);
		
		if (rowIndex != -1) {
			tableModel.removeRow(rowIndex);
		}
	}
	
	public void setStreaming(final long id, final boolean state) {
		final int rowIndex = getRowIndex(id);
		
		if (rowIndex != -1) {
			tableModel.setValueAt(state, rowIndex, 5);	//Column 5 = Streaming
		}
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
	public long getLastIdClicked() {
		return lastIdClicked;
	}
	
}
