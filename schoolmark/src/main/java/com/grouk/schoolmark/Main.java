package com.grouk.schoolmark;

import com.grouk.schoolmark.util.CsControl;
import com.grouk.schoolmark.util.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final static String MAIN_FXML = "/fxml/main.fxml";
    private final static String RESOURCE_NAME = "uiResource";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.info("Start application.");
        URL source = getClass().getResource(MAIN_FXML);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_NAME, CsControl.Cp1251);
        String title = resourceBundle.getString("main.title");

        SceneLoader.loadScene(source, primaryStage, title, resourceBundle);
    }
}
