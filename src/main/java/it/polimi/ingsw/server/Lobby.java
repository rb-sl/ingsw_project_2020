package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.RemoteView;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Lobby {
    /**
     * Number of players that will play the game
     */
    private Integer nplayer;
    /**
     * Map with nickname and linked connections that have been accepted for the game
     */
    private final Map<String, ClientSocketConnection> connectionAccepted = new HashMap<>();
    /**
     * List with all the connections
     */
    private final List<ClientSocketConnection> connections;
    /**
     * List with players that will play the game
     */
    private List<Player> players;
    /**
     * Flag that indicates if the game will use the god cards
     */
    private boolean withGods;
    /**
     * Flag that indicates if the lobby is active
     */
    private boolean isActive = false;
    /**
     * Flag that indicates if the lobby is open to other clients
     */
    private boolean open = true;
    /**
     * Gson object
     */
    private final Gson gson = new Gson();
    /**
     * Object used to lock the threads
     */
    private static final Object lock = new Object();
    /**
     * Executor used for thread pools
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();
    /**
     * List with the current accepted sockets
     */
    private final List<Socket> sockets;
    /**
     * List with the old accepted sockets
     */
    private final List<Socket> oldSockets;

    /**
     * Constructor for the class
     */
    public Lobby(){
        this.nplayer = 2;
        this.players = new ArrayList<>();
        this.sockets = new ArrayList<>();
        this.oldSockets = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    /**
     * This method sends to the alive connections the info that someone have disconnected from the server and the game ends, then invokes the method that resets the server
     * @param p the dead connection
     * @param isLose True if the client has lost, False otherwise
     * @param isDisconnected True if the client has disconnected, False otherwise
     */
    public synchronized void checkConnections(ClientSocketConnection p, boolean isDisconnected, boolean isLose) {
        Iterator<String> iterator = connectionAccepted.keySet().iterator();
        String k = null;
        for(ClientSocketConnection cl1: connections) {
            if (cl1 != p) {
                cl1.setStopped(true);
            }
        }
        if(connectionAccepted.containsValue(p) || connectionAccepted.size() == 0) {
            while (iterator.hasNext()) {
                k = iterator.next();
                if (connectionAccepted.get(k).equals(p)) {
                    connectionAccepted.remove(k);
                    break;
                }
            }

            oldSockets.clear();
            p.deRegister();
            if(isLose)
                p.removeFromLobby();
            if (isDisconnected) {
                sockets.remove(p.getSocket());
                oldSockets.remove(p.getSocket());
                p.closeConnection();
                connections.remove(p);
                if(connectionAccepted.size() == 0){
                    for (ClientSocketConnection c: connections) {
                        if(k == null)
                            k = "who was creating the game";
                        c.sendData(gson.toJson(new MessageEnvelope(MessageType.ENDGAME, gson.toJson(new EndGameMessage(k, false)))));
                    }
                }
                iterator = connectionAccepted.keySet().iterator();
                while (iterator.hasNext()) {
                    connectionAccepted.get(iterator.next()).sendData(gson.toJson(new MessageEnvelope(MessageType.ENDGAME, gson.toJson(new EndGameMessage(k, false)))));
                }
            }
            endGame();
        }else if(!connectionAccepted.containsValue(p)){
            oldSockets.clear();
            if (isDisconnected) {
                sockets.remove(p.getSocket());
                oldSockets.remove(p.getSocket());
                p.closeConnection();
                connections.remove(p);
                p.setFree(true);
            }
        }

    }

    /**
     * This method resets the server and ends it
     */
    public synchronized void endGame(){
        List<ClientSocketConnection> l = new ArrayList<>(connectionAccepted.values());
        if(l.size() == 0){
            for(ClientSocketConnection cl1: connections){
                Socket s = cl1.getSocket();
                sockets.remove(s);
                oldSockets.add(s);
            }
        }else {
            for (ClientSocketConnection clientSocketConnection : l) {
                    clientSocketConnection.deRegister();
            }
        }
        setActive(false);
        open = true;
        this.nplayer = 2;
        ClientSocketConnection.setFree(true);
        System.out.println(Server.getDateTime() + "Current game ended - The lobby has been reset");
        for (Socket s: oldSockets) {
            register(s);
        }
    }

    /**
     * This method unsubscribes a connection
     * @param c the connection that has to be unsubscribed
     */
    public synchronized void deRegister(ClientSocketConnection c) {
        connectionAccepted.keySet().removeIf(s -> connectionAccepted.get(s) == c);
        sockets.removeIf(s -> s.equals(c.getSocket()));
        oldSockets.add(c.getSocket());
    }

    /**
     * This method removes the connection c from the lobby
     * @param c connection that will be removed
     */
    public synchronized void removeFromOldSocket(ClientSocketConnection c){
        connections.remove(c);
        oldSockets.remove(c.getSocket());
    }

    /**
     * This method is a getter for the attribute active
     * @return the value of attribute active
     */
    public boolean isActive() {
        synchronized (lock){
            return isActive;
        }
    }

    /**
     * Setter for the attribute active
     * @param active the value that will be set
     */
    public void setActive(boolean active) {
        synchronized (lock){
            isActive = active;
        }
    }


    /**
     * This method manages the lobby. It registers the connections and when there are as many connection as nplayer it starts and initializes the game
     * @param envelope this message is the command that arrive from the client
     * @param c this is the connection of the client that sent the command
     * @throws BadConfigurationException Thrown when the Nickname specified by the player is duplicated
     */
    public synchronized void game(MessageEnvelope envelope, ClientSocketConnection c) throws BadConfigurationException {
        if(connectionAccepted.size() == 0){
            InitMatchMessage s = gson.fromJson(envelope.getMessage(), InitMatchMessage.class);
            connectionAccepted.put(s.getNickname(), c);
            this.nplayer = s.getnPlayers();
            this.isActive = true;
            this.withGods = s.getGods();
            if (sockets.size() > 1) {
                int i = 1;
                for (ClientSocketConnection cl : connections) {
                    if (cl != c && i < nplayer) {
                        cl.sendData(gson.toJson(new MessageEnvelope(MessageType.SERVERSTATE, gson.toJson(new ServerStateMessage(true, this.isActive)))));
                        i++;
                    }else if(i >= nplayer-1 && cl != c)
                        cl.sendData(gson.toJson(new MessageEnvelope(MessageType.SERVERSTATE, gson.toJson(new ServerStateMessage(false, this.isActive)))));
                }
            }
        }else{
            JoinMessage b = gson.fromJson(envelope.getMessage(), JoinMessage.class);
            if(connectionAccepted.containsKey(b.getNickname())) {
                c.sendData(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(b.getNickname(), "NICKNAME")))));
                throw new BadConfigurationException("NickName already taken");
            }
            else {
                connectionAccepted.put(b.getNickname(), c);
            }
        }
        if(connectionAccepted.size() == nplayer){
            open = false;
            List<ClientSocketConnection> conn = new ArrayList<>(connections);
            for (ClientSocketConnection c1: conn) {
                if (!connectionAccepted.containsValue(c1)) {
                    c1.sendData(gson.toJson(new MessageEnvelope(MessageType.ENDGAME, gson.toJson(new EndGameMessage("", false)))));
                    sockets.remove(c1.getSocket());
                    connections.remove(c1);
                    c1.closeConnection();
                }
            }
            players = new ArrayList<>();
            List<String> names = new ArrayList<>(connectionAccepted.keySet());
            Model model = new Model(nplayer, withGods);
            try {
                model.initBoard();
                System.out.println(Server.getDateTime() + "Starting a new " + nplayer + "-players game " + (withGods ? "with" : "without") + " gods");
            } catch (BadConfigurationException e) {
                System.out.println(e.getMessage());
                endGame();
            }
            System.out.print("\tPlayers: ");
            for(int i = 0; i < nplayer; i++) {
                players.add(new Player(names.get(i), model.getBoard()));
                System.out.print(names.get(i) + " ");
            }
            model.addPlayers(players);
            GodInitHandler godInitHandler = new GodInitHandler(model);
            ChooseStarterHandler chooseStarterHandler = new ChooseStarterHandler(model);
            WorkerInitHandler workerInitHandler = new WorkerInitHandler(model);
            MoveHandler moveHandler = new MoveHandler(model);
            BuildHandler buildHandler = new BuildHandler(model);
            PassHandler passHandler = new PassHandler(model);
            UndoHandler undoHandler = new UndoHandler(model);
            LoseHandler loseHandler = new LoseHandler(model);
            List<View> viewList = new ArrayList<>();
            for(int i = 0; i < nplayer; i++) {
                viewList.add(new RemoteView(players.get(i), godInitHandler, chooseStarterHandler, workerInitHandler, moveHandler, buildHandler, passHandler, undoHandler, loseHandler, connectionAccepted.get(names.get(i))));
                model.addObserver(viewList.get(i));
            }
            model.setChallenger();
            System.out.println("\n\tChallenger: " + model.getChallenger());
            if(this.withGods)
                model.getGodList();
            else{
                model.atheistCreation();
            }
        }
    }

    /**
     * Method that registers the socket to the lobby
     * @param newSocket socket that will be registered
     */
    public synchronized void register(Socket newSocket){
        try {
            ClientSocketConnection socketConnection = null;
            if(connectionAccepted.size()<nplayer && open) {

                String s;
                if (!this.isActive && sockets.size() > 0) {
                    s = gson.toJson(new ServerStateMessage(false, this.isActive));
                } else {
                    s = gson.toJson(new ServerStateMessage(true, this.isActive));
                }
                sockets.add(newSocket);
                for (ClientSocketConnection c : connections) {
                    if (c.getSocket().equals(newSocket)) {
                        c.interruptPing();
                        socketConnection = new ClientSocketConnection(newSocket, this, c.getObjectOutputStream());
                    }
                }
                if (connections.stream().map(x -> !x.getSocket().equals(newSocket)).reduce(true, (a, b) -> a && b))
                    socketConnection = new ClientSocketConnection(newSocket, this);
                connections.removeIf(clientSocketConnection -> clientSocketConnection.getSocket().equals(newSocket));
                connections.add(socketConnection);
                executor.submit(socketConnection);
                socketConnection.sendData(gson.toJson(new MessageEnvelope(MessageType.SERVERSTATE, s)));
            }

            else{
                ObjectOutputStream out = new ObjectOutputStream(newSocket.getOutputStream());
                ServerStateMessage m = new ServerStateMessage(false, this.isActive);
                out.reset();
                out.writeObject(gson.toJson(new MessageEnvelope(MessageType.SERVERSTATE,gson.toJson(m))));
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Connection Error! " + e.getMessage());
        }
    }
}
