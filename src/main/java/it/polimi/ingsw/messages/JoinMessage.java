package it.polimi.ingsw.messages;

/**
 * Class representing the message from clients wishing to connect to an already existing game
 */
public class JoinMessage {
    /**
     * The player's nickname
     */
    private final String nickname;

    /**
     * Constructor for the class
     * @param nickname The player's nickname
     */
    public JoinMessage(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter for the nickname attribute
     * @return The player's nickname
     */
    public String getNickname() {
        return nickname;
    }
}
