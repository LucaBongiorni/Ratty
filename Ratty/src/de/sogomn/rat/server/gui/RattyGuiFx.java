package de.sogomn.rat.server.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import de.sogomn.rat.Ratty;

public final class RattyGuiFx extends Application {
	
	@FXML
	private BorderPane pane;
	
	@FXML
	private TableView<ServerClient> table;
	
	@FXML
	private HBox bottom;
	
	@FXML
	private Button builder;
	
	private static final String TITLE = "Ratty";
	private static final String CSS_PATH = "/test.css";
	private static final String FXML_PATH = "/gui.fxml";
	private static final String LANGUAGE_BASE = "language.language";
	
	public RattyGuiFx() {
		//...
	}
	
	private Parent loadContent(final String path, final ResourceBundle bundle) {
		final FXMLLoader loader = new FXMLLoader();
		
		loader.setResources(bundle);
		
		try {
			final InputStream in = Ratty.class.getResourceAsStream(path);
			final Parent content = loader.load(in);
			
			return content;
		} catch (final IOException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		final ResourceBundle bundle = ResourceBundle.getBundle(LANGUAGE_BASE, Locale.ENGLISH);
		final Parent content = loadContent(FXML_PATH, bundle);
		final Scene scene = new Scene(content);
		final ObservableList<String> styleSheets = scene.getStylesheets();
		
		styleSheets.add(CSS_PATH);
		
		primaryStage.setTitle(TITLE);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		System.out.println(builder);
	}
	
	public static void main(final String[] args) {
		RattyGuiFx.launch(args);
	}
	
}
