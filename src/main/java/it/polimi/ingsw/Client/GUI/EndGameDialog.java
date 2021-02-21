package it.polimi.ingsw.Client.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Dialog displayed each time a player disconnects, wins or loses
 */
public class EndGameDialog {

    /**
     * Bar of buttons displayed in the dialog
     */
    @FXML
    private ButtonBar btn_bar;

    /**
     * Buttons displayed int the dialog
     */
    @FXML
    private Button btn_ok, btn_no, btn_view;

    /**
     * Label containing the information to be shown to the user
     */
    @FXML
    private Label lbl_info;

    /**
     * AnchorPane used as a container for the other elements
     */
    @FXML
    private AnchorPane anchor;

    /**
     * ImageView used to display the trumpets at the top of the dialog
     */
    @FXML
    private ImageView img_left, img_right;

    /**
     * Getter for the ok button
     * @return Button for the ok case
     */
    public Button getBtn_ok() {
        return btn_ok;
    }

    /**
     * Getter for the no button
     * @return Button for the no case
     */
    public Button getBtn_no() {
        return btn_no;
    }

    /**
     * Getter for the View button
     * @return Button for the view case
     */
    public Button getBtn_view() {
        return btn_view;
    }

    /**
     * Setter for changing the trumpets images in this dialog
     * @param victory True when the dialog is destined to the winner, false otherwise
     */
    public void setBackgroundImage(boolean victory) {
        Image image_left, image_right;

        if (!victory) {
            image_left = new Image(GUIApp.class.getResource(GUIApp.pathToEndgame + "trumpet_defeat_l.png").toExternalForm());
            image_right = new Image(GUIApp.class.getResource(GUIApp.pathToEndgame + "trumpet_defeat_r.png").toExternalForm());
            Platform.runLater(() -> {
                anchor.setStyle("-fx-background-image: url(/it/polimi/ingsw/Images/EndGame/Defeat.png)");
                img_left.setImage(image_left);
                img_right.setImage(image_right);
            });
        }
    }

    /**
     * Setter for the text to be shown to the user
     * @param text Information to be shown
     */
    public void setText(String text) {
        Platform.runLater(() -> lbl_info.setText(text));
    }
}
