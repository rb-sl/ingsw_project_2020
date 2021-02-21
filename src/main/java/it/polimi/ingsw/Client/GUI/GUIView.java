package it.polimi.ingsw.Client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.*;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.Client.Commands.CommandAllocator;
import it.polimi.ingsw.exception.EndGameException;
import it.polimi.ingsw.exception.MessageTypeNotFound;
import it.polimi.ingsw.messages.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GUI version of the view
 */
public class GUIView extends ClientView {
    /**
     * State message of the server
     */
    private ServerStateMessage serverState;
    /**
     * Javafx controller for the welcome screen
     */
    private WelcomeController welcomeController;
    /**
     * Flag stating if the application is in its start phase
     */
    private boolean isStart;
    /**
     * Thread used to handle the connection and start of the game
     */
    private Thread actionHandler;

    /**
     * Constructor for the class
     * @param socket The client's socket
     * @param nRow The number of rows in the board
     * @param nCol The number of columns in the board
     * @param welcomeController The welcome controller
     * @param workerSex The list of worker identifiers
     * @param helpSections The list of rile sections
     * @param infoCodes The map of infocodes
     */
    public GUIView(Socket socket, int nRow, int nCol, WelcomeController welcomeController, List<Character> workerSex, List<HelpSection> helpSections, Map<String, Info> infoCodes) {
        view = new GUI(this);
        view.initialize(nRow, nCol, workerSex, helpSections, infoCodes);
        super.socket = socket;
        this.welcomeController = welcomeController;
        isStart = true;
    }

    /**
     * Default constructor
     */
    public GUIView() {
        super();
    }

    @Override
    public void initGame() {
        messageQ = new LinkedList<>();
    }

    @Override
    public void endRead(boolean restart) {
        //Used only in CLIView to end the thread when an error occurs
    }

    @Override
    public void showInitError(String s) {
        view.showError(s);
    }

    /**
     * Initializes the welcome screen
     * @param join Indicates if the screen has to be a create or join match
     */
    private void initScene(boolean join) {
        WelcomeController welcomeController;
        try {
            welcomeController = GUIApp.loadInit();
            welcomeController.initialize(view, this);
            view.setWelcomeController(welcomeController);
            if (join)
                welcomeController.hideForJoin();
        } catch (IOException ex) {
            showInitError("Error while loading the interface");
        }
    }

    /**
     * Handles the possible combinations of server states
     * @throws EndGameException If the game cannot start
     */
    public void manageServerState() throws EndGameException {
        if (serverState.isActive() && !serverState.isOpen()) {
            throw new EndGameException("");
        }
        else {
            if (serverState.isOpen() && serverState.isActive()) {
                //When GUIView is created from GUIApp, the scene has already been set; when isStart is false the game ended and restarted
                if (isStart) {
                    welcomeController.initialize(view, this);
                    view.setWelcomeController(welcomeController);
                    welcomeController.hideForJoin();
                    isStart = false;
                }
                else
                    initScene(true);
            }
            else {
                if (isStart) {
                    welcomeController.initialize(view, this);
                    view.setWelcomeController(welcomeController);
                    isStart = false;
                } else
                    initScene(false);
            }
        }
    }

    /**
     * Sets and starts the action handler thread
     */
    public void startActionHandler() {
        actionHandler = actionHandler();
        actionHandler.start();
    }

    /**
     * Method to stop the threads
     * @throws IOException As per default behaviour
     * @throws InterruptedException As per default behaviour
     */
    public void stopThreads() throws IOException, InterruptedException {
        cleanExit();
        actionHandler.interrupt();
    }

    /**
     * Gets and decodes the server state
     * @throws EndGameException If the game cannot start
     */
    public void readServerState() throws EndGameException {
        Gson gson = new Gson();
        String message = readMessage();
        MessageEnvelope envelope = gson.fromJson(message, MessageEnvelope.class);
        //Should be always true
        if (envelope.getType().equals(MessageType.SERVERSTATE)) {
            serverState = gson.fromJson(envelope.getMessage(), ServerStateMessage.class);
            manageServerState();
        }
    }

    @Override
    public void viewRun() {
        Thread messageHandler;
        view.setInit(true);
        view.initBoard();

        try {
            serverInput = new ObjectInputStream(super.socket.getInputStream());
            clientOutput = new PrintWriter(super.socket.getOutputStream(), true);
            messageHandler = messageHandler(true, this);
            messageHandler.start();

            pingThread = asyncPing();
            Command.setClientOutput(clientOutput);
            pingThread.start();

            socket.setSoTimeout(5000);
        } catch (IOException exception) {
                ButtonType close = new ButtonType("Close all", ButtonBar.ButtonData.CANCEL_CLOSE);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage(), close);
                    alert.setHeaderText("An error occurred");
                    alert.setTitle("Error");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == close) {
                            Platform.exit();
                        }
                    }
                });
        }
    }

    /**
     * Getter for the server state message
     * @return The server's state
     */
    public ServerStateMessage getServerState() {
        return serverState;
    }

    /**
     * Creates the action handler thread
     * @return The newly created thread
     */
    public Thread actionHandler() {
        Gson gson = new Gson();
        return new Thread(() -> {
            MessageEnvelope envelope = null;
            try {
                do {
                    Command command;
                    String message = readMessage();
                    if (message != null) {
                        envelope = gson.fromJson(message, MessageEnvelope.class);
                        if (envelope.getType().equals(MessageType.INFO)) {
                            InfoMessage info = gson.fromJson(envelope.getMessage(), InfoMessage.class);
                            if (info.getInformation().equals("NICKNAME")) {
                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(GUIApp.pathToFxml + "JoinMatchView" + ".fxml"));
                                        Parent root = fxmlLoader.load();
                                        WelcomeController controller = fxmlLoader.getController();
                                        controller.initialize(view, this);
                                        view.setWelcomeController(controller);
                                        GUIApp.setRoot(root);
                                    } catch (IOException exception) {
                                        exception.printStackTrace();
                                    }
                                });
                            }
                        }
                        if (envelope.getType() != MessageType.SERVERSTATE) {
                            if (view.isInit()) {
                                command = CommandAllocator.allocateInitMessage(view, gson, envelope);
                            } else {
                                command = CommandAllocator.allocateMessage(view, gson, envelope);
                            }
                            command.execute();
                        } else {
                            serverState = gson.fromJson(envelope.getMessage(), ServerStateMessage.class);
                            if (view.isInit()) {
                                try {
                                    manageServerState();
                                } catch (EndGameException exception) {
                                    showInitError("Sorry, the current match is full. Please try again later");
                                }
                            }
                        }
                    }
                } while (!socket.isClosed() && !view.isQuitting());
            } catch (MessageTypeNotFound e) {
                //Should never happen
            }
        });
    }
}
