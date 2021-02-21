package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the pass on the model, according to the message received from the view
 */
public class PassHandler implements BiObserver<SimpleMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;
    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public PassHandler(Model model) {
        this.model = model;
    }
    /**
     * This method perform the pass
     * @param m message with the information needed for the move
     * @param v view from where the message came form
     */
    public synchronized void passTurn(SimpleMessage m, View v){
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(m.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else
            model.endTurn();
    }

    /**
     * Update as per the observer pattern
     * @param m The message being notified
     * @param v The concerned view
     */
    @Override
    public void update(SimpleMessage m, View v) {
        passTurn(m, v);
    }
}
