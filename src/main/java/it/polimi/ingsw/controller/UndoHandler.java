package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.UndoMessage;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the undo on the model, according to the message received from the view
 */
public class UndoHandler implements BiObserver<UndoMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;
    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public UndoHandler(Model model) {
        this.model = model;
    }
    /**
     * This method performs the undo
     * @param m message with the information needed for the move
     * @param v view from where the message came form
     */
    public synchronized void performUndo(UndoMessage m, View v) {
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(m.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else {
            for (Player p : model.getPlayers()) {
                if (p.getNickname().equals(m.getPlayer())) {
                    if(m.isAll())
                        model.undoAll(p);
                    else
                        model.undo(p);
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
    public void update(UndoMessage m, View v) {
        performUndo(m, v);
    }
}