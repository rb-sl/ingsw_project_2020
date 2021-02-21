package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.InfoMessage;

/**
 * Command used to show an info message
 */
public class InfoCommand extends Command {
    /**
     * The message received from the server
     */
    private final String serverMessage;
    /**
     * The gson object
     */
    private final Gson gson;
    /**
     * The connected view
     */
    private final ViewInterface view;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param serverMessage The message received from the server
     */
    public InfoCommand(ViewInterface view, Gson gson, String serverMessage) {
        this.serverMessage = serverMessage;
        this.gson = gson;
        this.view = view;
    }

    @Override
    public void execute() {
        InfoMessage infoMessage;
        infoMessage = gson.fromJson(serverMessage, InfoMessage.class);
        view.showInfo(infoMessage.getInformation());
    }
}
