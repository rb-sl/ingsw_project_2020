package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.UndoMessage;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * Command used to undo
 */
public class UndoCommand extends Command {
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * Gson object
     */
    private final Gson gson;
    /**
     * Message from the server
     */
    private final UpdateMessage updateMessage;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param updateMessage The message from the server
     */
    public UndoCommand(ViewInterface view, Gson gson, UpdateMessage updateMessage) {
        this.view = view;
        this.gson = gson;
        this.updateMessage = updateMessage;
    }

    @Override
    public void execute() {
        String message;
        MessageEnvelope sendEnvelope;
        UndoMessage undoMessage = new UndoMessage(updateMessage.getPlayer(), false);
        message = gson.toJson(undoMessage, UndoMessage.class);
        sendEnvelope = new MessageEnvelope(MessageType.UNDO, message);
        sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));
    }
}
