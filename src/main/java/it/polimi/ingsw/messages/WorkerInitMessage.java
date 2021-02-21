package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.map.SimpleCoordinates;

/**
 * This class represents the workerInit message from view to WorkerInitHandler
 */
public class WorkerInitMessage extends SimpleMessage{
    /**
     * This attribute contains the coordinates of the cell where the worker will be placed
     */
    private final SimpleCoordinates coordinates;
    /**
     * This attribute contains the sex of the worker
     */
    private final char sex;

    /**
     * Constructor for the class
     * @param coordinates the coordinates of the cell where the worker will be placed
     * @param player the nickname of the player that will perform the worker initialization
     * @param sex the sex of the worker
     */
    public WorkerInitMessage(SimpleCoordinates coordinates, String player, char sex) {
        super(player);
        this.coordinates = coordinates;
        this.sex = sex;
    }

    /**
     * Getter for the attribute sex
     * @return the value of the attribute sex
     */
    public char getSex() {
        return sex;
    }

    /**
     * Getter for the attribute coordinates
     * @return the value of the attribute coordinates
     */
    public SimpleCoordinates getCoordinates() {
        return coordinates;
    }
}
