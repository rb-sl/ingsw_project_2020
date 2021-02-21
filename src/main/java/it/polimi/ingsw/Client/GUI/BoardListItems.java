package it.polimi.ingsw.Client.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.io.IOException;

/**
 * Class controlling each item displayed in the player's list positioned to the left of the board
 */
public class BoardListItems {
    /**
     * Label containing the nickname of the player
     */
    @FXML
    private Label nick;
    /**
     * ImageView displaying the icon of the God used by the player
     */
    @FXML
    private ImageView icon;
    /**
     * Hbox containing the icon and the nickname of the player
     */
    @FXML
    private HBox hbox;

    /**
     * Constructor called at the creation of each item loading the fxml
     */
    public BoardListItems()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(GUIApp.pathToFxml + "BoardListItem.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Setter for displaying the nickname of the player in the label
     * @param name Nickname of the player
     */
    public void setNick(String name)
    {
        Platform.runLater(() -> nick.setText(name));
    }

    /**
     * Setter for displaying the icon of the God used by the player
     * @param path The path of the image of the God
     */
    public void setImage(String path)
    {
        Platform.runLater(() -> {
            Image image = new Image(getClass().getResource(GUIApp.pathToGods + path).toExternalForm(), 100, 100, true, true);
            Platform.runLater(() -> icon.setImage(image));
        });
    }

    /**
     * Setter for displaying the description of the player's God when the mouse hover this list item
     * @param text Description of the God
     */
    public void setTooltipText(String text) {
        Platform.runLater(() -> {
            Tooltip tooltip = new Tooltip();
            tooltip.setText(text);
            Tooltip.install(hbox,tooltip);
        });
    }

    /**
     * Setter for displaying the player's nickname in the color assigned to the player
     * @param color Color of the label
     */
    public void setNickColor(String color) {
        Platform.runLater(() -> nick.setTextFill(Color.web(color)));
    }

    /**
     * Getter for the hbox in the fxml
     * @return Hbox containing the icon and the label
     */
    public HBox getBox()
    {
        return hbox;
    }
}
