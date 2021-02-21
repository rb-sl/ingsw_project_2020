package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.exception.EndGameException;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.messages.MoveMessage;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the move on the model, according to the message received from the view
 */
public class MoveHandler implements BiObserver<MoveMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;

    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public MoveHandler(Model model) {
        this.model = model;
    }

    /**
     * This method performs the move
     * @param message message with the information needed for the move
     * @param v view from where the message came form
     */
    public synchronized void performMove(MoveMessage message, View v)  {
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(message.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else
            for(Player p: model.getPlayers()){
                if(p.getNickname().equals(message.getPlayer())) {
                    try {
                        Coordinates w = new Coordinates(message.getSource().getRow(),message.getSource().getColumn(), model.getBoard());
                        Coordinates d = new Coordinates(message.getDestination().getRow(),message.getDestination().getColumn(), model.getBoard());
                        model.workerMove(w, d);
                    } catch (EndGameException e) {
                        v.endGame(false);
                    }
                }
            }
    }

    /**
     * Update as per the observer pattern
     * @param m The message being notified
     * @param v The concerned view
     */
    @Override
    public void update(MoveMessage m, View v) {
        performMove(m, v);
    }
}
