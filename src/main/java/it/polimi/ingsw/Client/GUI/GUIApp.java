package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Client.*;
import it.polimi.ingsw.exception.EndGameException;
import it.polimi.ingsw.model.BoardAttribute;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Main JavaFX GUI application
 */
public class GUIApp extends Application {
    /**
     * Information about the socket
     */
    static private SocketInfo socketInfo;
    /**
     * The board attributes
     */
    static private BoardAttribute boardAttribute;
    /**
     * The board properties
     */
    static private Integer nRow = 0, nCol = 0;
    /**
     * List of worker identifiers
     */
    static private List<Character> workerSex;
    /**
     * List of sections composing the rules menu
     */
    static private List<HelpSection> helpMenu;
    /**
     * The connected view
     */
    static private GUIView clientView;
    /**
     * Colors associated to the gui
     */
    static private Map<String, ColorCodes> GUIcolors;
    /**
     * Defines if the game is ready to start
     */
    private final BooleanProperty ready = new SimpleBooleanProperty(false);
    /**
     * Map associating god names to their images' paths
     */
    static private Map<String, GodsImages> godsImages;
    /**
     * Map associating the blocks to their images' paths
     */
    static private Map<Integer, Location> blocksImages;
    /**
     * Controller for the welcome screen
     */
    private WelcomeController welcomeController;

    /**
     * Resources path
     */
    static final String pathToFxml = "/it/polimi/ingsw/";
    /**
     * Path to endgame resources
     */
    static final String pathToEndgame = "/it/polimi/ingsw/Images/EndGame/";
    /**
     * Path to block images
     */
    static final String pathToBlocks = "/it/polimi/ingsw/Images/Blocks/";
    /**
     * Path to workers images
     */
    static final String pathToWorkers = "/it/polimi/ingsw/Images/Workers/";
    /**
     * Path to gods images
     */
    static final String pathToGods = "/it/polimi/ingsw/Images/Gods/";
    /**
     * Path to css resources
     */
    static final String pathToCSS = "/it/polimi/ingsw/CSS/";

    /**
     * The current JavaFX scene
     */
    static private Scene scene;
    /**
     * The current JavaFX stage
     */
    static private Stage stage;
    /**
     * Initial screens width
     */
    static private final Integer INTRO_WIDTH = 900;
    /**
     * Initial screens height
     */
    static private final Integer INTRO_HEIGHT = 700;
    /**
     * Board screen width
     */
    static private final Integer BOARD_WIDTH = 1200;
    /**
     * Board screen height
     */
    static private final Integer BOARD_HEIGHT = 700;
    /**
     * Map associating the info codes to the information they represent
     */
    static private Map<String, Info> infoCodes;

    @Override
    public void start(Stage stage) throws IOException {
        GUIApp.stage = stage;
        welcomeController = loadInit();
        loadGame();

        ready.addListener(new ChangeListener<Boolean>(){
            public void changed(
                    ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (Boolean.TRUE.equals(t1)) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            stage.show();
                        }
                    });
                }
            }
        });;
    }

    /**
     * Method to set the scene
     * @param fxml The source file to set
     * @throws IOException If the file cannot be accessed
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Method to set the scene
     * @param node The node to set as root
     * @throws IOException as per setRoot
     */
    public static void setRoot(Parent node) throws IOException {
        scene.setRoot(node);
    }

    /**
     * Loads the welcome screen
     * @return The welcome controller
     * @throws IOException If files cannot be accessed
     */
    public static WelcomeController loadInit() throws IOException {
        WelcomeController welcomeController;
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "InitMatchView" + ".fxml"));
        Parent root = fxmlLoader.load();
        Platform.runLater(() -> {
            if (GUIApp.scene == null)
                GUIApp.scene = new Scene(root);
            else
                GUIApp.scene.setRoot(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Santorini (GUI version)");
            stage.setWidth(INTRO_WIDTH);
            stage.setHeight(INTRO_HEIGHT);
            stage.setMinWidth(INTRO_WIDTH);
            stage.setMinHeight(INTRO_HEIGHT);
        });
        welcomeController = fxmlLoader.getController();
        return welcomeController;
    }

    /**
     * Sets the screen dimensions for the board
     */
    public static void setBoardDim() {
        stage.setWidth(BOARD_WIDTH);
        stage.setHeight(BOARD_HEIGHT);
        stage.setMinWidth(BOARD_WIDTH);
        stage.setMinHeight(BOARD_HEIGHT);
    }

    /**
     * Sets the screen dimensions for the initial screens
     * @param stage The stage to set
     */
    public static void setLoaderDim(Stage stage) {
        stage.setWidth(INTRO_HEIGHT);
        stage.setHeight(INTRO_HEIGHT);
        stage.setMinWidth(INTRO_HEIGHT);
        stage.setMinHeight(INTRO_HEIGHT);
    }

    /**
     * Loads an fxml file
     * @param fxml The name of the file to load
     * @return The loaded fxml
     * @throws IOException If the file cannot be accessed
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }

    /**
     * Loads all the configurations at the start of the game
     */
    private void loadGame() {
        //simulates long init in background
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Reader reader;
                Gson gson = new Gson().newBuilder().enableComplexMapKeySerialization().create();
                try {
                    reader = new FileReader("./config/rules.json");
                    Type type = new TypeToken<List<HelpSection>>() {}.getType();
                    helpMenu = gson.fromJson(reader, type);
                    reader = new FileReader("./config/board.json");
                    boardAttribute = gson.fromJson(reader, BoardAttribute.class);
                    nRow = boardAttribute.nRow;
                    nCol = boardAttribute.nCol;
                    workerSex = boardAttribute.workerSex;
                    reader = new FileReader("./config/godsLocation.json");
                    Type godMapType = new TypeToken<Map<String, GodsImages>>() {}.getType();
                    godsImages = gson.fromJson(reader, godMapType);

                    FileReader readerInfo = new FileReader("./config/infoCodes.json");
                    infoCodes = gson.fromJson(readerInfo, new TypeToken<Map<String, Info>>() {}.getType());
                    FileReader readerColor = new FileReader("./config/GUIColors.json");
                    GUIcolors = gson.fromJson(readerColor,  new TypeToken<Map<String, ColorCodes>>() {}.getType());
                    FileReader readerBlock = new FileReader("./config/blockLocation.json");
                    blocksImages = gson.fromJson(readerBlock,  new TypeToken<Map<Integer, Location>>() {}.getType());
                } catch(IOException exception) {
                    notifyPreloader(new Preloader.ErrorNotification("Error", "Error while loading config files:\n" + exception.getMessage(), exception));
                }

                try {
                    notifyPreloader(new Preloader.ProgressNotification((50)));
                    Socket socket = new Socket(socketInfo.getHostName(), socketInfo.getPortNumber());
                    notifyPreloader(new Preloader.ProgressNotification((70)));
                    clientView = new GUIView(socket, nRow, nCol, welcomeController, workerSex, helpMenu, infoCodes);
                    clientView.initGame();
                    clientView.viewRun();
                    notifyPreloader(new Preloader.ProgressNotification((80)));
                    clientView.readServerState();
                    notifyPreloader(new Preloader.ProgressNotification((100)));
                    clientView.startActionHandler();
                    ready.setValue(Boolean.TRUE);
                    notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
                } catch(IOException exception) {
                    notifyPreloader(new Preloader.ErrorNotification("Error", "Error while trying to communicate with the server:\n" + exception.getMessage(), exception));
                } catch(EndGameException exception) {
                    notifyPreloader(new Preloader.ErrorNotification("Error", "Sorry, the current match is full, please try again later", exception));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Getter for the block images map
     * @return The block images map
     */
    public static Map<Integer, Location> getBlocksImages() {
        return blocksImages;
    }

    /**
     * Getter for the gui colors
     * @return The gui colors map
     */
    public static Map<String, ColorCodes> getGUIcolors() {
        return GUIcolors;
    }

    /**
     * Getter for the god images map
     * @return The godsImages map
     */
    public static Map<String, GodsImages> getGodsImages() {
        return godsImages;
    }

    /**
     * Main of the application
     * @param args The main's arguments
     */
    public static void main(String[] args) {
        Gson gson = new Gson().newBuilder().enableComplexMapKeySerialization().create();
        socketInfo = Main.checkAddress(args);
        if (socketInfo == null) {
            Reader reader;
            //try read from json configuration file
            try
            {
                reader = new FileReader("./config/clientConfigs.json");
                socketInfo = gson.fromJson(reader, SocketInfo.class);
            }
            catch(IOException exception) {
                clientView = new GUIView();
                exception.printStackTrace();
                clientView.showInitError("Error while reading network config");
            }
        }
        System.setProperty("javafx.preloader", GamePreloader.class.getCanonicalName());
        launch(args);
    }
}