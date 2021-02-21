package it.polimi.ingsw.messages;

/**
 * This class represents the undo message from view to UndoHandler
 */
public class UndoMessage extends SimpleMessage {
    /**
     * This attribute is a flag that indicates if the undo is a simple undo or a undoAll
     */
    private final boolean all;

    /**
     * Constructor for the class
     * @param player the nickname of the player that will perform the action
     * @param all the flag that indicates if the undo is a simple undo or a undoAll
     */
    public UndoMessage(String player, boolean all) {
        super(player);
        this.all = all;
    }

    /**
     * Getter for the attribute all
     * @return value of the attribute all
     */
    public boolean isAll() {
        return all;
    }
}
