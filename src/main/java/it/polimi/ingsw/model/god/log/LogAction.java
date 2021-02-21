package it.polimi.ingsw.model.god.log;

import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class used to define an action taken by a player
 */
public abstract class LogAction {
    /**
     * The type of the action
     */
    public enum actionType { INIT, MOVE, BUILD, TRIGGER }
    private actionType type;
    /**
     * The coordinates connected to the logged action
     */
    private Coordinates coordinates;
    /**
     * The list of connected subactions
     */
    private List<LogAction> subActions = new ArrayList<>();

    /**
     * Getter for the type
     * @return The action's type
     */
    public actionType getType() {
        return type;
    }

    /**
     * Setter for the type
     * @param type The type to be set
     */
    public void setType(actionType type) {
        this.type = type;
    }

    /**
     * Getter for the coordinates
     * @return The coordinates associated with the action
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Setter for the coordinates
     * @param coordinates The coordinates to set
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Reverts any changes taken during the action
     */
    public abstract void revert();

    /**
     * Reverts all connected subActions, creating a thread for each one
     * @return The list of thread to wait for after the main revert
     */
    public List<Thread> revertSubActions() {
        List<Thread> threadList = new ArrayList<>();

        for (LogAction action: subActions) {
            Thread t = new Thread(action::revert);
            threadList.add(t);
            t.start();
        }

        return threadList;
    }

    /**
     * Adds a new subactions
     * @param subAction The subaction to add
     */
    public void addSubAction(LogAction subAction) {
        subActions.add(subAction);
    }

    /**
     * Sets the list of subActions
     * @param subActions The subActions to be set
     */
    public void setSubActions(List<LogAction> subActions) {
        this.subActions = new ArrayList<>(subActions);
    }

    /**
     * Returns the list of subActions
     * @return The list of subActions
     */
    public List<LogAction> getSubActions() {
        return new ArrayList<>(subActions);
    }
}
