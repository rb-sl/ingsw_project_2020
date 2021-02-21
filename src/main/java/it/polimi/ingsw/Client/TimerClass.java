package it.polimi.ingsw.Client;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.SimpleMessage;

import java.util.TimerTask;

/**
 * Class managing the 5 seconds given to undo after a pass
 */
public class TimerClass extends TimerTask {
    /**
     * Connected view
     */
    private final ViewInterface view;

    /**
     * Constructor for the class
     * @param view The connected view
     */
    public TimerClass(ViewInterface view) {
        this.view = view;
    }

    /**
     * Sends the passMessage if the timer expires
     */
    @Override
    public void run() {
        Gson gson = new Gson();
        view.setCanPass(false);
        view.timeout();
        SimpleMessage passMessage = new SimpleMessage(view.getOwnerNick());
        String message = gson.toJson(passMessage, SimpleMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageType.PASS, message);
        Command.sendMessage(gson.toJson(envelope, MessageEnvelope.class));
    }
}
