package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.*;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.Client.Commands.CommandType;
import it.polimi.ingsw.exception.EndGameException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.map.SimpleCoordinates;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * GUI version of the client
 */
public class GUI extends ViewInterface {
    /**
     * Controller for the welcome screen
     */
    private WelcomeController welcomeController;
    /**
     * Controller for the god choose screen
     */
    private GodController godController;
    /**
     * Controller for the player choose screen
     */
    private PlayerController playerController;
    /**
     * Controller for the color choose screen
     */
    private ColorController colorController;
    /**
     * Controller for the board screen
     */
    private BoardController boardController;

    /**
     * Map associating color names to their codes
     */
    private Map<String, ColorCodes> GUIColors;
    /**
     * Map associating the players to their color
     */
    private Map<String, String> playersColor;
    /**
     * Nickname of the challenger
     */
    private String challenger;
    /**
     * Map associating the block levels to their path
     */
    private Map<Integer, Location> blocksImages;
    /**
     * View connected to the client
     */
    private GUIView guiView;

    /**
     * Flag indicating if the board has been set
     */
    private boolean isBoardSet = false;
    /**
     * Flag indicating if the gods have been set
     */
    private boolean godSet = false;
    /**
     * Flag indicating if the application is in the endgame state
     */
    private boolean endGame = false;
    /**
     * Flag used to show the lose screen
     */
    private boolean showLose = false;
    /**
     * Flag used to show the win screen
     */
    private boolean isWinner = false;

    /**
     * Lock to avoid color choose skips when the challenger chooses the starter player
     */
    public static Lock lockColors;

    /**
     * Constructor for the class
     * @param guiView The connected view
     */
    public GUI(GUIView guiView) {
        GUIColors = GUIApp.getGUIcolors();
        blocksImages = GUIApp.getBlocksImages();
        playersColor = new HashMap<>();
        this.guiView = guiView;
        lockColors = new ReentrantLock();
    }

    /**
     * Empty constructor
     */
    public GUI() {
        // Default actions
    }

    @Override
    public void setWelcomeController(WelcomeController controller) {
        this.welcomeController = controller;
    }

    @Override
    public void showInfo(String code) {
        Info info = getInfoCodes().get(code);
        Platform.runLater(() ->  {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            if (info != null) {
                alert.setContentText(info.text);
            }
            else {
                alert.setContentText(code);
            }
            alert.showAndWait();
        });
    }

    @Override
    public void showError(String error) {
        Platform.runLater(() ->  {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error:\n" + error, ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred. Press OK to close the game");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    Platform.exit();
                }
            }
        });
    }

    @Override
    public void waitMessage() {
        Platform.runLater(() -> {
            try {
                GUIApp.setRoot(GUIApp.pathToFxml + "WaitView");
            }catch(IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void challengerMessage(String challengerNick) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Wait");
            alert.setContentText("Wait for " + challengerNick + " to choose the starter player");
            alert.showAndWait();
        });
    }

    @Override
    public void waitForChoose(String activePlayer) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Wait");
            alert.setContentText("Wait for your turn while " + activePlayer + " is choosing");
            alert.showAndWait();
        });
    }

    /**
     * Getter for the isWinner attribute
     * @return The isWinner attribute
     */
    public boolean isWinner() {
        return isWinner;
    }

    @Override
    public void reset() {
        isBoardSet = false;
        godSet = false;
        endGame = false;
        showLose = false;
        isWinner = false;
        playersColor.clear();
        endGame = false;
        resetData();
    }

    @Override
    public CommandType chooseAction() {
        return null;
    }

    @Override
    public void showWelcome() {}


    @Override
    public MoveMessage move(Map<SimpleCoordinates, List<SimpleCoordinates>> reachableCells) {
        MoveMessage moveMessage = new MoveMessage(boardController.getCurrentWorker(), boardController.getSelectedCell(), getOwnerNick());
        setHasMoved(true);
        return moveMessage;
    }

    @Override
    public BuildMessage build(Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> buildableCells) {
        BuildMessage buildMessage = new BuildMessage(boardController.getCurrentWorker(), boardController.getSelectedCell(), boardController.getChosenLevel(), getOwnerNick());
        return buildMessage;
    }

    /**
     * Sets the players' colors based on the owner's choice
     * @param ownerColor The chosen color
     */
    public void setColors(String ownerColor) {
        playersColor.put(getOwnerNick(), ownerColor);
        Map<String, String> opponents = getPlayers().entrySet().stream().filter(entry -> !entry.getKey().equals(getOwnerNick()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        List<String> opponentsList = new ArrayList<String>(opponents.keySet());
        List<String> colorList = new ArrayList<String>(GUIColors.keySet());
        Random rand = new Random();
        for (String nickname : opponentsList) {
            int nRand = rand.nextInt(colorList.size());
            while (playersColor.containsValue(colorList.get(nRand)))
                nRand = rand.nextInt(colorList.size());
            playersColor.put(nickname, colorList.get(nRand));
        }
    }

    @Override
    public void showBoard() {
        if (!isBoardSet) {
            lockColors.lock();
            List<String> opponents = getPlayers().keySet().stream().filter(nick->!nick.equals(getOwnerNick())).collect(Collectors.toList());
            ObservableList<String> listObservable = FXCollections.observableArrayList(opponents);
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "Board" + ".fxml"));
            try {
                Parent root = fxmlLoader.load();
                boardController = fxmlLoader.getController();
                Platform.runLater(() -> {
                    GUIApp.setBoardDim();
                    try {
                        GUIApp.setRoot(root);
                    }
                    catch(IOException exception) {
                        // Does nothing in case of exception
                    }
                });
                boardController.initialize(this, blocksImages, playersColor, GUIColors);
                boardController.initBoard();
                boardController.initListView(listObservable);
            }
            catch(IOException exception) {
                exception.printStackTrace();
            }
            isBoardSet = true;
            lockColors.unlock();
        }
        else {
            List<SimpleCoordinates> modifiedCoordinates = getModifiedCoordinates();
            for (SimpleCoordinates coor : modifiedCoordinates) {
                boardController.updateCell(getMap()[coor.getRow()][coor.getColumn()], coor.getRow(), coor.getColumn());
            }
            boardController.disableCommands();
        }
    }

    @Override
    public void placeWorker(char sex) {
        boardController.setInfoText("Please select the cell where you want to place your " + sex + " worker. Remaining workers to be placed: " + (getWorkerSex().size() - getWorkers().size()), true);
        boardController.setWorkerSex(sex);
    }

    @Override
    public void updateMenu(UpdateMessage updateMessage) {
        Boolean hasLost = true;
        Boolean canMoveOrBuild = false;
        clearUserCommands();
        boardController.disableCommands();
        boardController.clearText();
        if (!updateMessage.getReachableCells().values().stream().map(x->x.isEmpty()).reduce(true, (a,b) -> a&&b)) {
            addUserCommand(CommandType.MOVE);
            boardController.setMoveText();
            hasLost = false;
            canMoveOrBuild = true;
        }
        if (!updateMessage.getBuildableCells().values().stream().map(x->x.isEmpty()).reduce(true, (a,b)->a&&b)) {
            addUserCommand(CommandType.BUILD);
            boardController.setBuildText();
            hasLost = false;
            canMoveOrBuild = true;
        }
        if (canMoveOrBuild) {
            boardController.setCommonText();
        }
        if (updateMessage.canPass()) {
            addUserCommand(CommandType.PASS);
            boardController.setInfoText("From now on you can pass your turn", !canMoveOrBuild);
            boardController.enablePass();
            hasLost = false;
        }
        if (updateMessage.canUndo()) {
            addUserCommand(CommandType.UNDO);
            boardController.enableUndo();
        }
        addUserCommand(CommandType.HELP);
        boardController.setCommands(getUserCommands());
        if (hasLost) {
            //A player can't lose if its worker has moved.
            if (!hasMoved()) {
                showLose();
                SimpleMessage loseMessage = new SimpleMessage(getOwnerNick());
                Gson gson = new Gson();
                String toSend = gson.toJson(loseMessage, SimpleMessage.class);
                MessageEnvelope envelope = new MessageEnvelope(MessageType.LOSE, toSend);
                Command.sendMessage(gson.toJson(envelope, MessageEnvelope.class));
            }
        }
        else
            boardController.setUpdate(updateMessage);
    }

    @Override
    public void pass() {
        Timer timer = new Timer();
        timer.schedule(new TimerClass(this), 5000);
        boardController.setUndoTurn();
        boardController.setTimer(timer);
        clearUserCommands();
    }

    public void showGod(GodListMessage godList, Gson gson) {
        List<ChosenGodMessage> gods = godList.getGodList();
        String activePlayer = godList.getPlayer();
        int i = 0;
        if (!godSet) {
            ObservableList<ChosenGodMessage> listObservable = FXCollections.observableArrayList(gods);
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "GodChoose.fxml"));
            try {
                Parent root = (Parent) fxmlLoader.load();
                godController = fxmlLoader.getController();
                Platform.runLater(() -> {
                    try {
                        GUIApp.setRoot(root);
                    } catch (IOException exception) {
                        //Does nothing in case of exception
                    }
                });
            } catch(IOException ex){
                ex.printStackTrace();
            }
            godSet = true;
            godController.initialize(this, listObservable);
            godController.setGodList(gods);
            setGods(gods);
            if (activePlayer.equals(getOwnerNick())) {
                godController.setTitle("CHOOSE YOUR FAVOURITE GOD");
            }
            else
                godController.setTitle("WAIT FOR CHOOSING YOUR GOD");
            godController.getList().setItems(listObservable);
            godController.customizeGodList();
        }
        else {
            if (activePlayer.equals(getOwnerNick())) {
                godController.setTitle("CHOOSE YOUR FAVOURITE GOD");
            }
            else
                godController.setTitle("WAIT FOR CHOOSING YOUR GOD");
            godController.setGodList(gods);
            godController.updateListView();
        }

        godController.load(gods.get(0));
        godController.setActivePlayer(activePlayer);
    }

    /**
     * Shows the players in their list
     */
    public void showPlayers() {
        List<String> playerList = new ArrayList<>(getPlayers().keySet());
        ObservableList<String> observableList = FXCollections.observableList(playerList);
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "PlayerChoose.fxml"));
        try {
            Parent root = (Parent) fxmlLoader.load();
            playerController = fxmlLoader.getController();
            Platform.runLater(() -> {
                try {
                    GUIApp.setRoot(root);
                } catch (IOException exception) {
                    //Does nothing in case of exception
                }
            });
        } catch(IOException ex){
            ex.printStackTrace();
        }
        if (playerController != null) {
            playerController.initialize(this, challenger);
            playerController.load(getPlayers().get(playerList.get(0)));
            playerController.getList().setItems(observableList);
            playerController.customizePlayerList();
        }
    }

    @Override
    public void showOpponents(PlayersMessage players, String challenger) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "ColorChoose" + ".fxml"));
            try {
                Parent root = fxmlLoader.load();
                colorController = fxmlLoader.getController();
                colorController.initialize(this, GUIColors, lockColors);
                colorController.createButtons();
                GUIApp.setRoot(root);
            }
            catch(IOException exception) {
                exception.printStackTrace();
            }
        });
        this.challenger = challenger;
        setPlayers(players.getPlayers());
    }

    @Override
    public void showOwnerGod() {
        // Does nothing - CLI specific
    }

    /**
     * Prints the rules' sections
     * @param section The section to print
     * @param tf The textflow where to print the information
     */
    private void recursivePrint(HelpSection section, TextFlow tf) {
        Text text = new Text();
        text.setFill(Color.MAGENTA);
        text.setText(section.getSection().toUpperCase() + "\n");
        Text text2 = new Text();
        if (section.getText() != null)
            text2.setText(section.getText() + "\n");
        Platform.runLater(() -> {
            tf.getChildren().add(text);
            if (text2.getText() != null) {
                tf.getChildren().add(text2);
            }
        });
        if (section.getSubSection() != null) {
            for (HelpSection subsection : section.getSubSection())
              recursivePrint(subsection, tf);
        }
    }

    /**
     * Prints the rules
     * @param tf The textflow where to print the rules
     */
    private void getHelpText(TextFlow tf) {
        for (HelpSection section: getHelpSections()) {
            recursivePrint(section, tf);
        }
    }

    @Override
    public void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Game rules");
        TextFlow textFlow = new TextFlow();
        ScrollPane scrollPane = new ScrollPane();
        getHelpText(textFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(400);
        scrollPane.setPrefHeight(400);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setContent(textFlow);
        alert.getDialogPane().setContent(scrollPane);
        alert.setResizable(false);
        alert.showAndWait();
    }

    @Override
    public void waitForTurn(String opponent) {
        boardController.setInfoText("Wait for your turn while " + opponent + " is playing", true);
    }

    /**
     * Getter for the endgame attribute
     * @return The endgame attribute
     */
    public boolean isEndGame() {
        return endGame;
    }

    @Override
    public void showLose() {
        showLose = true;
        Platform.runLater(() ->  {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You are not able to move/build with any of your workers. You lose", ButtonType.OK);
            alert.setTitle("Attention");
            alert.setHeaderText("Sorry");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    Platform.exit();
                }
            }
        });
    }

    @Override
    public void endGame(String winner) {
        if (winner.equals(getOwnerNick())) {
            isWinner = true;
            Platform.runLater(() -> {
                createEndDialog("Congratulations, you are the winner!\nWould you like to start a new game?", true, false);
            });
        }
        else
        {
            if (!showLose) {
                Platform.runLater(() -> {
                    createEndDialog("You lose but great match!\nWould you like to start a new game?", false, false);
                });
            }
        }
    }

    @Override
    public void disconnected(String player) {
        Platform.runLater(() -> {
            createEndDialog("Player " + player + " disconnected.\nWould you like to start a new game?", false, true);
        });
    }

    /**
     * Creates the dialog after an endgame
     * @param text The text to display
     * @param victory Defines whether the player has won or lost
     * @param disconnected Defines if the game ended due to a disconnection
     */
    private void createEndDialog(String text, boolean victory, boolean disconnected) {
        Stage stage  = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "EndGameDialog" + ".fxml"));
        try {
            Parent root = fxmlLoader.load();
            EndGameDialog dialog = fxmlLoader.getController();
            dialog.setText(text);
            dialog.setBackgroundImage(victory);
            if (disconnected)
                dialog.getBtn_view().setVisible(false);
            dialog.getBtn_ok().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    reset();
                    try {
                        setInit(true);
                        guiView.manageServerState();
                    }
                    catch (EndGameException exception) {
                        //Should never happen here
                        guiView.showInitError("Sorry, the current match is full. Please try again later");
                    }
                    endGame = false;
                    stage.close();
                }
            });
            dialog.getBtn_no().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    stage.close();
                    setQuitting(true);
                    try {
                        guiView.stopThreads();
                    }
                    catch (IOException exception) {
                        showError("Error while trying to close the socket");
                    }
                    catch (InterruptedException exception) {
                        showError("Error while waiting for the thread to join");
                    }
                    endGame = false;
                    Platform.exit();
                }
            });
            dialog.getBtn_view().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    stage.close();
                    boardController.setInfoText("Click anywhere on the board to continue", true);
                    endGame = true;
                }
            });

            Scene scene = new Scene(root, 400, 400);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Setter for the endgame attribute
     * @param endGame The attribute to set
     */
    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    @Override
    public InitMatchMessage startNewMatch() {
        setOwnerNick(welcomeController.getNick());
        Integer nPlayer = welcomeController.getnPlayer();
        boolean withGods = welcomeController.withGods();
        return new InitMatchMessage(getOwnerNick(), nPlayer, withGods);
    }

    @Override
    public JoinMessage joinMatch() {
        setOwnerNick(welcomeController.getNick());
        return new JoinMessage(getOwnerNick());
    }

    @Override
    public void timeout() {
        setHasMoved(false);
        boardController.setUndoAction();
        boardController.setInfoText("The timer is out. Wait for your next turn", true);
    }
}
