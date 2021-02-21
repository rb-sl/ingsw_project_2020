package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.StarterMessage;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Map;

/**
 * Class associated to the fxml used to choose the starter player
 */
public class PlayerController {
    /**
     * Map containing the nickname and the God image for each player
     */
    private Map<String, GodsImages> godImgMap;
    /**
     * Boolean to represent whether the player is the challenger or not
     */
    private boolean isChallenger;
    /**
     * Reference to class containing all GUI methods
     */
    private GUI view;
    /**
     * Name of the challenger
     */
    private String challenger;
    /**
     * AnchorPane used to display the God Image
     */
    @FXML
    private AnchorPane anchor_god;
    /**
     * ListView containing the nicknames of the players
     */
    @FXML
    private ListView<String> listView;
    /**
     * Label used to show an info to the player
     */
    @FXML
    private Label lbl_descr;

    /**
     * Method used to initialize the class attributes
     * @param view GUI view managing GUI functions
     * @param challenger Nickname of the challenger
     */
    @FXML
    public void initialize(GUI view, String challenger) {
        this.godImgMap = GUIApp.getGodsImages();
        this.view = view;
        this.challenger = challenger;
        isChallenger = view.getOwnerNick().equals(challenger);
    }

    /**
     * Method used to load the God image for the first player in the list and to show the info to the player
     * @param first Name of the God to be displayed
     */
    public void load(String first) {
        setNewImage(godImgMap.getOrDefault(first, godImgMap.get("Default")).getFull());
        if (isChallenger) {
            setText("You have been chosen to pick the starter player for this match.\nPlease choose your favourite starter player");
        }
        else
            setText("Please wait while the challenger is choosing the starter player");
    }

    /**
     * Setter for the text to be displayed at the bottom of the window
     * @param text Text to be shown to the user
     */
    private void setText(String text) {
        Platform.runLater(() -> lbl_descr.setText(text));
    }


    /**
     * Setter for changing the image of the God displayed
     * @param path Path to the God image
     */
    public void setNewImage(String path) {
        Image image = new Image(getClass().getResource(GUIApp.pathToGods + path).toExternalForm());
        BackgroundSize size = new BackgroundSize(0.8, 0.8, true, true, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        Background bg = new Background(backgroundImage);
        Platform.runLater(() -> anchor_god.setBackground(bg));
    }

    /**
     * Method invoked when the list is being created
     */
    public void customizePlayerList() {
        listView.setCellFactory(lv -> {
            ListCell<String> cells = new ListCell<>() {
                //Controls to avoid displaying text if the item is empty or null
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setDisable(true);
                    if (item != null) {
                        ItemsController itemsController = new ItemsController();
                        itemsController.setName(item + " (" + view.getPlayers().get(item) + ")");
                        Platform.runLater(() -> setGraphic(itemsController.getBox()));
                        setDisable(false);
                    }
                }
            };
            //Setting the events for each list item
            //When the mouse enters in the item the image of the God displayed changes
            cells.setOnMouseEntered(mouseEvent -> {
                if (!cells.isEmpty()) {
                    setNewImage(godImgMap.getOrDefault(view.getPlayers().get(cells.getItem()), godImgMap.get("Default")).getFull());
                }
            });
            //When an element is clicked the starter player is selected if the user is the challenger; otherwise a message is
            //displayed to invite the user to wait for its turn
            cells.setOnMouseClicked(mouseEvent -> {
                Integer index = listView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < view.getPlayers().size()) {
                    if (isChallenger) {
                        Gson gson = new Gson();
                        StarterMessage starterMessage = new StarterMessage(
                                listView.getSelectionModel().getSelectedItem(), view.getOwnerNick());
                        String sendMessage = gson.toJson(starterMessage, StarterMessage.class);
                        MessageEnvelope sendEnvelope = new MessageEnvelope(MessageType.CHOSESTARTER, sendMessage);
                        Command.sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));
                    } else
                        view.challengerMessage(challenger);
                }
            });
            return cells;
        });
    }

    /**
     * Getter for the ListView
     * @return ListView used to display the list of players
     */
    public ListView<String> getList() {
        return listView;
    }
}
