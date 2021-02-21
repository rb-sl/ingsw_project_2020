package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.InfoMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.messages.WorkerInitMessage;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.misc.BiObserver;
import it.polimi.ingsw.view.View;

/**
 * This class performs the worker initialization on the model, according to the message received from the view
 */
public class WorkerInitHandler implements BiObserver<WorkerInitMessage, View> {
    /**
     * This attribute contains the model class instance that will be modified
     */
    private Model model;

    /**
     * Constructor for the class
     * @param model model which will be modified by the class
     */
    public WorkerInitHandler(Model model) {
        this.model = model;
    }
    /**
     * This method performs the worker initialization
     * @param m message with the information needed for the move
     * @param v view from where the message came form
     */
    private synchronized void initWorker(WorkerInitMessage m, View v) {
        Gson gson = new Gson();
        if (!model.getTurnPlayer().equals(m.getPlayer()))
            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(m.getPlayer(),"WAIT")))));
        else
            for(Player p: model.getPlayers()){
                if(p.getNickname().equals(m.getPlayer())) {
                    if (p.getWorkers().size() < model.getBoard().getNWorkers()) {
                        Coordinates c = new Coordinates(m.getCoordinates().getRow(), m.getCoordinates().getColumn(), model.getBoard());
                        char sex = m.getSex();
                        if (!model.initWorker(p, sex, c)) {
                            v.showMessage(gson.toJson(new MessageEnvelope(MessageType.INFO, gson.toJson(new InfoMessage(m.getPlayer(), "OCCUPIED")))));
                            model.sendInitUpdate();
                        }
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
    public void update(WorkerInitMessage m, View v) {
        initWorker(m, v);
    }
}
