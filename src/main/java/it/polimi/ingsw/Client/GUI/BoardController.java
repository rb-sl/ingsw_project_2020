package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.Client.Commands.CommandType;
import it.polimi.ingsw.Client.Commands.BuildCommand;
import it.polimi.ingsw.Client.Commands.MoveCommand;
import it.polimi.ingsw.Client.Commands.PassCommand;
import it.polimi.ingsw.Client.Commands.UndoCommand;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.BoardUpdate;
import it.polimi.ingsw.model.map.SimpleCoordinates;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Javafx controller for the board window
 */
public class BoardController {
    /**
     * The grid representing the board
     */
    @FXML
    private GridPane grid_board;

    /**
     * Buttons used in the window
     */
    @FXML
    private Button btn_level1, btn_level2, btn_level3, btn_dome, btn_undo, btn_pass, btn_image, btn_help;

    /**
     * Label containing the player's nickname
     */
    @FXML
    private Label lbl_nickname;

    /**
     * Text used to give tips on actions
     */
    @FXML
    private TextFlow text_info;

    /**
     * List of opponents
     */
    @FXML
    private ListView<String> lv_players;

    /**
     * Tooltip for the god's description
     */
    @FXML
    private Tooltip tooltip_img;

    /**
     * Size of the textflow
     */
    private final Integer textFlowSize = 18;

    /**
     * The connected view
     */
    private GUI view;

    /**
     * Flag stating if a following undo undoes all the turn
     */
    private boolean undoTurn = false;

    /**
     * Map associating the block levels to their images' locations
     */
    private Map<Integer, Location> blockImage;

    /**
     * Map associating the worker images to their paths based on color
     */
    private Map<String, ColorCodes> workerImages;

    /**
     * Variable storing the last clicked level button
     */
    private Integer chosenLevel = 0;

    /**
     * The latest used worker's identifier
     */
    private char workerSex = 'M';

    /**
     * Map associating the players to their colors
     */
    private Map<String, String> playersColor;

    /**
     * The update from the server
     */
    private UpdateMessage updateMessage;

    /**
     * List of available commandTypes at a given time
     */
    private List<CommandType> commands = new ArrayList<>();

    /**
     * Map associating the cells to their respective stackpanes to be highlighted in a move
     */
    private final Map<SimpleCoordinates, StackPane> moveStackpanes = new HashMap<>();

    /**
     * Map associating the cells to their respective stackpanes to be highlighted in a build
     */
    private final Map<SimpleCoordinates, StackPane> buildStackpanes = new HashMap<>();

    /**
     * The coordinates of the latest click
     */
    private SimpleCoordinates currentWorker, selectedCell;

    /**
     * Map associating the go names to their images
     */
    private Map<String, GodsImages> godsImagesMap;

    /**
     * Timer used in the pass phase
     */
    private Timer timer;

    /**
     * Images' original dimensions
     */
    private final Integer originalWidth = 90, originalHeight = 90;

    /**
     * Effect applied to labels
     */
    private DropShadow lblEffect;

    /**
     * Color of the labels
     */
    private Paint lblColor;

    /**
     * Application's font
     */
    private Font lillybelle;

    /**
     * Object used to synchronize threads and correctly create the board
     */
    private static final Object boardLock = new Object();

    /**
     * Initializes the object
     * @param view The connected view
     * @param blockImage The map of block levels associated to their paths
     * @param playersColor The colors associated to the players
     * @param workerImages The images associated to the workers
     */
    public void initialize(GUI view, Map<Integer, Location> blockImage, Map<String, String> playersColor, Map<String, ColorCodes> workerImages) {
        this.view = view;
        this.blockImage = blockImage;
        this.playersColor = playersColor;
        this.workerImages = workerImages;
        this.godsImagesMap = GUIApp.getGodsImages();
        setLabelEffect();
    }

    /**
     * Creates the label effect
     */
    private void setLabelEffect() {
        lblEffect = new DropShadow();
        lblEffect.setColor(Color.BLACK);
        lblEffect.setBlurType(BlurType.THREE_PASS_BOX);
        lblEffect.setRadius(2);
        lblEffect.setHeight(5);
        lblEffect.setWidth(5);
    }

    /**
     * Setter for the worker identifier
     * @param workerSex The worker identifier to be set
     */
    public void setWorkerSex(char workerSex) {
        this.workerSex = workerSex;
    }

    /**
     * Getter for the selected cell
     * @return The selected coordinates
     */
    public SimpleCoordinates getSelectedCell() {
        return selectedCell;
    }

    /**
     * Getter for the selected worker's position
     * @return The worker's coordinates
     */
    public SimpleCoordinates getCurrentWorker() {
        return currentWorker;
    }

    /**
     * Method to initialize the board
     */
    public void initBoard() {
        for (int i = 0; i < view.getnRow(); i++) {
            for (int j = 0; j < view.getnCol(); j++) {
                StackPane stackPane = new StackPane();
                stackPane.setStyle("-fx-border-color: transparent; -fx-border-width: 3; -fx-border-style: SOLID;");
                int finalI = i;
                int finalJ = j;
                Platform.runLater(() -> {
                    synchronized (boardLock) {
                        grid_board.add(stackPane, finalI, finalJ);
                        boardLock.notify();
                    }
                });

            }
        }
        this.lillybelle = Font.loadFont(getClass().getResource(GUIApp.pathToCSS + "LillyBelle.ttf").toExternalForm(), textFlowSize);
        setNickText(view.getOwnerNick());
        setGodImage(view.getPlayers().get(view.getOwnerNick()));
        tooltip_img.setText(view.getGods().get(view.getPlayers().get(view.getOwnerNick())));
        setOwnerColor();
    }

    /**
     * Creates and sets the owner's god image
     * @param godName The owner's god
     */
    public void setGodImage(String godName) {
        Image image = new Image(getClass().getResource(GUIApp.pathToGods + godsImagesMap.getOrDefault(godName, godsImagesMap.get("Default")).getFull()).toExternalForm());
        BackgroundSize size = new BackgroundSize(0.8, 0.8, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        Background bg = new Background(backgroundImage);
        Platform.runLater(() -> {
            btn_image.setBackground(bg);
        });
    }

    /**
     * Sets the owner's color
     */
    private void setOwnerColor() {
        lblColor = Color.web(playersColor.get(view.getOwnerNick()));
        Platform.runLater(() -> {
            lbl_nickname.setEffect(lblEffect);
            lbl_nickname.setTextFill(lblColor);
        });
    }

    /**
     * Sets the text under the board
     * @param info The information to display
     * @param clear States if the text must be cleared before appending info
     */
    public void setInfoText(String info, boolean clear) {
        Text text = new Text(info);
        text.setFont(lillybelle);
        text.setEffect(lblEffect);
        text.setFill(lblColor);
        Platform.runLater(() -> {
            if (text_info.getChildren().size() != 0 && clear) {
                text_info.getChildren().clear();
            }
            text_info.getChildren().add(text);
        });
    }

    /**
     * Adds the move text to the info text
     */
    public void setMoveText() {
        Text text = new Text("MOVE ");
        text.setFont(lillybelle);
        text.setEffect(lblEffect);
        text.setFill(Color.CYAN);
        Platform.runLater(() -> {
            if (text_info.getChildren().size() != 0) {
                text_info.getChildren().clear();
            }
            text_info.getChildren().add(text);
        });
    }

    /**
     * Adds the build text to the info text
     */
    public void setBuildText() {
        Text text = new Text("BUILD ");
        text.setFont(lillybelle);
        text.setEffect(lblEffect);
        text.setFill(Color.MAGENTA);
        Platform.runLater(() -> {
            text_info.getChildren().add(text);
        });
    }

    /**
     * Adds more information to the info text
     */
    public void setCommonText() {
        Text text = new Text("by selecting one of your workers");
        text.setFont(lillybelle);
        text.setEffect(lblEffect);
        text.setFill(lblColor);
        Platform.runLater(() -> {
            text_info.getChildren().add(text);
        });
    }

    /**
     * Clears the info text
     */
    public void clearText() {
        if (text_info.getChildren().size() != 0) {
            Platform.runLater(() -> {
                text_info.getChildren().clear();
            });
        }
    }

    /**
     * Sets the player's nickname to be shown
     * @param nickname The nickname to show
     */
    public void setNickText(String nickname) {
        Platform.runLater(() -> {
            lbl_nickname.setText(nickname);
        });
    }

    /**
     * Setter for the timer attribute
     * @param timer The attribute to set
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * Defines the undo buttons's behaviour
     */
    public void onClickUndo() {
        if (moveStackpanes.size() != 0)
            clearMovableBorder();
        if (buildStackpanes.size() != 0)
            clearBuildableBorder();
        if (undoTurn) {
            if (timer != null) {
                timer.purge();
                timer.cancel();
                view.setHasMoved(false);
                Gson gson = new Gson();
                UndoMessage undoMessage = new UndoMessage(view.getOwnerNick(), true);
                String message = gson.toJson(undoMessage, UndoMessage.class);
                MessageEnvelope envelope = new MessageEnvelope(MessageType.UNDO, message);
                Command.sendMessage(gson.toJson(envelope, MessageEnvelope.class));
                setUndoAction();
                timer = null;
            }
        }
        else {
            UndoCommand undoCommand = new UndoCommand(view, new Gson(), updateMessage);
            undoCommand.execute();
        }
    }

    /**
     * Defines the pass button's behaviour
     */
    public void onClickPass() {
        btn_pass.setDisable(true);
        if (moveStackpanes.size() != 0) {
            clearMovableBorder();
        }
        if (buildStackpanes.size() != 0) {
            clearBuildableBorder();
        }
        setInfoText("From now on you have 5 seconds to undo your turn. \nPress Undo Turn to undo your last turn", true);
        Command command = new PassCommand(view, new Gson(), updateMessage);
        command.execute();
    }

    /**
     * Sets the turn text to the undo button
     */
    public void setUndoTurn() {
        undoTurn = true;
        Platform.runLater(() -> {
            btn_undo.setText("Undo turn");
        });
    }

    /**
     * Sets the action text to the undo button
     */
    public void setUndoAction() {
        undoTurn = false;
        Platform.runLater(() -> {
            btn_undo.setText("Undo action");
        });
    }

    /**
     * Adds a worker on the given position
     * @param stackPane The stackpane where to add the worker
     * @param nickname The player's nickname
     * @param workerSex The worker identifier
     */
    private void setWorker(StackPane stackPane, String nickname, char workerSex) {
        if (workerSex == 'F') {
            addImage(stackPane, GUIApp.pathToWorkers, workerImages.get(playersColor.get(nickname)).getFemale());
        }
        else
            addImage(stackPane, GUIApp.pathToWorkers, workerImages.get(playersColor.get(nickname)).getMale());
    }

    /**
     * Setter for the server's update
     * @param updateMessage The update from the server
     */
    public void setUpdate(UpdateMessage updateMessage) {
        this.updateMessage = updateMessage;
    }

    /**
     * Setter for the commands list
     * @param commands The commands to set
     */
    public void setCommands(List<CommandType> commands) {
        this.commands = commands;
    }

    /**
     * Enables the pass button
     */
    public void enablePass() {
        btn_pass.setDisable(false);
    }

    /**
     * Enables the undo button
     */
    public void enableUndo() {
        btn_undo.setDisable(false);
    }

    /**
     * Disables all buttons (minus help)
     */
    public void disableCommands() {
        btn_undo.setDisable(true);
        btn_pass.setDisable(true);
        btn_level1.setDisable(true);
        btn_level2.setDisable(true);
        btn_level3.setDisable(true);
        btn_dome.setDisable(true);
    }

    /**
     * JavaFX onClick listener; performs all actions given the application's state
     * @param e The click event
     */
    @FXML
    public void onClickBoard(Event e) {
        Node source = (Node) e.getTarget();
        Integer column = GridPane.getColumnIndex(source);
        Integer row = GridPane.getRowIndex(source);

        //Click on imageView where row and column return null
        if (row == null && column == null) {
            Parent parent = source.getParent();
            column = GridPane.getColumnIndex(parent);
            row = GridPane.getRowIndex(parent);
        }

        //If they are still null it means nothing on the grid is clicked except for borders
        if (row != null && column != null) {
            if (view.isInit()) {
                //Place worker on the board and send message to the server
                WorkerInitMessage workerInitMessage = new WorkerInitMessage(new SimpleCoordinates(row, column), view.getOwnerNick(), workerSex);
                Gson gson = new Gson();
                String toSend = gson.toJson(workerInitMessage, WorkerInitMessage.class);
                MessageEnvelope sendEnvelope = new MessageEnvelope(MessageType.WORKERINIT, toSend);
                Command.sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));
            } else {
                if (view.isEndGame()) {
                    if (view.isWinner())
                        view.endGame(view.getOwnerNick());
                    else
                        view.endGame("");
                    setInfoText("Click anywhere on the board to continue", true);
                    view.setEndGame(false);
                } else {
                    //Check all game conditions and moves worker or builds blocks
                    //Help command always present
                    if (commands.size() > 1) {
                        SimpleCoordinates coordinates = new SimpleCoordinates(row, column);
                        List<SimpleCoordinates> selected = view.getWorkers().values().stream().filter(coor -> coordinates.equals(coor)).collect(Collectors.toList());
                        if (moveStackpanes.size() != 0) {
                            if (moveStackpanes.containsKey(coordinates)) {
                                selectedCell = coordinates;
                                Command command = new MoveCommand(view, new Gson(), updateMessage);
                                command.execute();
                                clearMovableBorder();
                            } else {
                                if (selected.size() != 0) {
                                    clearMovableBorder();
                                }
                            }
                        }
                        if (buildStackpanes.size() != 0) {
                            if (buildStackpanes.containsKey(coordinates)) {
                                selectedCell = coordinates;
                                Command command = new BuildCommand(view, new Gson(), updateMessage);
                                command.execute();
                                clearBuildableBorder();
                            } else {
                                if (selected.size() != 0) {
                                    clearBuildableBorder();
                                }
                            }
                        }
                        if (selected.size() != 0) {
                            currentWorker = selected.get(0);
                            if (!commands.contains(CommandType.MOVE) && !commands.contains(CommandType.BUILD))
                                clearText();
                            if (commands.contains(CommandType.MOVE)) {
                                List<SimpleCoordinates> reachables = updateMessage.getReachableCells().get(coordinates);
                                setMovableBorder(reachables);
                                setMoveText();
                                setInfoText("into", false);
                            }
                            if (commands.contains(CommandType.BUILD)) {
                                Map<SimpleCoordinates, List<Integer>> buildableMap = updateMessage.getBuildableCells().get(coordinates);
                                List<Integer> buildableLevels = buildableMap.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
                                clearText();
                                if (buildableLevels.size() != 0) {
                                    if (buildableLevels.contains(1)) {
                                        btn_level1.setDisable(false);
                                    }
                                    if (buildableLevels.contains(2)) {
                                        btn_level2.setDisable(false);
                                    }
                                    if (buildableLevels.contains(3)) {
                                        btn_level3.setDisable(false);
                                    }
                                    if (buildableLevels.contains(4)) {
                                        btn_dome.setDisable(false);
                                    }
                                    setInfoText("Select the level you want to ", false);
                                    setBuildText();
                                    setInfoText("with", false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Getter for the chosen level attribute
     * @return The chosen level
     */
    public Integer getChosenLevel() {
        return chosenLevel;
    }

    /**
     * Removes the borders on the reachable cells
     */
    private void clearMovableBorder() {
        synchronized (moveStackpanes) {
            for (SimpleCoordinates coor : moveStackpanes.keySet()) {
                Platform.runLater(() -> {
                    moveStackpanes.get(coor).setStyle("-fx-border-color: transparent; -fx-border-width: 3; -fx-border-style: SOLID;");
                    moveStackpanes.remove(coor);
                });
            }
        }
    }

    /**
     * Removes the borders on the buildable cells
     */
    private void clearBuildableBorder() {
        synchronized (buildStackpanes) {
            for (SimpleCoordinates coor : buildStackpanes.keySet()) {
                Platform.runLater(() -> {
                    buildStackpanes.get(coor).setStyle("-fx-border-color: transparent; -fx-border-width: 3; -fx-border-style: SOLID;");
                    buildStackpanes.remove(coor);
                });
            }
        }
    }

    /**
     * Adds the borders to the reachable cells
     * @param reachables The cells to be marked
     */
    private void setMovableBorder(List<SimpleCoordinates> reachables) {
        synchronized (moveStackpanes) {
            for (SimpleCoordinates coor : reachables) {
                Integer index = coor.getColumn() * view.getnCol() + coor.getRow();
                StackPane stackPane = (StackPane) grid_board.getChildren().get(index);
                Platform.runLater(() -> {
                    moveStackpanes.put(coor, stackPane);
                    stackPane.setStyle("-fx-border-color: cyan; -fx-border-width: 3; -fx-border-style: SOLID;");
                });
            }
        }
    }

    /**
     * Adds the borders to the buildable cells
     * @param buildables The cells to be marked
     */
    private void setBuildableBorder(List<SimpleCoordinates> buildables) {
        synchronized (buildStackpanes) {
            for (SimpleCoordinates coor : buildables) {
                Integer index = coor.getColumn() * view.getnCol() + coor.getRow();
                StackPane stackPane = (StackPane) grid_board.getChildren().get(index);
                Platform.runLater(() -> {
                    buildStackpanes.put(coor, stackPane);
                    stackPane.setStyle("-fx-border-color: magenta; -fx-border-width: 3; -fx-border-style: SOLID;");
                });
            }
        }
    }

    /**
     * Sets the buildable cells list for the selected worker
     */
    private void setBuildableCells() {
        if (buildStackpanes.size() != 0)
            clearBuildableBorder();
        Map<SimpleCoordinates, List<Integer>> buildableLevels = updateMessage.getBuildableCells().get(currentWorker);
        List<SimpleCoordinates> buildableCells = buildableLevels.keySet().stream().filter(key -> buildableLevels.get(key).contains(chosenLevel)).collect(Collectors.toList());
        setBuildableBorder(buildableCells);
    }

    /**
     * Actions when selecting a level 1 block
     */
    public void onClickLevel1() {
        chosenLevel = 1;
        if (moveStackpanes.size() != 0) {
            clearMovableBorder();
        }
        setBuildableCells();
    }

    /**
     * Actions when selecting a level 2 block
     */
    public void onClickLevel2() {
        chosenLevel = 2;
        if (moveStackpanes.size() != 0) {
            clearMovableBorder();
        }
        setBuildableCells();
    }

    /**
     * Actions when selecting a level 3 block
     */
    public void onClickLevel3() {
        chosenLevel = 3;
        if (moveStackpanes.size() != 0) {
            clearMovableBorder();
        }
        setBuildableCells();
    }

    /**
     * Actions when selecting a dome
     */
    public void onClickDome() {
        chosenLevel = 4;
        if (moveStackpanes.size() != 0) {
            clearMovableBorder();
        }
        setBuildableCells();
    }

    /**
     * Defines the help button's behaviour
     */
    public void onHelpClick() {
        view.showHelp();
    }

    /**
     * Adds an image to the board
     * @param stackPane The stack where to add the image
     * @param absolutePath The absolute path to the image
     * @param relativePath The relative path to the image
     */
    private void addImage(StackPane stackPane, String absolutePath, String relativePath) {
        Image image = new Image(getClass().
                getResource(absolutePath + relativePath).toExternalForm());
        ImageView imageView = new ImageView();
        imageView.setFitWidth(originalWidth);
        imageView.setFitHeight(originalHeight);
        Platform.runLater(() -> {
            imageView.setImage(image);
            stackPane.getChildren().add(imageView);
        });
    }

    /**
     * Removes an image from the board
     * @param index The index in the stackpane
     * @param stackPane The stackpane from which the image must be removed
     */
    private void removeImage(Integer index, StackPane stackPane) {
        Platform.runLater(() -> {
            stackPane.getChildren().remove(stackPane.getChildren().get(index));
        });
    }

    /**
     * Changes an image on the board
     * @param stackPane The stackpane to modify
     * @param path The path to the image
     * @param index The index inside the stackpane
     */
    private void changeImage(StackPane stackPane, String path, Integer index) {
        Image image = new Image(getClass().
                getResource(path).toExternalForm());
        ImageView imageView = (ImageView)stackPane.getChildren().get(index);
        Platform.runLater(() -> {
            imageView.setImage(image);
        });
    }

    /**
     * Updates a board cell based on the server message
     * @param update The server's update
     * @param stackPane The stackpane to update
     * @return The stackpane's size
     */
    public Integer adjustStack(BoardUpdate update, StackPane stackPane) {
        Integer i = 0;
        Integer oldSize = stackPane.getChildren().size();
        update.getLevelList().remove(Integer.valueOf(0));
        if (update.getLevelList().size() > stackPane.getChildren().size()) {
            i = stackPane.getChildren().size();
            while (i != update.getLevelList().size()) {
                addImage(stackPane, GUIApp.pathToBlocks, blockImage.get(update.getLevelList().get(update.getLevelList().size() - 1 - i)).getLocation());
                i++;
            }
        }
        else {
            if (update.getLevelList().size() < stackPane.getChildren().size()) {
                i = stackPane.getChildren().size();
                while (i != update.getLevelList().size()) {
                    removeImage(i - 1, stackPane);
                    i--;
                    oldSize--;
                }
            }
        }
        return oldSize;
    }

    /**
     * Updates a board cell as per the server message, given the coordinates
     * @param update The server's message
     * @param row The cell's row
     * @param column The cell's column
     */
    public void updateCell(BoardUpdate update, int row, int column) {
        // Waits for all cells to be created
        while(grid_board.getChildren().size() < view.getnRow() * view.getnCol()) {
            synchronized (boardLock) {
                try {
                    boardLock.wait();
                } catch(InterruptedException e) {
                    // Does nothing in case of exception
                }
            }
        }
        Integer index =  column*view.getnCol()+row;
        StackPane stackPane = (StackPane)grid_board.getChildren().get(index);
        Integer oldSize = adjustStack(update, stackPane);
        for (int i = 0; i < oldSize; i++) {
            changeImage(stackPane, GUIApp.pathToBlocks + blockImage.get(update.getLevelList().get(update.getLevelList().size() - 1 -i)).getLocation(), i);
        }
        if (update.getWorkerOwner() != null) {
            setWorker(stackPane, update.getWorkerOwner(), update.getWorkerSex());
        }
    }

    /**
     * Initializes the opponents list
     * @param players The players to add to the list
     */
    public void initListView(ObservableList<String> players) {
        lv_players.setItems(players);
        customizePlayersList();
    }

    /**
     * Adds god's images to the opponents list
     */
    private void customizePlayersList() {
        lv_players.setCellFactory(lv -> {
            ListCell<String> cells = new ListCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setDisable(true);
                    }
                    if (item != null) {
                        BoardListItems boardListItems = new BoardListItems();
                        boardListItems.setNickColor(playersColor.get(item));
                        boardListItems.setNick(item);
                        boardListItems.setImage(godsImagesMap.getOrDefault(view.getPlayers().get(item), godsImagesMap.get("Default")).getSmall());
                        boardListItems.setTooltipText(view.getGods().get(view.getPlayers().get(item)));
                        Platform.runLater(() -> {
                            setGraphic(boardListItems.getBox());
                        });
                        setDisable(false);
                    }
                }
            };
            return cells;
        });
    }
}
