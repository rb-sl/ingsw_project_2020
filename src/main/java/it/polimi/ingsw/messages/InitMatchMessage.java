package it.polimi.ingsw.messages;

/**
 * Class representing the message from the client who is creating the game
 */
public class InitMatchMessage {
    /**
     * The player's nickname
     */
    private final String nickname;
    /**
     * The requested number of players
     */
    private final Integer nPlayers;
    /**
     * Attribute representing whether the game uses god cards
     */
    private final Boolean gods;

    /**
     * Constructor for the class
     * @param nickname The player's nickname
     * @param nPlayers The number of players for the game
     * @param gods Specifies if the game uses god cards
     */
    public InitMatchMessage(String nickname, Integer nPlayers, Boolean gods) {
        this.nickname = nickname;
        this.nPlayers = nPlayers;
        this.gods = gods;
    }

    /**
     * Getter for the nickname attribute
     * @return The player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Getter for the nPlayers attribute
     * @return The number of players
     */
    public Integer getnPlayers() {
        return nPlayers;
    }

    /**
     * Getter for the gods attribute
     * @return the gods attribute
     */
    public Boolean getGods() {
        return gods;
    }
}
