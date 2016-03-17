package de.sogomn.rat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.ImageUtils;

/*
 * WHEEE! Hardcoding!
 */
final class GUISettings {
	
	private static final Color BACKGROUND = new Color(250, 250, 255);
	private static final Color BASE = new Color(235, 205, 185);
	private static final Color BRIGHTER = new Color(245, 220, 200);
	private static final Color DARKER = new Color(215, 185, 165);
	private static final Color ALTERNATIVE = new Color(245, 235, 230);
	private static final Color SELECTION = new Color(120, 120, 135);
	
	private static final EmptyBorder TABLE_CELL_BORDER = new EmptyBorder(2, 5, 2, 5);
	
	private static final Font FONT;
	
	private static final BufferedImage[] NOTIFICATION_ICONS = new SpriteSheet("/gui_notification_icons.png", 16, 16).getSprites();
	private static final BufferedImage[] FILE_ICONS = new SpriteSheet("/gui_file_icons.png", 16, 16).getSprites();
	private static final ImageIcon ERROR_ICON = new ImageIcon(ImageUtils.scaleImage(NOTIFICATION_ICONS[0], 2));
	private static final ImageIcon INFORMATION_ICON = new ImageIcon(ImageUtils.scaleImage(NOTIFICATION_ICONS[1], 2));
	private static final ImageIcon QUESTION_ICON = new ImageIcon(ImageUtils.scaleImage(NOTIFICATION_ICONS[2], 2));
	private static final ImageIcon WARNING_ICON = new ImageIcon(ImageUtils.scaleImage(NOTIFICATION_ICONS[3], 2));
	private static final ImageIcon SAVE_ICON = new ImageIcon(FILE_ICONS[0]);
	private static final ImageIcon FOLDER_ICON = new ImageIcon(FILE_ICONS[1]);
	private static final ImageIcon FOLDER_OPEN_ICON = new ImageIcon(FILE_ICONS[2]);
	private static final ImageIcon FILE_ICON = new ImageIcon(FILE_ICONS[3]);
	private static final ImageIcon LIST_ICON = new ImageIcon(FILE_ICONS[4]);
	private static final ImageIcon DISC_ICON = new ImageIcon(FILE_ICONS[5]);
	private static final ImageIcon HOME_ICON = new ImageIcon(FILE_ICONS[6]);
	private static final ImageIcon NEW_FOLDER_ICON = new ImageIcon(FILE_ICONS[7]);
	private static final ImageIcon UP_FOLDER_ICON = new ImageIcon(FILE_ICONS[8]);
	private static final ImageIcon DETAILS_ICON = new ImageIcon(FILE_ICONS[9]);
	
	private static final Painter<?> BASE_PAINTER = (g, object, width, height) -> {
		g.setColor(BASE);
		g.fillRect(0, 0, width, height);
	};
	
	private static final Painter<?> BRIGHTER_PAINTER = (g, object, width, height) -> {
		g.setColor(BRIGHTER);
		g.fillRect(0, 0, width, height);
	};
	
	private static final Painter<?> DARKER_PAINTER = (g, object, width, height) -> {
		g.setColor(DARKER);
		g.fillRect(0, 0, width, height);
	};
	
	private static final Painter<?> SELECTION_PAINTER = (g, object, width, height) -> {
		g.setColor(SELECTION);
		g.fillRect(0, 0, width, height);
	};
	
	static {
		Font newFont;
		
		try {
			newFont = Font.createFont(Font.TRUETYPE_FONT, Ratty.class.getResourceAsStream("/lato.ttf")).deriveFont(13f);
		} catch (final IOException | FontFormatException ex) {
			ex.printStackTrace();
			
			newFont = new Font("Trebuchet MS", Font.PLAIN, 13);
		}
		
		FONT = newFont;
	}
	
	private GUISettings() {
		//...
	}
	
	public static void setDefaults(final UIDefaults defaults) {
		defaults.put("nimbusBase", BASE);
		defaults.put("control", BACKGROUND);
		defaults.put("MenuBar[Enabled].backgroundPainter", BASE_PAINTER);
		
		defaults.put("Button[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("Button[Default].backgroundPainter", BASE_PAINTER);
		defaults.put("Button[Focused].backgroundPainter", BASE_PAINTER);
		defaults.put("Button[Default+Focused].backgroundPainter", BASE_PAINTER);
		defaults.put("Button[MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("Button[Default+MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("Button[Focused+MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("Button[Default+Focused+MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("Button[Pressed].backgroundPainter", DARKER_PAINTER);
		defaults.put("Button[Default+Pressed].backgroundPainter", DARKER_PAINTER);
		defaults.put("Button[Focused+Pressed].backgroundPainter", DARKER_PAINTER);
		defaults.put("Button[Default+Focused+Pressed].backgroundPainter", DARKER_PAINTER);
		
		defaults.put("ToggleButton[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("ToggleButton[Focused].backgroundPainter", BASE_PAINTER);
		defaults.put("ToggleButton[MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Focused+MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Pressed].backgroundPainter", BASE_PAINTER);
		defaults.put("ToggleButton[Focused+Pressed].backgroundPainter", BASE_PAINTER);
		defaults.put("ToggleButton[Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[Focused+Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[MouseOver+Selected].backgroundPainter", BASE_PAINTER);
		defaults.put("ToggleButton[Focused+MouseOver+Selected].backgroundPainter", BASE_PAINTER);
		defaults.put("ToggleButton[Pressed+Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[Focused+Pressed+Selected].backgroundPainter", DARKER_PAINTER);
		
		defaults.put("Table.background", new ColorUIResource(ALTERNATIVE));
		defaults.put("Table:\"Table.cellRenderer\".background", ALTERNATIVE);
		defaults.put("Table.alternateRowColor", ALTERNATIVE);
		defaults.put("Table[Enabled+Selected].textBackground", SELECTION);
		defaults.put("Table.focusCellHighlightBorder", TABLE_CELL_BORDER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", BASE_PAINTER);
		
//		defaults.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", DARKER_PAINTER);
//		defaults.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", BASE_PAINTER);
//		defaults.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", BRIGHTER_PAINTER);
//		defaults.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", BASE_PAINTER);
//		defaults.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", BASE_PAINTER);
//		defaults.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", BRIGHTER_PAINTER);
//		defaults.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", BASE_PAINTER);
		
		defaults.put("PopupMenu[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("MenuItem[MouseOver].backgroundPainter", SELECTION_PAINTER);
		
		defaults.put("Tree:TreeCell[Enabled+Selected].backgroundPainter", SELECTION_PAINTER);
		defaults.put("Tree:TreeCell[Focused+Selected].backgroundPainter", SELECTION_PAINTER);
		
		defaults.put("ComboBox:\"ComboBox.listRenderer\"[Selected].background", SELECTION);
		defaults.put("ComboBox:\"ComboBox.renderer\"[Selected].background", SELECTION);
		defaults.put("ComboBox[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("ComboBox[Focused].backgroundPainter", BASE_PAINTER);
		defaults.put("ComboBox[Enabled+Selected].backgroundPainter", BASE_PAINTER);
		defaults.put("ComboBox[MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ComboBox[Focused+MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ComboBox[Pressed].backgroundPainter", DARKER_PAINTER);
		defaults.put("ComboBox[Focused+Pressed].backgroundPainter", DARKER_PAINTER);
		
		defaults.put("Button.font", FONT);
		defaults.put("Table.font", FONT);
		defaults.put("Label.font", FONT);
		defaults.put("TableHeader.font", FONT);
		defaults.put("FileChooser.font", FONT);
		defaults.put("TextField.font", FONT);
		defaults.put("FormattedTextField.font", FONT);
		defaults.put("PopupMenu.font", FONT);
		defaults.put("Menu.font", FONT);
		defaults.put("MenuItem.font", FONT);
		defaults.put("Panel.font", FONT);
		defaults.put("Tree.font", FONT);
		defaults.put("ToggleButton.font", FONT);
		defaults.put("List.font", FONT);
		defaults.put("OptionPane.font", FONT);
		defaults.put("ComboBox.font", FONT);
		
		defaults.put("OptionPane.errorIcon", ERROR_ICON);
		defaults.put("OptionPane.informationIcon", INFORMATION_ICON);
		defaults.put("OptionPane.questionIcon", QUESTION_ICON);
		defaults.put("OptionPane.warningIcon", WARNING_ICON);
		defaults.put("FileChooser.directoryIcon", FOLDER_ICON);
		defaults.put("FileChooser.floppyDriveIcon", SAVE_ICON);
		defaults.put("FileChooser.homeFolderIcon", HOME_ICON);
		defaults.put("FileChooser.listViewIcon", LIST_ICON);
		defaults.put("FileChooser.newFolderIcon", NEW_FOLDER_ICON);
		defaults.put("FileChooser.upFolderIcon", UP_FOLDER_ICON);
		defaults.put("FileChooser.hardDriveIcon", DISC_ICON);
		defaults.put("FileChooser.detailsViewIcon", DETAILS_ICON);
		defaults.put("Tree.closedIcon", FOLDER_ICON);
		defaults.put("Tree.openIcon", FOLDER_OPEN_ICON);
		defaults.put("Tree.leafIcon", FILE_ICON);
	}
	
}
