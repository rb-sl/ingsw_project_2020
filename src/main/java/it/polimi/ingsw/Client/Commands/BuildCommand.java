package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.*;

/**
 * Class  used to send a build message
 */
public class BuildCommand extends Command {
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * The gson object
     */
    private final Gson gson;
    /**
     * The update from the server
     */
    private final UpdateMessage update;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param update The update from the server
     */
    public BuildCommand(ViewInterface view, Gson gson, UpdateMessage update) {
        this.view = view;
        this.gson = gson;
        this.update = update;
    }

    @Override
    public void execute() {
        BuildMessage buildMessage = view.build(update.getBuildableCells());
        if (buildMessage != null) {
            String clientMessage = gson.toJson(buildMessage, BuildMessage.class);
            MessageEnvelope envelope = new MessageEnvelope(MessageType.BUILD, clientMessage);
            sendMessage(gson.toJson(envelope, MessageEnvelope.class));
        }
    }
}
