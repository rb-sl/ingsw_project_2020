package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.Client.Commands.InitPlayerData;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.ServerStateMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * JavaFX controller for the welcome screen
 */
public class WelcomeController {
    /**
     * The connected view
     */
    private ViewInterface view;
    /**
     * The connected guiview
     */
    private GUIView guiView;

    /**
     * Textfield for the nickname text
     */
    @FXML
    private TextField nickField;

    /**
     * Input combobox for the player name
     */
    @FXML
    private ComboBox<String> playerCombo;

    /**
     * Input combobox for the god usage
     */
    @FXML
    private ComboBox<String> godCombo;

    /**
     * Play button
     */
    @FXML
    private Button playBtn;

    /**
     * Label for the players text
     */
    @FXML
    private Label lbl_players;

    /**
     * Label for the gods usage
     */
    @FXML
    private Label lbl_gods;

    /**
     * Initialization function
     * @param view The connected view
     * @param guiView The connected gui view
     */
    @FXML
    public void initialize(ViewInterface view, GUIView guiView) {
        this.view = view;
        this.guiView = guiView;
    }

    /**
     * Hides the fields not relevant when joining a game
     */
    public void hideForJoin() {
        Platform.runLater(() -> {
            lbl_players.setVisible(false);
            godCombo.setVisible(false);
            playerCombo.setVisible(false);
            lbl_gods.setVisible(false);
        });
    }

    /**
     * Handles the actions to be taken when the play button is pressed
     */
    @FXML
    private void onPlayPress() {
        if (nickField.getText().isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Empty field");
                alert.setTitle("Attention");
                alert.setContentText("Please insert the nickname");
                alert.showAndWait();
            });
        }
        else {
            Gson gson = new Gson();
            ServerStateMessage message = guiView.getServerState();
            Command initPlayer = new InitPlayerData(view, message, gson);
            initPlayer.setClientOutput(guiView.clientOutput);
            initPlayer.execute();
        }
    }

    /**
     * Getter for the nickname field
     * @return The inserted nickname
     */
    public String getNick() {
        return nickField.getText();
    }

    /**
     * Getter for the number of players field
     * @return The chosen number of players
     */
    public Integer getnPlayer() {
        Integer n = Integer.parseInt(playerCombo.getValue());
        return n;
    }

    /**
     * Getter for the gods choice
     * @return True if the player desires to play with gods
     */
    public Boolean withGods() {
        if (godCombo.getValue().equals("Yes"))
            return true;
        return false;
    }
}
