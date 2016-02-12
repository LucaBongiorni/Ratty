package de.sogomn.rat.server.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import de.sogomn.rat.Ratty;

public final class RattyGuiFx extends Application {
	
	private Stage stage;
	
	@FXML
	private TableView<ServerClient> table;
	@FXML
	private TableColumn<ServerClient, String> name;
	@FXML
	private TableColumn<ServerClient, String> address;
	@FXML
	private TableColumn<ServerClient, String> os;
	@FXML
	private TableColumn<ServerClient, String> version;
	@FXML
	private TableColumn<ServerClient, Boolean> desktop;
	@FXML
	private TableColumn<ServerClient, Boolean> voice;
	@FXML
	private HBox bottom;
	@FXML
	private Button builder;
	
	private static final String TITLE = "Ratty";
	private static final String FXML_PATH = "/main_gui.fxml";
	private static final String CSS_PATH = "/main_gui.css";
	private static final String LANGUAGE_BASE = "language.language";
	
	private static final Image ICON = new Image("/gui_icon.png");
	
	private static final String NAME = "name";
	private static final String ADDRESS = "address";
	private static final String OS = "os";
	private static final String VERSION = "version";
	private static final String DESKTOP = "desktop";
	private static final String VOICE = "voice";
	
	public RattyGuiFx() {
		//...
	}
	
	private Parent loadContent(final String path, Locale locale) {
		final ResourceBundle bundle = ResourceBundle.getBundle(LANGUAGE_BASE, locale);
		
		try {
			final URL url = Ratty.class.getResource(path);
			final Parent content = FXMLLoader.load(url, bundle);
			
			return content;
		} catch (final IOException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	@FXML
	private void initialize() {
		final PropertyValueFactory<ServerClient, String> nameFactory = new PropertyValueFactory<ServerClient, String>(NAME);
		final PropertyValueFactory<ServerClient, String> addressFactory = new PropertyValueFactory<ServerClient, String>(ADDRESS);
		final PropertyValueFactory<ServerClient, String> osFactory = new PropertyValueFactory<ServerClient, String>(OS);
		final PropertyValueFactory<ServerClient, String> versionFactory = new PropertyValueFactory<ServerClient, String>(VERSION);
		final PropertyValueFactory<ServerClient, Boolean> desktopFactory = new PropertyValueFactory<ServerClient, Boolean>(DESKTOP);
		final PropertyValueFactory<ServerClient, Boolean> voiceFactory = new PropertyValueFactory<ServerClient, Boolean>(VOICE);
		
		name.setCellValueFactory(nameFactory);
		address.setCellValueFactory(addressFactory);
		os.setCellValueFactory(osFactory);
		version.setCellValueFactory(versionFactory);
		desktop.setCellValueFactory(desktopFactory);
		voice.setCellValueFactory(voiceFactory);
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		final Parent root = loadContent(FXML_PATH, Locale.ENGLISH);
		final Scene scene = new Scene(root);
		final ObservableList<String> styleSheets = scene.getStylesheets();
		final ObservableList<Image> icons = primaryStage.getIcons();
		
		icons.addAll(ICON);
		styleSheets.add(CSS_PATH);
		
		primaryStage.setTitle(TITLE);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		stage = primaryStage;
	}
	
	public void changeLanguage(final Locale locale) {
		if (stage == null) {
			return;
		}
		
		final Parent root = loadContent(FXML_PATH, locale);
		final Scene scene = stage.getScene();
		
		scene.setRoot(root);
	}
	
	public void addRow(final ServerClient serverClient) {
		final ObservableList<ServerClient> items = table.getItems();
		
		items.add(serverClient);
	}
	
	public void removeRow(final ServerClient serverClient) {
		final ObservableList<ServerClient> items = table.getItems();
		
		items.remove(serverClient);
	}
	
	public static void main(final String[] args) {
		RattyGuiFx.launch(args);
	}
	
}
