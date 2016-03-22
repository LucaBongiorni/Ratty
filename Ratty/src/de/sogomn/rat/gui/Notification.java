package de.sogomn.rat.gui;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public final class Notification {
	
	private JDialog dialog;
	private JLabel label;
	
	private static final EmptyBorder PADDING = new EmptyBorder(10, 50, 10, 50);
	private static final int GAP = 30;
	private static final int INTERVAL = 3;
	private static final int WAIT_TIME = 3000;
	
	public Notification(final String text, final Icon icon) {
		dialog = new JDialog();
		label = new JLabel(text);
		
		label.setIcon(icon);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		label.setBorder(PADDING);
		label.setIconTextGap(GAP);
		
		dialog.setUndecorated(true);
		dialog.setContentPane(label);
		dialog.pack();
		dialog.setLocation(-dialog.getWidth(), 0);
		dialog.setAlwaysOnTop(true);
	}
	
	public Notification(final String text) {
		this(text, null);
	}
	
	public Notification() {
		this("");
	}
	
	public void trigger() {
		if (dialog.isVisible() || !dialog.isDisplayable()) {
			return;
		}
		
		final Runnable runnable = () -> {
			try {
				while (dialog.getX() < 0) {
					final int x = dialog.getX() + 1;
					final int y = dialog.getY();
					
					dialog.setLocation(x, y);
					
					Thread.sleep(INTERVAL);
				}
				
				Thread.sleep(WAIT_TIME);
				
				while (dialog.getX() > -dialog.getWidth()) {
					final int x = dialog.getX() - 1;
					final int y = dialog.getY();
					
					dialog.setLocation(x, y);
					
					Thread.sleep(INTERVAL);
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			
			dialog.setVisible(false);
			dialog.dispose();
		};
		final Thread thread = new Thread(runnable);
		
		dialog.setVisible(true);
		
		thread.setDaemon(true);
		thread.start();
	}
	
}
