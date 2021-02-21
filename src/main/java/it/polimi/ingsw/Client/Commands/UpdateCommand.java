package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * Command used to execute server updates
 */
public class UpdateCommand extends Command {
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
    private final String serverMessage;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param serverMessage The message from the server
     */
    public UpdateCommand(ViewInterface view, Gson gson, String serverMessage) {
        this.view = view;
        this.gson = gson;
        this.serverMessage = serverMessage;
    }

    @Override
    public void execute() {
        UpdateMessage updateMessage =  gson.fromJson(serverMessage, UpdateMessage.class);
        view.updateBoard(updateMessage.getBoardUpdate());
        view.showBoard();
        view.showOwnerGod();
        if (updateMessage.getPlayer().equals(view.getOwnerNick())) {
            view.updateMenu(updateMessage);
        }
        else
            view.waitForTurn(updateMessage.getPlayer());
    }
}
