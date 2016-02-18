package de.sogomn.rat;

import java.awt.Color;

import javax.swing.Painter;
import javax.swing.UIDefaults;

final class GUISettings {
	
	private static final Color BACKGROUND = new Color(255, 245, 245);
	
	private static final Color BASE = new Color(240, 160, 100);
	private static final Color BRIGHTER = new Color(250, 170, 110);
	private static final Color DARKER = new Color(230, 150, 90);
	
	private static final Color ALTERNATIVE = new Color(255, 240, 220);
	private static final Color ALTERNATIVE_DARKER = new Color(245, 230, 210);
	private static final Color SELECTION = new Color(205, 170, 160);
	
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
	
	private GUISettings() {
		//...
	}
	
	public static void setDefaults(final UIDefaults defaults) {
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
		
		defaults.put("ToggleButton[Enabled].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Focused].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Focused+MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Pressed].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Focused+Pressed].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ToggleButton[Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[Focused+Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[MouseOver+Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[Focused+MouseOver+Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[Pressed+Selected].backgroundPainter", DARKER_PAINTER);
		defaults.put("ToggleButton[Focused+Pressed+Selected].backgroundPainter", DARKER_PAINTER);
		
		defaults.put("Table:\"Table.cellRenderer\".background", ALTERNATIVE);
		defaults.put("Table.alternateRowColor", ALTERNATIVE_DARKER);
		defaults.put("Table[Enabled+Selected].textBackground", SELECTION);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", BASE_PAINTER);
		defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", BASE_PAINTER);
		
		defaults.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", DARKER_PAINTER);
		defaults.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", BASE_PAINTER);
		defaults.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", BRIGHTER_PAINTER);
		defaults.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", BASE_PAINTER);
		defaults.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", BRIGHTER_PAINTER);
		defaults.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", BRIGHTER_PAINTER);
	}
	
}
