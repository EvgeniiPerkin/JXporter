package ru.evp.JXporter.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.*;

public class SettingsService {

    private final ObjectMapper mapper;
    private final Path configPath;
    private AppSettings settings;

    public SettingsService() throws IOException {
        mapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT);

        settings = new AppSettings();
        
        Path dir = Paths.get(settings.getConfigDirectory());
        Files.createDirectories(dir);
        configPath = dir.resolve("config.json");

        load();
        enableAutoSave();
    }

    private void load() throws IOException {
        if (Files.exists(configPath)) {
        	AppSettingsDTO dto =
                    mapper.readValue(configPath.toFile(), AppSettingsDTO.class);

            settings.setTheme(dto.theme);
            settings.setWindowWidth(dto.windowWidth);
            settings.setWindowHeight(dto.windowHeight);
            settings.setWindowX(dto.windowX);
            settings.setWindowY(dto.windowY);
            settings.setInitialDirectory(dto.initialDirectory);
        }
    }

    private void save() {
        try {
        	AppSettingsDTO dto = new AppSettingsDTO();
            dto.theme = settings.getTheme();
            dto.windowWidth = settings.getWindowWidth();
            dto.windowHeight = settings.getWindowHeight();
            dto.windowX = settings.getWindowX();
            dto.windowY = settings.getWindowY();
            dto.initialDirectory = settings.getInitialDirectory();

            mapper.writeValue(configPath.toFile(), dto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableAutoSave() {
        PauseTransition saveDelay =
                new PauseTransition(Duration.millis(400));

        ChangeListener<Number> listener = (_, _, _) -> {
            saveDelay.stop();
            saveDelay.setOnFinished(_ -> save());
            saveDelay.play();
        };
        
        settings.windowWidthProperty().addListener(listener);
        settings.windowHeightProperty().addListener(listener);
        settings.windowXProperty().addListener(listener);
        settings.windowYProperty().addListener(listener);
        
        settings.initialDirectoryProperty().addListener((_, _, _) -> save());        
        settings.themeProperty().addListener((_, _, _) -> save());
    }

    public AppSettings getSettings() {
        return settings;
    }
}
