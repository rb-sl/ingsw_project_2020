package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.*;

/**
 * Command used to pass
 */
public class PassCommand extends Command {
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
    public PassCommand(ViewInterface view, Gson gson, UpdateMessage updateMessage) {
        this.view = view;
        this.gson = gson;
        this.updateMessage = updateMessage;
    }

    @Override
    public void execute() {
        view.pass();
    }
}