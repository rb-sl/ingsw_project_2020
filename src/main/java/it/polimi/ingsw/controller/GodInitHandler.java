package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.messages.GodInitMessage;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the god initialization on the model, according to the message received from the view
 */
public class GodInitHandler implements BiObserver<GodInitMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;
    /**
     * This attribute is a counter that tracks how many players have already chosen their god
     */
    private Integer i = 0;
    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public GodInitHandler(Model model) {
        this.model = model;
    }
    /**
     * This method performs the god initializations. When as many players as nplayers have chosen their gods, the powers will be initialized
     * @param init message with the information needed for the move
     * @param v view from where the message came from
     */
    private synchronized void initialize(GodInitMessage init, View v){
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(init.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else{
            for(Player p: model.getPlayers()){
                if(p.getNickname().equals(init.getPlayer())) {
                    model.createGod(p, init.getGodName());
                    i++;
                }
            }
            if(i== model.getnPlayers()&&model.isWithGods())
                model.initGods();
        }
    }

    /**
     * Update as per the observer pattern
     * @param init The message being notified
     * @param v The concerned view
     */
    @Override
    public void update(GodInitMessage init, View v) {
        initialize(init, v);
    }
}
