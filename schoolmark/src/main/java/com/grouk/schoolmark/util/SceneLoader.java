package com.grouk.schoolmark.util;

import com.grouk.schoolmark.controller.InitDataController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Loader of new window
 * Created by Alena on 11.02.2017.
 */
public class SceneLoader {

    public static void loadScene(URL source, Stage stage, String title, ResourceBundle resourceBundle) throws IOException {
        try {
            Parent root = FXMLLoader.load(source, resourceBundle);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadSceneWithData(URL source, Stage stage, String title, ResourceBundle resourceBundle,
                                         Object data) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(source, resourceBundle);
            Parent root = loader.load();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            InitDataController<Object> controller = loader.getController();
            controller.initData(data);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
