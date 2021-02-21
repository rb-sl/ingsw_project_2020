package it.polimi.ingsw.messages;

/**
 * Class used to send a message to the view, ending the game
 */
public class EndGameMessage extends SimpleMessage{
    /**
     * Boolean value indicating if the end game is due to the player winning or to a disconnection
     */
    private final Boolean isWinner;
    /**
     * Constructor for the class
     * @param player the nickname of the player that will perform the action
     * @param isWinner True if the owner is the winner, False otherwise
     */
    public EndGameMessage(String player, Boolean isWinner) {
        super(player);
        this.isWinner = isWinner;
    }

    /**
     * Getter for the isWinner attribute
     * @return True if the end is due to a winner, false if to a disconnection
     */
    public Boolean isWinner() {
        return isWinner;
    }
}
