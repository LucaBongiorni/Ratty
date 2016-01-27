package de.sogomn.rat.server.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	private long lastIdClicked;
	
	private JPopupMenu menu;
	
	private IGuiController controller;
	
	private static final String[] HEADERS = {
		"ID",
		"Name",
		"IP address",
		"OS",
		"Version"
	};
	
	public static final String POPUP = "Popup";
	public static final String SCREENSHOT = "Screenshot";
	public static final String KEY_EVENT = "Key event";
	public static final String FREE = "Free";
	
	public static final String[] ACTION_COMMANDS = {
		POPUP,
		SCREENSHOT,
		KEY_EVENT,
		FREE
	};
	
	public RattyGui() {
		frame = new JFrame();
		table = new JTable();
		tableModel = (DefaultTableModel)table.getModel();
		menu = new JPopupMenu();
		
		for (final String command : ACTION_COMMANDS) {
			final JMenuItem item = new JMenuItem(command);
			
			item.setActionCommand(command);
			item.addActionListener(this::menuItemClicked);
			
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
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setLocationByPlatform(true);
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
	
	public void setController(final IGuiController controller) {
		this.controller = controller;
	}
	
	public long getLastIdClicked() {
		return lastIdClicked;
	}
	
}
