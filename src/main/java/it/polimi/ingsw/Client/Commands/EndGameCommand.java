package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.EndGameMessage;

/**
 * Command used to send an endgame message
 */
public class EndGameCommand extends Command {
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
    public EndGameCommand(ViewInterface view, Gson gson, String serverMessage) {
        this.serverMessage = serverMessage;
        this.gson = gson;
        this.view = view;
    }

    @Override
    public void execute() {
        EndGameMessage endGame =  gson.fromJson(serverMessage, EndGameMessage.class);
        //endgame player is empty only when someone is creating the game and when the client sends the number of player to
        //the server, the server accepts clients in order of their connection and the client in excess needs to terminate
        if (endGame.isWinner() != null) {
            if (endGame.isWinner()) {
                view.showBoard();
                view.endGame(endGame.getPlayer());
            } else {
                if (endGame.getPlayer().isEmpty()) {
                    view.showInfo("The game is full, please try again later");
                }
                else
                    view.disconnected(endGame.getPlayer());
            }

        }
        else
        {
            //==null only when a dummy endgame is created by the client due to server disconnection
            view.showError("Error while trying to communicate with the server.\nPress ok to continue");
        }
    }
}
