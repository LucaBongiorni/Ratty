package de.sogomn.rat.server.gui;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class RattyGui {
	
	private JFrame frame;
	
	private JTable table;
	private DefaultTableModel tableModel;
	
	private JPopupMenu menu;
	
	private IGuiController controller;
	
	private static final String[] HEADERS = {
		"Name",
		"IP address",
		"OS",
		"Version"
	};
	
	public static final String POPUP = "Popup";
	public static final String SCREENSHOT = "Screenshot";
	public static final String KEY_EVENT = "Key event";
	public static final String[] ACTION_COMMANDS = {POPUP, SCREENSHOT, KEY_EVENT};
	
	public RattyGui() {
		frame = new JFrame();
		table = new JTable();
		tableModel = (DefaultTableModel)table.getModel();
		menu = new JPopupMenu();
		
		for (final String command : ACTION_COMMANDS) {
			final JMenuItem item = new JMenuItem(command);
			
			item.addActionListener(this::actionPerformed);
			menu.add(item);
		}
		
		tableModel.setColumnIdentifiers(HEADERS);
		table.setEnabled(false);
		table.setComponentPopupMenu(menu);
		
		final JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	private void actionPerformed(final ActionEvent a) {
		final String command = a.getActionCommand();
		
		controller.userInput(command);
	}
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
}
