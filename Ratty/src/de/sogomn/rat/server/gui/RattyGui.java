package de.sogomn.rat.server.gui;

import static de.sogomn.rat.Ratty.LANGUAGE;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.engine.util.ImageUtils;

final class RattyGui extends AbstractListenerContainer<IGuiController> {
	
	private JFrame frame;
	
	private JTable table;
	private ServerClientTableModel tableModel;
	private JScrollPane scrollPane;
	
	private JPopupMenu menu;
	private JMenuBar menuBar;
	private JButton build, attack;
	
	private JFileChooser fileChooser;
	
	private ServerClient lastServerClientClicked;
	
	private static final Dimension SIZE = new Dimension(800, 600);
	
	private static final BufferedImage GUI_ICON_SMALL = ImageUtils.loadImage("/gui_icon.png");
	private static final BufferedImage GUI_ICON_MEDIUM = ImageUtils.scaleImage(GUI_ICON_SMALL, 64, 64);
	private static final BufferedImage GUI_ICON_LARGE = ImageUtils.scaleImage(GUI_ICON_SMALL, 128, 128);
	private static final BufferedImage GUI_ICON_HUGE = ImageUtils.scaleImage(GUI_ICON_SMALL, 256, 256);
	private static final List<BufferedImage> GUI_ICONS = Arrays.asList(GUI_ICON_SMALL, GUI_ICON_MEDIUM, GUI_ICON_LARGE, GUI_ICON_HUGE);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet(ImageUtils.scaleImage(ImageUtils.loadImage("/gui_menu_icons.png"), 2), 16 * 2, 16 * 2).getSprites();
	
	public static final String POPUP = LANGUAGE.getString("action.popup");
	public static final String SCREENSHOT = LANGUAGE.getString("action.screenshot");
	public static final String DESKTOP = LANGUAGE.getString("action.desktop");
	public static final String VOICE = LANGUAGE.getString("action.voice");
	public static final String FILES = LANGUAGE.getString("action.files");
	public static final String COMMAND = LANGUAGE.getString("action.command");
	public static final String CLIPBOARD = LANGUAGE.getString("action.clipboard");
	public static final String WEBSITE = LANGUAGE.getString("action.website");
	public static final String AUDIO = LANGUAGE.getString("action.audio");
	public static final String FREE = LANGUAGE.getString("action.free");
	public static final String BUILD = LANGUAGE.getString("action.build");
	public static final String ATTACK = LANGUAGE.getString("action.attack");
	
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
		attack = new JButton(ATTACK);
		fileChooser = new JFileChooser();
		
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
		
		attack.setActionCommand(ATTACK);
		attack.addActionListener(this::actionPerformed);
		build.setActionCommand(BUILD);
		build.addActionListener(this::actionPerformed);
		menuBar.add(build);
		menuBar.add(attack);
		scrollPane.setBorder(null);
		table.setComponentPopupMenu(menu);
		table.addMouseListener(mouseAdapter);
		table.setModel(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowHorizontalLines(true);
		
		container.add(scrollPane, BorderLayout.CENTER);
		container.add(menuBar, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(SIZE);
		frame.pack();
		frame.setLocationRelativeTo(null);
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
	
	public void update() {
		tableModel.fireTableDataChanged();
	}
	
	public void addRow(final ServerClient client) {
		tableModel.addServerClient(client);
	}
	
	public void removeRow(final ServerClient client) {
		tableModel.removeServerClient(client);
	}
	
	public int showWarning(final String message, final String... options) {
		final int input = JOptionPane.showOptionDialog(frame, message, null, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);
		
		return input;
	}
	
	public void showMessage(final String message) {
		final JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
		final JDialog dialog = pane.createDialog(frame, null);
		
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public File getFile(final String type) {
		final FileFilter filter;
		
		if (type != null) {
			filter = new FileNameExtensionFilter("*." + type, type);
		} else {
			filter = null;
		}
		
		fileChooser.setFileFilter(filter);
		
		final int input = fileChooser.showOpenDialog(frame);
		
		if (input == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			
			return file;
		}
		
		return null;
	}
	
	public File getFile() {
		return getFile(null);
	}
	
	public String getInput() {
		final String input = JOptionPane.showInputDialog(frame, null);
		
		return input;
	}
	
	public ServerClient getLastServerClientClicked() {
		return lastServerClientClicked;
	}
	
}
