package it.polimi.ingsw.messages;

import java.util.List;

/**
 * Class containing the list of gods that can be still chosen from the players
 */
public class GodListMessage extends SimpleMessage{
    /**
     * The list of choosable gods
     */
    private final List<ChosenGodMessage> godList;

    /**
     * Constructor for the class
     * @param activePlayer The player who can choose the god
     * @param godList The list of choosable gods
     */
    public GodListMessage(String activePlayer, List<ChosenGodMessage> godList) {
        super(activePlayer);
        this.godList = godList;
    }

    /**
     * Getter for the godList attribute
     * @return The list of gods
     */
    public List<ChosenGodMessage> getGodList() {
        return godList;
    }
}
