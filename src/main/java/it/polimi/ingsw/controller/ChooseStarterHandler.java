package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.messages.StarterMessage;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the starter choose on the model, according to the message received from the view
 */
public class ChooseStarterHandler implements BiObserver<StarterMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;

    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public ChooseStarterHandler(Model model) {
        this.model = model;

    }
    /**
     * This method performs the starter chose
     * @param message message with the information needed for the move
     * @param v view from where the message came form
     */
    private synchronized void choseStarter(StarterMessage message, View v){
        Gson gson = new Gson();
        if(!(model.getTurnPlayer().equals(message.getPlayer())&&model.getChallenger().equals(message.getPlayer())))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else
            for(Player p: model.getPlayers()){
                if(p.getNickname().equals(message.getStarter())) {
                    model.setTurn(model.getPlayers().indexOf(p));
                    model.sendInitUpdate();
                }

            }
    }

    /**
     * Update as per the observer pattern
     * @param m The message being notified
     * @param v The concerned view
     */
    @Override
    public void update(StarterMessage m, View v) {
        choseStarter(m, v);
    }
}
