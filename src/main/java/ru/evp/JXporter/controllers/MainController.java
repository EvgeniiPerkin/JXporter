package ru.evp.JXporter.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import ru.evp.JXporter.DependencyInjector;

public class MainController {
	@FXML private StackPane contentArea;
	private final DependencyInjector injector;

	public MainController(DependencyInjector injector) {
	    this.injector = injector;
	}
	
	@FXML
	private void loadWorkspace() {
	    loadView("/fxml/workspace.fxml");
	}
	
	private void loadView(String fxmlPath) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
	        loader.setControllerFactory(injector.getControllerFactory());
	        Node view = loader.load();
	        contentArea.getChildren().setAll(view);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
