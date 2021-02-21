package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.*;

/**
 * Class used to show information based on the server state
 */
public class InitPlayerData extends Command {
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * The message from the server
     */
    private final ServerStateMessage serverState;
    /**
     * The gson
     */
    private final Gson gson;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param serverState The message from the server
     * @param gson The gson object
     */
    public InitPlayerData(ViewInterface view, ServerStateMessage serverState, Gson gson) {
        this.view = view;
        this.serverState = serverState;
        this.gson = gson;
    }

    @Override
    public void execute() {
        String clientMessage = null;
        InitMatchMessage initMessage;
        String ownerNick = null;
        JoinMessage joinMessage;
        String toSend = null;
        if (serverState.isOpen()) {
            if (!serverState.isActive()) {
                initMessage = view.startNewMatch();
                if (initMessage != null) {
                    ownerNick = initMessage.getNickname();
                    clientMessage = gson.toJson(initMessage, InitMatchMessage.class);
                    MessageEnvelope envelope = new MessageEnvelope(MessageType.REGISTER, clientMessage);
                    toSend = gson.toJson(envelope, MessageEnvelope.class);
                }
            } else {
                joinMessage = view.joinMatch();
                if (joinMessage != null) {
                    ownerNick = joinMessage.getNickname();
                    clientMessage = gson.toJson(joinMessage, JoinMessage.class);
                    MessageEnvelope envelope = new MessageEnvelope(MessageType.REGISTER, clientMessage);
                    toSend = gson.toJson(envelope, MessageEnvelope.class);
                }
            }
            if (ownerNick != null) {
                view.waitMessage();
                sendMessage(toSend);
                view.setOwnerNick(ownerNick);
            }
        } else {
            if (serverState.isActive()) {
                view.showError("Sorry, the current match is full. Please try again later");
                view.setQuitting(true);
            }
            else
                view.showInfo("Someone is creating the game, please wait for their to conclude this process");
        }
    }
}
