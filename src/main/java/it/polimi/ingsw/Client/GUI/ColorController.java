package it.polimi.ingsw.Client.GUI;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Class associated to the fxml used for choosing the color of the player's worker
 */
public class ColorController {
    /**
     * Reference to class containing all GUI methods
     */
    private GUI view;
    /**
     * Map containing the color of the players
     */
    private Map<String, ColorCodes> colorMap;
    /**
     * Character of the worker image displayed
     */
    private Character sex = 'F';
    /**
     * Color currently selected by the player
     */
    private String color;
    /**
     * Lock for avoiding background processing of other messages during color selection
     */
    private Lock lockColor;

    /**
     * Method to initialize the variables for this class
     * @param view GUI view managing GUI functions
     * @param colorMap Map of the players' colors
     * @param lockColor Lock for blocking outer execution
     */
    public void initialize(GUI view, Map<String, ColorCodes> colorMap, Lock lockColor) {
        this.view = view;
        this.colorMap = colorMap;
        this.lockColor = lockColor;
        GUI.lockColors.lock();
    }

    /**
     * ImageView containing the image of the worker (female/male) displayed
     */
    @FXML
    private ImageView img_worker;

    /**
     * TilePane containing all of the available colors
     */
    @FXML
    private TilePane tile_color;

    /**
     * Setter for path of the image to be displayed
     * @param path Relative path to the image
     */
    private void setImage(String path) {
        Image image = new Image(getClass().getResource(GUIApp.pathToWorkers + path).toExternalForm());
        Platform.runLater(() -> img_worker.setImage(image));
    }

    /**
     * Method invoked when clicking on male button to change the worker image being displayed
     */
    @FXML
    private void mouseClickedMale() {
        sex = 'M';
        setImage(colorMap.get(color).getMale());
    }

    /**
     * Method invoked when clicking on female button to change the worker image being displayed
     */
    @FXML
    private void mouseClickedFemale() {
        sex = 'F';
        setImage(colorMap.get(color).getFemale());
    }

    /**
     * Method to generate all of the buttons in the TilePane representing all the available GUI colors
     */
    public void createButtons() {
        boolean isFirstColor = true;
        //Creation of all buttons made using the colors in the color file
        for (String code : colorMap.keySet()) {
            Button btn = new Button();
            //Set first color so that when Male or Female button is clicked before color change, color is not null
            if (isFirstColor) {
                setColor(code);
                isFirstColor = false;
            }
            Paint color = Color.web(code);
            BackgroundFill bgFill = new BackgroundFill(color, null, null);
            Background bg = new Background(bgFill);
            btn.setBackground(bg);
            btn.setPrefSize(80, 80);
            btn.setCursor(Cursor.HAND);
            //Handling for the events of the buttons
            //When the mouse enters in the buttons the color of the image displayed changes
            btn.setOnMouseEntered(mouseEvent -> {
                String path;
                if (sex.equals('F')) {
                    path = colorMap.get(code).getFemale();
                }
                else
                    path = colorMap.get(code).getMale();
                setImage(path);
                setColor(code);
            });
            //When the user clicks on the buttons the color is selected and the background operations may continue
            btn.setOnMouseClicked(mouseEvent -> {
                view.setColors(code);
                view.showPlayers();
                GUI.lockColors.unlock();
            });
            tile_color.getChildren().add(btn);
        }

    }

    /**
     * Setter for setting the current color of the worker image displayed
     * @param color Color of the current worker image displayed
     */
    public void setColor(String color) {
        this.color = color;
    }
}
