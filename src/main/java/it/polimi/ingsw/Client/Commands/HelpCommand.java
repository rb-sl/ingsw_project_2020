package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * Command used to show the rules
 */
public class HelpCommand extends Command {
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * Gson object
     */
    private final Gson gson;
    /**
     * The message from the server
     */
    private final UpdateMessage updateMessage;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param updateMessage The update from the server
     */
    public HelpCommand(ViewInterface view, Gson gson, UpdateMessage updateMessage) {
        this.view = view;
        this.gson = gson;
        this.updateMessage = updateMessage;
    }

    @Override
    public void execute() {
        view.showHelp();
    }
}
