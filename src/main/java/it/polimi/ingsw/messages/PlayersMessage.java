package it.polimi.ingsw.messages;

import java.util.Map;

/**
 * Class representing the message containing the information related to the players and their gods
 */
public class PlayersMessage extends SimpleMessage{
    /**
     * The list of players associated to their gods
     */
    private final Map<String, String> players;

    /**
     * Constructor for the class
     * @param players The map containing players and their gods
     * @param player The player for the turn
     */
    public PlayersMessage(Map<String, String> players, String player) {
        super(player);
        this.players = players;
    }

    /**
     * Getter for the map of players
     * @return The map of players
     */
    public Map<String, String> getPlayers() {
        return players;
    }
}
