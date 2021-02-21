package it.polimi.ingsw.Client;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.messages.EndGameMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.PingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Class used to handle the client side of the application
 */
public abstract class ClientView {
    /**
     * Queue used in the consumer/producer pattern for reading the user input
     */
    public LinkedList<String> messageQ;
    /**
     * The view used by the client
     */
    public ViewInterface view;
    /**
     * The client's socket
     */
    public Socket socket;
    /**
     * Input from the server
     */
    public ObjectInputStream serverInput;
    /**
     * Output to the client
     */
    public PrintWriter clientOutput;
    /**
     * Thread used to keep the heartbeat with the server
     */
    public Thread pingThread;
    /**
     * Flag indicating if the current view is a gui
     */
    private boolean isGUI = false;

    /**
     * Method that executes the tasks requested as a view
     */
    public abstract void viewRun();

    /**
     * Game initializer
     */
    public abstract void initGame();

    /**
     * Ends the read of server messages if another player disconnected
     * @param restart Flag indicating whether to restart the reading
     */
    public abstract void endRead(boolean restart);

    /**
     * Shows an error on start
     * @param s The error to display
     */
    public abstract void showInitError(String s);

    /**
     * Stops the threads to end the application
     * @throws IOException As per default behaviour
     * @throws InterruptedException As per default behaviour
     */
    public abstract void stopThreads() throws IOException, InterruptedException;

    //if isGUI == true the condition about ENDGAME is not considered.

    /**
     * Method to handle the messages from the server; starts a dedicated thread
     * @param isGUI Specifies if the view is a gui
     * @param clientView The connected clientView
     * @return The reader thread
     */
    public Thread messageHandler(boolean isGUI, ClientView clientView) {
        Gson gson = new Gson();
        this.isGUI = isGUI;
        return new Thread(() -> {
            MessageEnvelope envelope = null;
            try {
                do {
                    String inputMessage = (String)serverInput.readObject();
                    envelope = gson.fromJson(inputMessage, MessageEnvelope.class);
                    if (envelope.getType() != MessageType.PING) {
                        synchronized (messageQ) {
                            messageQ.add(inputMessage);
                            messageQ.notifyAll();
                        }
                        if (!isGUI && envelope.getType().equals(MessageType.ENDGAME)) {
                            EndGameMessage endGameMessage = gson.fromJson(envelope.getMessage(), EndGameMessage.class);
                            if (!endGameMessage.isWinner() && !endGameMessage.getPlayer().isEmpty()) {
                                clientView.endRead(true);
                            }
                        }
                    }
                } while (!socket.isClosed() && !view.isQuitting());
            } catch (IOException | ClassNotFoundException e) {
                // If this branch is executed when the user is not really quitting, there was a server disconnection.
                // If the user is quitting, this thread is terminated (catches the exception because of the socket closing),
                // due to viewRun socket.close() call (should do nothing)
                if (!view.isQuitting()) {
                    // Line needed to avoid another do..while iteration in viewRun()
                    view.setQuitting(true);
                    EndGameMessage message = new EndGameMessage("", null);
                    MessageEnvelope finalEnvelope = new MessageEnvelope(MessageType.ENDGAME, gson.toJson(message, EndGameMessage.class));
                    try {
                        serverInput.close();
                        clientOutput.close();
                        socket.close();
                        synchronized (messageQ) {
                            messageQ.add(gson.toJson(finalEnvelope, MessageEnvelope.class));
                            messageQ.notifyAll();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (!isGUI) {
                        clientView.endRead(false);
                    }
                }
            }
        });
    }

    /**
     * Closes correctly the application
     * @throws IOException As per default methods behaviour
     * @throws InterruptedException As per default methods behaviour
     */
    protected void cleanExit() throws IOException, InterruptedException {
        if (!socket.isClosed()) {
            serverInput.close();
            clientOutput.close();
            socket.close();
            if (pingThread.isAlive())
                pingThread.join();
        } else {
            view.showError("Match terminated due to server disconnection");
        }
    }

    /**
     * Consumer in the producer/consumer pattern
     * @return The message read from the queue
     */
    protected String readMessage() {
        synchronized (messageQ) {
            while (messageQ.isEmpty()) {
                try {
                    messageQ.wait();
                } catch (InterruptedException e) {
                    if (isGUI)
                        return null;
                }
            }
            return messageQ.poll();
        }
    }

    /**
     * Discards all non-endgame messages from the server
     */
    protected void clearExceptLast() {
        synchronized (messageQ) {
            if (messageQ.size() > 1) {
                String latest = messageQ.getLast();
                messageQ.removeIf(el -> !el.equals(latest));
            }
        }
    }

    /**
     * Starts a thread to ping the server
     * @return The new thread
     */
    public Thread asyncPing() {
        Gson gson = new Gson();
        PingMessage pingMessage = new PingMessage("ping");
        return new Thread(() -> {
            try {
                while (!socket.isClosed() && !Command.checkError()) {
                    Command.sendMessage(gson.toJson(new MessageEnvelope(MessageType.PING, gson.toJson(pingMessage, PingMessage.class)), MessageEnvelope.class));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                view.showError("Error ping thread interrupted");
            }
        });
    }
}
