package ru.evp.JXporter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.evp.JXporter.settings.AppSettings;
import ru.evp.JXporter.settings.SettingsService;

import org.apache.logging.log4j.ThreadContext;

public class JxApp extends Application {
	 
	public void run() {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// устанавливаем username для логов
        ThreadContext.put("username", System.getProperty("user.name"));
		SettingsService settingsService  = new SettingsService();
		// Получаем настройки и директорию для записи логов
		AppSettings settings = settingsService .getSettings();
		System.setProperty("app.logs.dir", settings.getConfigDirectory());
		// Внедрение зависимостей
		DependencyInjector injector = new DependencyInjector(settingsService);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		loader.setControllerFactory(injector.getControllerFactory());
		
		Parent root = loader.load();
		Scene scene = new Scene(root, settings.getWindowWidth(), settings.getWindowHeight());
		String stylesheet = getClass().getResource("/css/style.css").toExternalForm();
		scene.getStylesheets().add(stylesheet);
		primaryStage.setMinWidth(630);
		primaryStage.setMinHeight(630);
		// для записи в пользовательские настройки изменений положения и размеров окна
		primaryStage.setWidth(settings.getWindowWidth());
		primaryStage.setHeight(settings.getWindowHeight());
		primaryStage.setX(settings.getWindowX());
		primaryStage.setY(settings.getWindowY());		
		primaryStage.widthProperty().addListener((_, _, newV) ->
	        settings.setWindowWidth(newV.doubleValue())
		);		
		primaryStage.heightProperty().addListener((_, _, newV) ->
	        settings.setWindowHeight(newV.doubleValue())
		);		
		primaryStage.xProperty().addListener((_, _, newV) ->
	        settings.setWindowX(newV.doubleValue())
		);		
		primaryStage.yProperty().addListener((_, _, newV) ->
	        settings.setWindowY(newV.doubleValue())
		);

		primaryStage.setTitle("JXporter");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
    @Override
    public void stop() {
        ThreadContext.clearAll();
    }
}