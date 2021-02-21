package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.messages.ChosenGodMessage;
import it.polimi.ingsw.messages.GodInitMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to manage the choosing of the God
 */
public class GodController {

    /**
     * List containing all of the Gods available to be chosen
     */
    private List<ChosenGodMessage> godList;

    /**
     * Map containing the name of the Gods along with their images
     */
    private Map<String, GodsImages> godImgMap;

    /**
     * ListView used to display the names of the available Gods
     */
    @FXML
    private ListView<ChosenGodMessage> listView;

    /**
     * Label containing the description of the current God displayed
     */
    @FXML
    private Label lbl_descr;

    /**
     * AnchorPane used to display the God Image
     */
    @FXML
    private AnchorPane anchor_god;

    /**
     * Label containing the title for this window
     */
    @FXML
    private Label lbl_title;

    /**
     * Reference to class containing all GUI methods
     */
    private GUI view;

    /**
     * Current player that is choosing the God
     */
    private String activePlayer;

    /**
     * Observable List of item used to contain the items in the ListView
     */
    private ObservableList<ChosenGodMessage> observableList;

    /**
     * Method to initialize attributes for this class
     * @param view GUI view managing GUI functions
     * @param observableList Observable list displayed by the ListView
     */
    @FXML
    public void initialize(GUI view, ObservableList<ChosenGodMessage> observableList) {
        this.view = view;
        this.observableList = observableList;
        this.godImgMap = GUIApp.getGodsImages();
    }

    /**
     * Setter for the title displayed at the top of this window
     * @param title Title of the window
     */
    public void setTitle(String title) {
        Platform.runLater(() -> lbl_title.setText(title));
    }

    /**
     * Setter for the list of Gods to be displayed
     * @param godList List of Gods
     */
    public void setGodList(List<ChosenGodMessage> godList) {
        this.godList = godList;
    }

    /**
     * Method to update the list of Gods to contain only the remaining ones after other players has chosen theirs
     */
    public void updateListView() {
        List<String> godsName = godList.stream().map(el -> el.getName()).collect(Collectors.toList());
        List<ChosenGodMessage> toRemove = observableList.stream().filter(el -> !godsName.contains(el.getName())).collect(Collectors.toList());
        observableList.removeAll(toRemove);
        Platform.runLater(() -> listView.getItems().removeAll(toRemove));
    }

    /**
     * Setter for the active player that is choosing the God
     * @param activePlayer Nickname of the current player
     */
    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
    }

    /**
     * Getter for the ListView
     * @return ListView contained in the scene
     */
    public ListView getList() {
        return listView;
    }

    /**
     * Method to load the first God of the list
     * @param first God at the top of the list
     */
    public void load(ChosenGodMessage first) {
        setNewImage(godImgMap.getOrDefault(first.getName(), godImgMap.get("Default")).getFull());
        setDescr(first.getDescription());
    }

    /**
     * Setter for changing the image of the God displayed
     * @param path Relative pat to the God image
     */
    public void setNewImage(String path) {
        Image image = new Image(getClass().getResource(GUIApp.pathToGods + path).toExternalForm());
        BackgroundSize size = new BackgroundSize(0.8, 0.8, true, true, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        Background bg = new Background(backgroundImage);
        Platform.runLater(() -> anchor_god.setBackground(bg));
    }

    /**
     * Setter for changing the description of the current God displayed at the bottom of the window
     * @param descr Description of the God
     */
    public void setDescr(String descr) {
        Platform.runLater(() -> lbl_descr.setText(descr));
    }

    /**
     * Method invoked when the list is being created
     */
    public void customizeGodList() {
        listView.setCellFactory(lv -> {
            ListCell<ChosenGodMessage> cells = new ListCell<>() {
                //Called when a new element is added to the list or changed
                @Override
                public void updateItem(ChosenGodMessage item, boolean empty) {
                    super.updateItem(item, empty);
                    //Controls to avoid displaying text if the item is empty or null
                    if (empty) {
                        setDisable(true);
                        setText("");
                        Platform.runLater(() -> setGraphic(null));
                    }
                    if (item != null) {
                        ItemsController itemsController = new ItemsController();
                        itemsController.setName(item.getName());
                        Platform.runLater(() -> setGraphic(itemsController.getBox()));
                        setDisable(false);
                    }
                }
            };
            //Setting the events for each list item
            //When the mouse enters in the item the image and the description of the God displayed changes
            cells.setOnMouseEntered(mouseEvent -> {
                if (!cells.isEmpty()) {
                    setNewImage(godImgMap.getOrDefault(cells.getItem().getName(), godImgMap.get("Default")).getFull());
                    setDescr(cells.getItem().getDescription());
                }
            });
            //When an element is clicked the God is selected if the user is the active player; otherwise a message is
            //displayed to invite the user to wait for its turn
            cells.setOnMouseClicked(mouseEvent -> {
                Integer index = listView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < godList.size()) {
                    if (view.getOwnerNick().equals(activePlayer)) {
                        Gson gson = new Gson();
                        GodInitMessage godInitMessage = new GodInitMessage(
                                godList.get(listView.getSelectionModel().getSelectedIndex()).getName(),
                                view.getOwnerNick());
                        String clientMessage = gson.toJson(godInitMessage, GodInitMessage.class);
                        MessageEnvelope sendEnvelope = new MessageEnvelope(MessageType.GODINIT, clientMessage);
                        Command.sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));
                    } else {
                        view.waitForChoose(activePlayer);
                    }
                }
            });
            return cells;
        });
    }

}
