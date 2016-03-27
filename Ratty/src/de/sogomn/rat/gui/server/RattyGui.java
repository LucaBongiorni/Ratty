package de.sogomn.rat.gui.server;

import static de.sogomn.rat.Ratty.LANGUAGE;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.engine.util.ImageUtils;
import de.sogomn.rat.Ratty;
import de.sogomn.rat.gui.IGuiController;

public final class RattyGui extends AbstractListenerContainer<IGuiController> implements IRattyGui {
	
	private JFrame frame;
	
	private JTable table;
	private ServerClientTableModel tableModel;
	private JScrollPane scrollPane;
	private JPopupMenu menu;
	private JMenuBar menuBar;
	private JButton build, attack;
	private JFileChooser fileChooser;
	private ServerClient clientClicked;
	
	private static final String TITLE = "Ratty " + Ratty.VERSION;
	private static final Dimension SIZE = new Dimension(1150, 600);
	
	private static final BufferedImage GUI_ICON_SMALL = ImageUtils.loadImage("/gui_icon.png");
	private static final BufferedImage GUI_ICON_MEDIUM = ImageUtils.scaleImage(GUI_ICON_SMALL, 64, 64);
	private static final BufferedImage GUI_ICON_LARGE = ImageUtils.scaleImage(GUI_ICON_SMALL, 128, 128);
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet(ImageUtils.scaleImage(ImageUtils.loadImage("/gui_menu_icons.png"), 2), 16 * 2, 16 * 2).getSprites();
	private static final SpriteSheet CATEGORY_SHEET = new SpriteSheet(ImageUtils.scaleImage(ImageUtils.loadImage("/gui_category_icons.png"), 2), 16 * 2, 16 * 2);
	private static final BufferedImage SURVEILLANCE_ICON = CATEGORY_SHEET.getSprite(0);
	private static final BufferedImage FILE_MANAGEMENT_ICON = CATEGORY_SHEET.getSprite(1);
	private static final BufferedImage UTILITY_ICON = CATEGORY_SHEET.getSprite(2);
	private static final BufferedImage OTHER_ICON = CATEGORY_SHEET.getSprite(3);
	private static final List<BufferedImage> GUI_ICONS = Arrays.asList(GUI_ICON_SMALL, GUI_ICON_MEDIUM, GUI_ICON_LARGE);
	
	private static final String SURVEILLANCE = LANGUAGE.getString("menu.surveillance");
	private static final String FILE_MANAGEMENT = LANGUAGE.getString("menu.file_management");
	private static final String UTILITY = LANGUAGE.getString("menu.utility");
	private static final String OTHER = LANGUAGE.getString("menu.other");
	private static final LinkedHashMap<String, BufferedImage> FILE_MANAGEMENT_ITEM_DATA = new LinkedHashMap<String, BufferedImage>();
	private static final LinkedHashMap<String, BufferedImage> SURVEILLANCE_ITEM_DATA = new LinkedHashMap<String, BufferedImage>();
	private static final LinkedHashMap<String, BufferedImage> UTILITY_ITEM_DATA = new LinkedHashMap<String, BufferedImage>();
	private static final LinkedHashMap<String, BufferedImage> OTHER_ITEM_DATA = new LinkedHashMap<String, BufferedImage>();
	
	public static final String POPUP = LANGUAGE.getString("action.popup");
	public static final String SCREENSHOT = LANGUAGE.getString("action.screenshot");
	public static final String DESKTOP = LANGUAGE.getString("action.desktop");
	public static final String VOICE = LANGUAGE.getString("action.voice");
	public static final String FILES = LANGUAGE.getString("action.files");
	public static final String COMMAND = LANGUAGE.getString("action.command");
	public static final String CLIPBOARD = LANGUAGE.getString("action.clipboard");
	public static final String WEBSITE = LANGUAGE.getString("action.website");
	public static final String AUDIO = LANGUAGE.getString("action.audio");
	public static final String UPLOAD_EXECUTE = LANGUAGE.getString("action.upload_execute");
	public static final String FREE = LANGUAGE.getString("action.free");
	public static final String BUILD = LANGUAGE.getString("action.build");
	public static final String ATTACK = LANGUAGE.getString("action.attack");
	public static final String DROP_EXECUTE = LANGUAGE.getString("action.drop_execute");
	public static final String CHAT = LANGUAGE.getString("action.chat");
	public static final String INFORMATION = LANGUAGE.getString("action.information");
	public static final String CLOSE = "Close";
	
	static {
		SURVEILLANCE_ITEM_DATA.put(SCREENSHOT, MENU_ICONS[1]);
		SURVEILLANCE_ITEM_DATA.put(DESKTOP, MENU_ICONS[2]);
		SURVEILLANCE_ITEM_DATA.put(VOICE, MENU_ICONS[3]);
		SURVEILLANCE_ITEM_DATA.put(CLIPBOARD, MENU_ICONS[6]);
		FILE_MANAGEMENT_ITEM_DATA.put(FILES, MENU_ICONS[4]);
		FILE_MANAGEMENT_ITEM_DATA.put(UPLOAD_EXECUTE, MENU_ICONS[9]);
		FILE_MANAGEMENT_ITEM_DATA.put(DROP_EXECUTE, MENU_ICONS[11]);
		UTILITY_ITEM_DATA.put(POPUP, MENU_ICONS[0]);
		UTILITY_ITEM_DATA.put(COMMAND, MENU_ICONS[5]);
		UTILITY_ITEM_DATA.put(WEBSITE, MENU_ICONS[8]);
		UTILITY_ITEM_DATA.put(AUDIO, MENU_ICONS[7]);
		UTILITY_ITEM_DATA.put(CHAT, MENU_ICONS[12]);
		OTHER_ITEM_DATA.put(INFORMATION, MENU_ICONS[13]);
		OTHER_ITEM_DATA.put(FREE, MENU_ICONS[10]);
	}
	
	public RattyGui() {
		frame = new JFrame(TITLE);
		table = new JTable();
		tableModel = new ServerClientTableModel();
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		menu = new JPopupMenu();
		menuBar = new JMenuBar();
		build = new JButton(BUILD);
		attack = new JButton(ATTACK);
		fileChooser = new JFileChooser();
		
		final Container contentPane = frame.getContentPane();
		final MouseAdapter tableMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent m) {
				final Point mousePoint = m.getPoint();
				final int rowIndex = table.rowAtPoint(mousePoint);
				
				clientClicked = tableModel.getServerClient(rowIndex);
				
				table.setRowSelectionInterval(rowIndex, rowIndex);
			}
		};
		final String currentPath = System.getProperty("user.dir");
		final File currentDirectory = new File(currentPath);
		final JMenu surveillance = createMenu(SURVEILLANCE, SURVEILLANCE_ICON, SURVEILLANCE_ITEM_DATA);
		final JMenu fileManagement = createMenu(FILE_MANAGEMENT, FILE_MANAGEMENT_ICON, FILE_MANAGEMENT_ITEM_DATA);
		final JMenu utility = createMenu(UTILITY, UTILITY_ICON, UTILITY_ITEM_DATA);
		final JMenu other = createMenu(OTHER, OTHER_ICON, OTHER_ITEM_DATA);
		final JTableHeader tableHeader = table.getTableHeader();
		final WindowAdapter closingAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent w) {
				close();
			}
		};
		
		tableHeader.setReorderingAllowed(false);
		
		attack.setActionCommand(ATTACK);
		attack.addActionListener(this::actionPerformed);
		build.setActionCommand(BUILD);
		build.addActionListener(this::actionPerformed);
		menuBar.add(build);
		menuBar.add(attack);
		menu.add(surveillance);
		menu.add(fileManagement);
		menu.add(utility);
		menu.addSeparator();
		menu.add(other);
		scrollPane.setBorder(null);
		table.setComponentPopupMenu(menu);
		table.addMouseListener(tableMouseAdapter);
		table.setModel(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(true);
		fileChooser.setCurrentDirectory(currentDirectory);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(menuBar, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(closingAdapter);
		frame.setPreferredSize(SIZE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setIconImages(GUI_ICONS);
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	private JMenu createMenu(final String name, final BufferedImage image, final Map<String, BufferedImage> data) {
		final JMenu menu = new JMenu(name);
		final ImageIcon icon = new ImageIcon(image);
		final Set<String> keySet = data.keySet();
		
		menu.setIcon(icon);
		
		for (final String key : keySet) {
			final BufferedImage itemImage = data.get(key);
			final JMenuItem item = createMenuItem(key, itemImage);
			
			menu.add(item);
		}
		
		return menu;
	}
	
	private JMenuItem createMenuItem(final String name, final BufferedImage image) {
		final JMenuItem item = new JMenuItem(name);
		final ImageIcon icon = new ImageIcon(image);
		
		item.setActionCommand(name);
		item.addActionListener(this::actionPerformed);
		item.setIcon(icon);
		
		return item;
	}
	
	private void actionPerformed(final ActionEvent a) {
		final String command = a.getActionCommand();
		
		notifyListeners(controller -> controller.userInput(command, clientClicked));
	}
	
	public void close() {
		frame.setVisible(false);
		frame.dispose();
		
		notifyListeners(controller -> controller.userInput(CLOSE, clientClicked));
	}
	
	@Override
	public void update() {
		final int selectedRow = table.getSelectedRow();
		
		tableModel.fireTableDataChanged();
		
		if (selectedRow != -1) {
			table.setRowSelectionInterval(selectedRow, selectedRow);
		}
	}
	
	@Override
	public void addClient(final ServerClient client) {
		tableModel.addServerClient(client);
	}
	
	@Override
	public void removeClient(final ServerClient client) {
		tableModel.removeServerClient(client);
	}
	
	@Override
	public boolean showWarning(final String message, final String yes, final String no) {
		final String[] options = {yes, no};
		final int input = JOptionPane.showOptionDialog(frame, message, null, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);
		
		if (input == JOptionPane.YES_OPTION) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void showError(final String message) {
		JOptionPane.showMessageDialog(frame, message, null, JOptionPane.ERROR_MESSAGE, null);
	}
	
	@Override
	public void showMessage(final String message) {
		final JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
		final JDialog dialog = pane.createDialog(frame, null);
		
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	@Override
	public int showOptions(final String message, final String yes, final String no, final String cancel) {
		final String[] options = {yes, no, cancel};
		final int input = JOptionPane.showOptionDialog(frame, message, null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		
		return input;
	}
	
	@Override
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
	
	@Override
	public File getFile() {
		return getFile(null);
	}
	
	@Override
	public File getSaveFile() {
		final int input = fileChooser.showSaveDialog(frame);
		
		if (input == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			
			return file;
		}
		
		return null;
	}
	
	@Override
	public File getSaveFile(final String type) {
		File file = getSaveFile();
		
		if (file == null) {
			return null;
		}
		
		final String name = file.getName().toLowerCase();
		final String suffix = "." + type.toLowerCase();
		
		if (!name.endsWith(suffix)) {
			file = new File(file + suffix);
		}
		
		return file;
	}
	
	@Override
	public String getInput(final String message) {
		final String input = JOptionPane.showInputDialog(frame, message);
		
		return input;
	}
	
	@Override
	public String getInput() {
		return getInput(null);
	}
	
}
