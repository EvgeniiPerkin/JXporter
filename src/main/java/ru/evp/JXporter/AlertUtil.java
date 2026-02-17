package ru.evp.JXporter;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertUtil {
	private static void show(
            AlertType type,
            String title,
            String header,
            String content
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ERROR
    public static void error(String title, String header, String content) {
        show(AlertType.ERROR, title, header, content);
    }

    // WARNING
    public static void warning(String title, String header, String content) {
        show(AlertType.WARNING, title, header, content);
    }

    // INFORMATION
    public static void info(String title, String header, String content) {
        show(AlertType.INFORMATION, title, header, content);
    }

    // CONFIRMATION (возвращает true / false)
    public static boolean confirm(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
