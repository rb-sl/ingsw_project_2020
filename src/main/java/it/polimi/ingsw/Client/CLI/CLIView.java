package it.polimi.ingsw.Client.CLI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.*;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.Client.Commands.CommandAllocator;
import it.polimi.ingsw.Client.Commands.EndGameCommand;
import it.polimi.ingsw.Client.Commands.InitPlayerData;
import it.polimi.ingsw.exception.MessageTypeNotFound;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.BoardAttribute;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * CLI version of the view
 */
public class CLIView extends ClientView {
    /**
     * The thread executing the application
     */
    public static Thread t = null;
    /**
     * The gson object
     */
    private final Gson gson = new Gson();
    /**
     * Object used to synchronize the threads
     */
    private final Object obj = new Object();
    /**
     * The message from the server
     */
    private String serverMessage;
    /**
     * The command attribute
     */
    private Command command;
    /**
     * Envelope for deserialization
     */
    private MessageEnvelope envelope;

    /**
     * Constructor for the class
     * @param socket The client's socket
     * @param boardAttribute The board attribute to set
     * @param helpSections The rules sections
     * @param infoCodes The map of infocodes associated to their message
     */
    public CLIView(Socket socket, BoardAttribute boardAttribute, List<HelpSection> helpSections, Map<String, Info> infoCodes) {
        super.socket = socket;
        view = new CLI();
        view.initialize(boardAttribute.nRow, boardAttribute.nCol, boardAttribute.workerSex, helpSections, infoCodes);
    }

    /**
     * Empty constructor
     */
    public CLIView() {
        //In case of error while reading init files or creating socket connection
    }

    @Override
    public void initGame() {
        messageQ = new LinkedList<>();
        viewRun();
    }

    @Override
    public void endRead(boolean restart) {
        clearExceptLast();
        t.interrupt();
    }

    @Override
    public void showInitError(String s) {
        CLI view = new CLI();
        view.showError(s);
    }

    @Override
    public void stopThreads() throws IOException, InterruptedException {
        cleanExit();
    }

    @Override
    public void viewRun() {
        view.showWelcome();
        try {
            serverInput = new ObjectInputStream(socket.getInputStream());
            clientOutput = new PrintWriter(socket.getOutputStream(), true);
            Command.setClientOutput(clientOutput);
            Thread messageThread = messageHandler(false, this);
            messageThread.start();
            pingThread = asyncPing();
            pingThread.start();
            t = houseKeeper();
            t.start();
            synchronized (obj) {
                obj.wait();
            }
        } catch (IOException exception) {
            view.showError("Error while trying to communicate with the server");
        }
        catch (InterruptedException exception) {
            //Should never be thrown
        }
    }

    /**
     * Starts the thread that manages the application
     * @return the new thread
     */
    private Thread houseKeeper() {
        return new Thread(() -> {
            try {
                do {
                    view.reset();
                    view.initBoard();
                    ServerStateMessage serverState;

                    //loop repeated in case of simultaneous connections
                    do {
                        serverMessage = readMessage();
                        envelope = gson.fromJson(serverMessage, MessageEnvelope.class);
                        serverState = gson.fromJson(envelope.getMessage(), ServerStateMessage.class);
                        command = new InitPlayerData(view, serverState, gson);
                        command.execute();
                    } while (!socket.isClosed() && view.getOwnerNick() == null && !serverState.isActive() && !serverState.isOpen() && envelope.getType() != MessageType.ENDGAME);

                    //if view.isQuitting() is true, it means the server is full and the client ends
                    if (!view.isQuitting()) {
                        serverMessage = readMessage();
                        envelope = gson.fromJson(serverMessage, MessageEnvelope.class);
                        //Loop repeated in case of duplicate nickname
                        while (!socket.isClosed() && view.getOwnerNick() != null && envelope.getType() == MessageType.INFO && envelope.getType() != MessageType.ENDGAME) {
                            command = CommandAllocator.allocateInitMessage(view, gson, envelope);
                            command.execute();
                            command = new InitPlayerData(view, serverState, gson);
                            command.execute();
                            serverMessage = readMessage();
                            envelope = gson.fromJson(serverMessage, MessageEnvelope.class);
                        }

                        //Init PlayerData, Gods, StarterPlayer
                        if (view.getOwnerNick() != null && !socket.isClosed()) {
                            //Starts timeout for socket in case the server is no longer reachable (but still connected)
                            socket.setSoTimeout(5000);
                            view.setInit(true);

                            while (!socket.isClosed() && envelope.getType() != MessageType.ENDGAME && view.isInit()) {
                                command = CommandAllocator.allocateInitMessage(view, gson, envelope);
                                command.execute();
                                serverMessage = readMessage();
                                envelope = gson.fromJson(serverMessage, MessageEnvelope.class);
                            }

                            //Game on, if isInitWorker is false then the game can start (reprocessed message); instead it's an ENDGAME
                            //for client disconnection
                            while (!socket.isClosed() && envelope.getType() != MessageType.ENDGAME && !view.isQuitting()) {
                                command = CommandAllocator.allocateMessage(view, gson, envelope);
                                command.execute();
                                serverMessage = readMessage();
                                envelope = gson.fromJson(serverMessage, MessageEnvelope.class);
                            }
                            if (!socket.isClosed() && !view.isQuitting()) {
                                command = new EndGameCommand(view, gson, envelope.getMessage());
                                command.execute();
                            }
                        }
                    }
                } while (!view.isQuitting()); //Until user doesn't want to play anymore (chosen during init of match)
                //If socket has been closed before this point (by messageThread), it means server has disconnected
                //otherwise there was no disconnection and the client can start a new match with the server
                stopThreads();
                synchronized (obj) {
                    obj.notifyAll();
                }
            } catch (MessageTypeNotFound exception) {
                view.showError("Unexpected message from server");
            } catch (IOException ec) {
                view.showError("Error while trying to communicate with the server");
            }
            catch (InterruptedException e) {
                view.showError("Error while waiting for thread to terminate");
            }
        });
    }
}