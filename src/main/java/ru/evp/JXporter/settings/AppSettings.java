package ru.evp.JXporter.settings;

import java.nio.file.Paths;

import javafx.beans.property.*;

public class AppSettings {
    private final StringProperty theme = new SimpleStringProperty("light");
    private final DoubleProperty windowWidth = new SimpleDoubleProperty(650);
    private final DoubleProperty windowHeight = new SimpleDoubleProperty(650);
    private final DoubleProperty windowX = new SimpleDoubleProperty(100);
    private final DoubleProperty windowY = new SimpleDoubleProperty(100);
    private final StringProperty initialDirectory =
            new SimpleStringProperty(System.getProperty("user.home"));
    private final StringProperty configDirectory = 
            new SimpleStringProperty(
            		Paths.get(System.getProperty("user.home"), ".jxporter")
            			.toAbsolutePath().toString());

    public StringProperty themeProperty() { return theme; }
    public DoubleProperty windowWidthProperty() { return windowWidth; }
    public DoubleProperty windowHeightProperty() { return windowHeight; }
    public DoubleProperty windowXProperty() { return windowX; }
    public DoubleProperty windowYProperty() { return windowY; }
    public StringProperty initialDirectoryProperty() { return initialDirectory; }
    public StringProperty configDirectoryProperty() { return configDirectory; }

    public String getTheme() { return theme.get(); }
    public void setTheme(String value) { theme.set(value); }

    public double getWindowWidth() { return windowWidth.get(); }
    public void setWindowWidth(double value) { windowWidth.set(value); }

    public double getWindowHeight() { return windowHeight.get(); }
    public void setWindowHeight(double value) { windowHeight.set(value); }

    public double getWindowX() { return windowX.get(); }
    public void setWindowX(double value) { windowX.set(value); }

    public double getWindowY() { return windowY.get(); }
    public void setWindowY(double value) { windowY.set(value); }

	public String getInitialDirectory() { return initialDirectory.get(); }
	public void setInitialDirectory(String value) { initialDirectory.set(value); }

	public String getConfigDirectory() { return configDirectory.get(); }
	public void setConfigDirectory(String value) { configDirectory.set(value); }
}
