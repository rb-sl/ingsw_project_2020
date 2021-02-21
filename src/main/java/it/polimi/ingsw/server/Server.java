package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    /**
     * Socket of the server
     */
    private final ServerSocket serverSocket;
    /**
     * Lobby of the server
     */
    private final Lobby lobby;
    /**
     * Formatter for the datetime
     */
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * This class represents the server side of the software. It creates the server, the model, accepts and manages the connections, create the lobby and initializes the game
     * @param port this is the port of the server
     * @throws IOException Input output exception thrown if it can not create the server socket
     */
    public Server(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.lobby = new Lobby();
    }

    /**
     * This method accepts the socket, creates the object that manages the connection and submits it to the executor
     */
    public void run(){
        System.out.println(getDateTime() + "Server started!");
        while(true){
            try {
                Socket newSocket = serverSocket.accept();
                lobby.register(newSocket);
            } catch (IOException e) {
                System.out.println(getDateTime() + "Connection Error: "+ e.getMessage());
            }
        }
    }

    /**
     * Getter for the formatted datetime
     * @return A string containing the datetime
     */
    public static String getDateTime() {
        return formatter.format(new Date(System.currentTimeMillis())) + " > ";
    }
}
