package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.PingMessage;
import it.polimi.ingsw.misc.Observable;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;

/**
 * This class manages the socket connection between the server and a client
 */
public class ClientSocketConnection extends Observable<MessageEnvelope> implements Runnable {
    /**
     * Client socket
     */
    private final Socket socket;
    /**
     * Server lobby
     */
    private final Lobby lobby;
    /**
     * Scanner for the input
     */
    private Scanner in;
    /**
     * Stream for the output
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Flag that indicates if the connection is active
     */
    private boolean isActive = false;
    /**
     * Flag that indicates if the connection is not alive
     */
    private boolean dead = false;
    /**
     * This flag indicates if, among all the connections, no one has started the deregistration process
     */
    private static boolean free;
    /**
     * Object that is needed to serialize and deserialize the objects
     */
    private final Gson gson = new Gson();
    /**
     * This thread will manage the ping
     */
    private Thread t1;

    /**
     * Flagged indicating if the socket has been stopped
     */
    private boolean stopped = false;

    /**
     * First constructor for the class
     * @param socket client socket
     * @param lobby  server lobby
     */
    public ClientSocketConnection(Socket socket, Lobby lobby) {
        this.socket = socket;
        this.lobby = lobby;
        if (!free)
            this.free = true;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println(Server.getDateTime() + "Creating new socket connection from " + socket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Second constructor for the class
     * @param socket             client socket
     * @param lobby              server lobby
     * @param objectOutputStream outputstream of the socket
     */
    public ClientSocketConnection(Socket socket, Lobby lobby, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.lobby = lobby;
        this.objectOutputStream = objectOutputStream;
        System.out.println(Server.getDateTime() + "Creating new socket connection from " + socket.getRemoteSocketAddress().toString());
    }

    /**
     * Getter for the attribute objectOutputStream
     * @return the value of the attribute objectOutputStream
     */
    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    /**
     * Setter for the attribute dead
     * @param dead the value that will be set
     */
    public synchronized void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Setter for the static flag free
     * @param t the value that will be set
     */
    public static synchronized void setFree(boolean t) {
        free = t;
    }

    /**
     * This method closes the socket
     */
    public synchronized void closeConnection() {
        try {
            socket.close();
            System.out.println(Server.getDateTime() + "Connection with " + socket.getRemoteSocketAddress().toString() + " closed");
        } catch (IOException e) {
            System.out.println("Error when closing socket! " + e.getMessage());
        }
        isActive = false;
    }

    /**
     * Setter for the stopped attribute
     * @param stopped The parameter to set
     */
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    /**
     * This method deregisters the connection
     */
    public synchronized void deRegister() {
        if (isActive) {
            lobby.deRegister(this);
            System.out.println(Server.getDateTime() + "Connection from " + socket.getRemoteSocketAddress().toString() + " unregistered");
        }
    }

    /**
     * This method invokes the server method that manages the disconnection of the client of this connection
     * @param isDisconnected True if the client is disconnected, False otherwise
     * @param isLose True if the client has lost, False otherwise
     */
    public synchronized void checkClients(boolean isDisconnected, boolean isLose) {
        lobby.checkConnections(this, isDisconnected, isLose);
    }

    /**
     * This method sends an object trough the socket
     * @param o the object that will be sent
     */
    public synchronized void sendData(Object o) {
        try {
            objectOutputStream.reset();
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
        } catch (IOException e) {
            // Does nothing in case of exception
        }
    }

    /**
     * Method that interrupts the thread that manages the ping
     */
    public void interruptPing() {
        if(t1 != null)
            t1.interrupt();
    }

    /**
     * Method that creates a thread for async ping
     *
     * @return the created thread
     */
    public Thread asyncPing() {
        Gson gson = new Gson();
        PingMessage pingMessage = new PingMessage("ping");
        return new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    sendData(gson.toJson(new MessageEnvelope(MessageType.PING, gson.toJson(pingMessage, PingMessage.class)), MessageEnvelope.class));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                // Does nothing in case of exception
            }
        });
    }

    /**
     * Getter for the socket
     * @return return the value of the socket
     */
    public synchronized Socket getSocket() {
        return socket;
    }

    /**
     * This method removes the connection from the lobby
     */
    public synchronized void removeFromLobby() {
        System.out.println(Server.getDateTime() + "Connection from " + socket.getRemoteSocketAddress().toString() + " removed from the lobby");
        lobby.removeFromOldSocket(this);
    }

    /**
     * This method registers the connection to the lobby and sends the messages from the socket to the view
     */
    @Override
    public void run() {
        try {
            if (!stopped) {
                in = new Scanner(socket.getInputStream());
                InputStreamReader inn = new InputStreamReader(socket.getInputStream());
                t1 = asyncPing();
                t1.start();
                while (!isActive && !stopped) {
                    Timer t = new Timer();
                    t.schedule(new TimerTimeout(this), 5000);
                    while (!dead && !inn.ready()) {
                        // Waits for a change of state
                    }
                    if (dead && free && !isActive && !stopped) {
                        free = false;
                        System.out.println(Server.getDateTime() + "Connection from " + socket.getRemoteSocketAddress().toString() + " lost");
                        checkClients(true, false);
                    }
                    String input = in.nextLine();
                    MessageEnvelope envelope = gson.fromJson(input, MessageEnvelope.class);
                    if (envelope.getType().equals(MessageType.REGISTER)) {
                        try {
                            lobby.game(envelope, this);
                            isActive = true;
                        } catch (BadConfigurationException e) {
                            // Thrown only if a client tries to use an already taken nickname; ignored
                        }
                    }
                    t.cancel();
                    t.purge();
                }
                while (isActive && lobby.isActive()) {
                    Timer t = new Timer();
                    t.schedule(new TimerTimeout(this), 5000);
                    while (!dead && !inn.ready()) {
                    }
                    if (dead && free && isActive && !stopped) {
                        free = false;
                        System.out.println(Server.getDateTime() + "Connection from " + socket.getRemoteSocketAddress().toString() + " lost");
                        checkClients(true, false);
                    }
                    String message = in.nextLine();
                    MessageEnvelope envelope = gson.fromJson(message, MessageEnvelope.class);
                    if (envelope.getType().equals(MessageType.PING)) {
                    } else if (lobby.isActive()) {
                        notify(envelope);
                    }
                    t.cancel();
                    t.purge();
                }
            }
        } catch (IOException e) {
            // Does nothing in case of exception
        }
    }
}


