package it.polimi.ingsw.Client.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Class controlling each item displayed during the starter player and God choosing lists
 */
public class ItemsController {
    /**
     * Label containing the text to be displayed
     */
    @FXML
    private Label label;

    /**
     * HBox containing the label
     */
    @FXML
    private HBox hbox;

    /**
     * Constructor for loading the fxml associated to the item of the list
     */
    public ItemsController()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(GUIApp.pathToFxml + "listItem.fxml"));
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
     * Setter for the name to be displayed in the label
     * @param name Name to be displayed
     */
    public void setName(String name)
    {
        Platform.runLater(() -> {
            label.setText(name);
        });
    }

    /**
     * Getter for the HBox containing the label
     * @return HBox container
     */
    public HBox getBox()
    {
        return hbox;
    }
}
