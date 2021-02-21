package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.exception.EndGameException;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;
/**
 * This class performs the lose on the model, according to the message received from the view
 */
public class LoseHandler implements BiObserver<SimpleMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;
    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public LoseHandler(Model model) {
        this.model = model;
    }
    /**
     * This method performs the lose
     * @param message message with the information needed for the move
     * @param v view from where the message came form
     */
    public synchronized void removePlayer(SimpleMessage message, View v){
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(message.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else
            for(Player p: model.getPlayers()){
                if(p.getNickname().equals(message.getPlayer())) {
                    try {
                        model.removePlayer(p, v);
                        //v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "LOSE")))));
                    } catch (EndGameException e) {
                        v.endGame(true);
                        v.setEnd(true);
                    }
                    return;
                }
            }
    }

    /**
     * Update as per the observer pattern
     * @param m The message being notified
     * @param v The concerned view
     */
    @Override
    public void update(SimpleMessage m, View v) {
        removePlayer(m, v);
    }
}
