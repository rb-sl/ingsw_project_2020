package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.ClientSocketConnection;


/**
 * This class manages the server side view in the MVC pattern in a distributed version
 */
public class RemoteView extends View  {
    /**
     * The Gson attribute
     */
    private final Gson gson = new Gson().newBuilder().enableComplexMapKeySerialization().create();
    /**
     * The connection to the server
     */
    private final ClientSocketConnection c;

    /**
     * Constructor for the class
     * @param player View owner
     * @param godInitHandler Handler of godInit
     * @param chooseStarterHandler Handler of the starter
     * @param workerInitHandler Handler of the worker initialization
     * @param moveHandler Handler of the move
     * @param buildHandler Handler of the build
     * @param passHandler Handler of the pass
     * @param undoHandler Handler of the undo
     * @param loseHandler Handler of the lose
     * @param c The connection
     */
    public RemoteView(Player player, GodInitHandler godInitHandler, ChooseStarterHandler chooseStarterHandler, WorkerInitHandler workerInitHandler, MoveHandler moveHandler, BuildHandler buildHandler, PassHandler passHandler, UndoHandler undoHandler, LoseHandler loseHandler, ClientSocketConnection c) {
        super(player, godInitHandler, chooseStarterHandler, workerInitHandler, moveHandler, buildHandler, passHandler, undoHandler, loseHandler);
        this.c = c;
        c.addObserver(new MessageRouter(this));
    }

    /**
     * Sends a message to the views
     * @param s The message to be sent
     */
    public void showMessage(String s) {
        c.sendData(s);
    }

    /**
     * Notifies the handler of the lose
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    @Override
    public void loseAction(MessageEnvelope envelope) {
        super.loseAction(envelope);
        if(!isEnd()) {
            c.deRegister();
            c.removeFromLobby();
        }
    }

    /**
     * Ends the game for the view
     */
    @Override
    public void endGame(boolean isLose) {
        c.checkClients(false, isLose);
    }

    /**
     * Update as per the observer pattern
     * @param t The type of the message
     * @param m The message being notified
     */
    @Override
    public void update(MessageType t, SimpleMessage m) {

        if(!t.equals(MessageType.UPDATE))
            c.sendData(gson.toJson(new MessageEnvelope(t, gson.toJson(m))));
        else{
            if(m.getPlayer().equals(getPlayer().getNickname()))
                c.sendData(gson.toJson(new MessageEnvelope(t, gson.toJson(m))));
            else{
                c.sendData(gson.toJson(new MessageEnvelope(t, gson.toJson(m.makeLight()))));
            }
        }
    }
}
