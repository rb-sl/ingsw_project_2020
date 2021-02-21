package it.polimi.ingsw.messages;

/**
 * This class represents the godInit message from view to GodInitHandler
 */
public class GodInitMessage extends SimpleMessage{
    /**
     * This attribute contains the name of the chosen god
     */
    private final String godName;

    /**
     * Constructor for the class
     * @param godName name of the chosen god
     * @param player the nickname of the player that will perform the action
     */
    public GodInitMessage(String godName, String player) {
        super(player);
        this.godName = godName;
    }

    /**
     * Getter for the attribute godName
     * @return the value of the attribute godName
     */
    public String getGodName() {
        return godName;
    }
}
