package de.sogomn.rat.server.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.engine.util.ImageUtils;

public final class RattyGui extends AbstractListenerContainer<IGuiController> {
	
	private JFrame frame;
	
	private JTable table;
	private ServerClientTableModel tableModel;
	private JScrollPane scrollPane;
	
	private JPopupMenu menu;
	private JMenuBar menuBar;
	private JButton build;
	
	private ServerClient lastServerClientClicked;
	
	private static final Dimension SIZE = new Dimension(800, 600);
	
	private static final BufferedImage GUI_ICON_SMALL = ImageUtils.loadImage("/gui_icon.png");
	private static final BufferedImage GUI_ICON_MEDIUM = ImageUtils.scaleImage(ImageUtils.loadImage("/gui_icon.png"), 64, 64);
	private static final BufferedImage GUI_ICON_LARGE = ImageUtils.scaleImage(ImageUtils.loadImage("/gui_icon.png"), 128, 128);
	private static final List<BufferedImage> GUI_ICONS = Arrays.asList(GUI_ICON_SMALL, GUI_ICON_MEDIUM, GUI_ICON_LARGE);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet("/menu_icons.png", 32, 32).getSprites();
	
	public static final String POPUP = "Open popup";
	public static final String SCREENSHOT = "Take screenshot";
	public static final String DESKTOP = "Toggle desktop stream";
	public static final String VOICE = "Toggle audio stream";
	public static final String FILES = "Browse files";
	public static final String COMMAND = "Execute command";
	public static final String CLIPBOARD = "Get clipboard content";
	public static final String WEBSITE = "Open website";
	public static final String AUDIO = "Play audio";
	public static final String FREE = "Free client";
	public static final String BUILD = "Client builder";
	
	public static final String[] COMMANDS = {
		POPUP,
		SCREENSHOT,
		DESKTOP,
		VOICE,
		FILES,
		COMMAND,
		CLIPBOARD,
		AUDIO,
		WEBSITE,
		FREE
	};
	
	public RattyGui() {
		frame = new JFrame();
		table = new JTable();
		tableModel = new ServerClientTableModel();
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		menu = new JPopupMenu();
		menuBar = new JMenuBar();
		build = new JButton(BUILD);
		
		for (int i = 0; i < COMMANDS.length && i < MENU_ICONS.length; i++) {
			final String command = COMMANDS[i];
			final BufferedImage image = MENU_ICONS[i];
			
			addMenuItem(command, image);
		}
		
		final Container container = frame.getContentPane();
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent m) {
				final Point mousePoint = m.getPoint();
				final int rowIndex = table.rowAtPoint(mousePoint);
				
				lastServerClientClicked = tableModel.getServerClient(rowIndex);
			}
		};
		
		build.setActionCommand(BUILD);
		build.addActionListener(this::actionPerformed);
		menuBar.add(build);
		scrollPane.setBorder(null);
		table.setComponentPopupMenu(menu);
		table.addMouseListener(mouseAdapter);
		table.setModel(tableModel);
		
		container.add(scrollPane, BorderLayout.CENTER);
		container.add(menuBar, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(SIZE);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setIconImages(GUI_ICONS);
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	private void addMenuItem(final String name, final BufferedImage image) {
		final JMenuItem item = new JMenuItem(name);
		final ImageIcon icon = new ImageIcon(image);
		
		item.setActionCommand(name);
		item.addActionListener(this::actionPerformed);
		item.setIcon(icon);
		
		menu.add(item);
	}
	
	private void actionPerformed(final ActionEvent a) {
		final String command = a.getActionCommand();
		
		notifyListeners(controller -> controller.userInput(command));
	}
	
	public void updateTable() {
		tableModel.fireTableDataChanged();
	}
	
	public void addRow(final ServerClient client) {
		tableModel.addServerClient(client);
	}
	
	public void removeRow(final ServerClient client) {
		tableModel.removeServerClient(client);
	}
	
	public ServerClient getLastServerClientClicked() {
		return lastServerClientClicked;
	}
	
}
