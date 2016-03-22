package de.sogomn.rat.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.engine.util.ImageUtils;

public final class ChatWindow extends AbstractListenerContainer<IGuiController> {
	
	private JFrame frame;
	private JTextArea chat;
	private JTextField submit;
	private JScrollPane scrollPane;
	
	private String message;
	
	private static final Dimension SIZE = new Dimension(500, 500);
	private static final String USER_PREFIX = "You: ";
	
	public static final String MESSAGE_SENT = "Message sent";
	
	public ChatWindow() {
		frame = new JFrame();
		chat = new JTextArea();
		submit = new JTextField();
		scrollPane = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		final Container contentPane = frame.getContentPane();
		final DefaultCaret caret = (DefaultCaret)chat.getCaret();
		
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		submit.addActionListener(this::messageSubmitted);
		chat.setEditable(false);
		chat.setLineWrap(true);
		chat.setWrapStyleWord(true);
		
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(submit, BorderLayout.SOUTH);
		
		frame.setIconImage(ImageUtils.EMPTY_IMAGE);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setPreferredSize(SIZE);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	private void messageSubmitted(final ActionEvent a) {
		final String message = submit.getText();
		
		if (!message.isEmpty()) {
			this.message = message;
			
			notifyListeners(controller -> controller.userInput(MESSAGE_SENT, this));
			
			addLine(USER_PREFIX + message);
		}
		
		submit.setText("");
	}
	
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void addLine(final String line) {
		chat.append(line + "\r\n");
	}
	
	public void setVisible(final boolean visible) {
		frame.setVisible(visible);
	}
	
	public void setTitle(final String title) {
		frame.setTitle(title);
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isVisible() {
		return frame.isVisible();
	}
	
}
