package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.PlayersMessage;

/**
 * Command used to print the list of players
 */
public class InitChallenger extends Command {
    /**
     * Gson object
     */
    private final Gson gson;
    /**
     * Message from the server
     */
    private final String serverMessage;
    /**
     * Connected view
     */
    private final ViewInterface view;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param serverMessage The message from the server
     */
    public InitChallenger(ViewInterface view, Gson gson, String serverMessage) {
        this.gson = gson;
        this.serverMessage = serverMessage;
        this.view = view;
    }

    @Override
    public void execute() {
        PlayersMessage players;
        String challenger;
        players = gson.fromJson(serverMessage, PlayersMessage.class);
        challenger = players.getPlayer();
        view.showOpponents(players, challenger);
    }
}
