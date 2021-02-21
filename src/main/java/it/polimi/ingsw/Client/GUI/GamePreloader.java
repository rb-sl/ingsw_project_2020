package it.polimi.ingsw.Client.GUI;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Class used to manage the game preloader shown when the game is loading its files and connecting to the server
 */
public class GamePreloader extends Preloader {
    /**
     * ProgressBar showing the loading progress
     */
    private ProgressBar loadProgress;
    /**
     * Stage for the preloader window
     */
    private Stage stage;
    /**
     * Scene for the preloader visual effects
     */
    private Scene scene;

    /**
     * Method invoked to manage preloader initialization
     * @param stage Stage the preloader should be displayed into
     * @throws Exception Thrown when an exception happens during the Preloader construction
     */
    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "preloader.fxml")).load());
        loadProgress = (ProgressBar)scene.lookup("#loadProgress");
        this.stage = stage;
        GUIApp.setLoaderDim(stage);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method for handling all of the notifications sent by the application to the preloader
     * @param notification Notification sended by the Application
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        //Notification sent when the progress needs to be updated
        if (notification instanceof ProgressNotification) {
            double progress = ((ProgressNotification) notification).getProgress();
            loadProgress.setProgress(progress);
        }
        //Notification sent when the loading process ends
        if (notification instanceof StateChangeNotification) {
            //Hide the preloader when is needed no more
            stage.hide();
        }
        //Notification sent when an error happens during the loading process
        if (notification instanceof ErrorNotification) {
            ErrorNotification error = (ErrorNotification)notification;
            ButtonType close = new ButtonType("Close game", ButtonBar.ButtonData.CANCEL_CLOSE);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, error.getDetails(), close);
                alert.setHeaderText("An error occurred");
                alert.setTitle("Error");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == close) {
                        Platform.exit();
                    }
                }
            });
        }
    }
}
