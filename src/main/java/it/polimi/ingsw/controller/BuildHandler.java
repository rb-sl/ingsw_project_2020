package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.BuildMessage;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the build on the model, according to the message received from the view
 */
public class BuildHandler implements BiObserver<BuildMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;

    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public BuildHandler(Model model) {
        this.model = model;

    }
    /**
     * this method performs the build
     * @param message message with the information needed for the move
     * @param v view from where the message came form
     */
    public synchronized void performBuild(BuildMessage message, View v){
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(message.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(model.getTurnPlayer(), "WAIT")))));
        else
            for(Player p: model.getPlayers()){
                if(p.getNickname().equals(message.getPlayer())) {
                    Coordinates w = new Coordinates(message.getSource().getRow(),message.getSource().getColumn(), model.getBoard());
                    Coordinates b = new Coordinates(message.getDestination().getRow(),message.getDestination().getColumn(), model.getBoard());
                    model.workerBuild(w, b, message.getLevel());
                }
            }
    }

    /**
     * Update as per the observer pattern
     * @param m The message being notified
     * @param v The concerned view
     */
    @Override
    public void update(BuildMessage m, View v) {
        performBuild(m, v);
    }
}
