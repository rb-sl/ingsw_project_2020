package it.polimi.ingsw.model.god.log;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodEventManager;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class used to log any actions taken by the player during a turn and undoing them
 */
public class LogTurn {
    /**
     * Enum used to define if the event is main or a consequence happening before or after the main event
     */
    public enum order {BEFORE, MAIN, AFTER}
    /**
     * Stack of actions, used for the LIFO logic
     */
    private final LinkedList<LogAction> actions;
    /**
     * Buffer for sub actions happening before a main event
     */
    private final List<LogAction> subActionBuffer;

    /**
     * Constructor for the class
     */
    public LogTurn() {
        actions = new LinkedList<>();
        subActionBuffer = new ArrayList<>();
    }

    /**
     * Creates and adds a new place worker action, logging the position
     * @param coordinates The worker's position
     */
    public void addPlaceWorker(Coordinates coordinates) {
        actions.push(new LogPlaceWorker(coordinates));
    }

    /**
     * Creates and adds a new movement action, logging the worker's previous coordinates
     * @param order The type of event
     * @param worker The worker which changed position
     * @param previousCoordinates The coordinates the worker left
     */
    public void addMove(order order, Worker worker, Coordinates previousCoordinates) {
        switch(order) {
            case MAIN:
                actions.push(new LogMove(worker, previousCoordinates, getBuffer()));
                break;
            case BEFORE:
                subActionBuffer.add(new LogMove(worker, previousCoordinates));
                break;
            case AFTER:
                actions.peek().addSubAction(new LogMove(worker, previousCoordinates));
        }
    }

    /**
     * Creates and adds a new build action, logging the destination coordinates
     * @param order The type of event
     * @param worker The worker who built
     * @param coordinates The coordinates which were used to build
     */
    public void addBuild(order order, Worker worker, Coordinates coordinates) {
        switch(order) {
            case MAIN:
                actions.push(new LogBuild(worker, coordinates, getBuffer()));
                break;
            case BEFORE:
                subActionBuffer.add(new LogBuild(worker, coordinates));
                break;
            case AFTER:
                actions.peek().addSubAction(new LogBuild(worker, coordinates));
        }
    }

    /**
     * Creates and adds a new notification action, logging the event (Can't be of MAIN type)
     * @param order The order of event
     * @param eventManager The event manager which notified the event
     * @param event The event which was notified
     * @param data The connected data
     */
    public void addEvent(order order, GodEventManager eventManager, String event, Integer data) {
        switch(order) {
            case BEFORE:
                subActionBuffer.add(new LogTrigger(eventManager, event, data));
                break;
            case AFTER:
                actions.peek().addSubAction(new LogTrigger(eventManager, event, data));
        }
    }

    /**
     * Gets and clears the buffer of sub actions which happend before the main action
     * @return The actions in the buffer
     */
    private List<LogAction> getBuffer() {
        List<LogAction> subs = new ArrayList<>(subActionBuffer);
        subActionBuffer.clear();
        return subs;
    }

    /**
     * Gets the latest action
     * @return The latest action
     */
    public LogAction getLatestAction() {
        return actions.peek();
    }

    /**
     * Checks that a player has moved then built, for the pass function
     * @return A boolean if the worker has moved then built
     */
    public boolean hasMovedThenBuilt() {
        boolean hasBuilt = false;
        boolean outcome = false;
        Iterator<LogAction> i = actions.iterator();
        while(i.hasNext() && !outcome) {
            LogAction currentAction = i.next();
            if (currentAction.getType().equals(LogAction.actionType.BUILD)) {
                hasBuilt = true;
            }
            else if(currentAction.getType().equals(LogAction.actionType.MOVE)) {
                outcome = hasBuilt;
            }
        }
        return outcome;
    }

    /**
     * Returns the coordinates connected to the latest main action
     * @return The coordinates of the latest main action
     */
    public Coordinates getLatestCoordinates() {
        LogAction action = actions.peek();

        return (action == null) ? null : action.getCoordinates();
    }

    /**
     * Checks if there is an action that can be undone
     * @return True if there is at least one logged action
     */
    public boolean canUndo() {
        return !actions.isEmpty();
    }

    /**
     * Undoes the latest main action and its sub actions
     */
    public void undo() {
        if(!actions.isEmpty()) {
            LogAction action = actions.pop();
            action.revert();
        }
    }

    /**
     * Undoes all actions taken during the turn
     */
    public void undoAll() {
        while(actions.size() > 0) {
            undo();
        }
    }

    /**
     * Resets the log for a new turn
     */
    public void reset() {
        actions.clear();
        subActionBuffer.clear();
    }
}
