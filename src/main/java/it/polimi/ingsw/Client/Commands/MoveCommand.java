package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.MoveMessage;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * Command used to send a move message
 */
public class MoveCommand extends Command {
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * Gson object
     */
    private final Gson gson;
    /**
     * Update from the server
     */
    private final UpdateMessage update;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param update The message from the server
     */
    public MoveCommand(ViewInterface view, Gson gson, UpdateMessage update) {
        this.view = view;
        this.gson = gson;
        this.update = update;
    }

    @Override
    public void execute() {
        MoveMessage moveMessage = view.move(update.getReachableCells());
        if (moveMessage != null) {
            String clientMessage = gson.toJson(moveMessage, MoveMessage.class);
            MessageEnvelope envelope = new MessageEnvelope(MessageType.MOVE, clientMessage);
            sendMessage(gson.toJson(envelope, MessageEnvelope.class));
        }
    }
}
