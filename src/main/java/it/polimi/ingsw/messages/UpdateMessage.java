package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.BoardUpdate;
import it.polimi.ingsw.model.map.SimpleCoordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message class used to send the view every changed occurred during an action
 */
public class UpdateMessage extends SimpleMessage {
    /**
     * Map associating to every modified coordinates the changes happened during the past action
     */
    private final Map<SimpleCoordinates, BoardUpdate> boardUpdate;
    /**
     * Map associating to each worker's position the list of its reachable cells
     */
    private final Map<SimpleCoordinates, List<SimpleCoordinates>> reachableCells;
    /**
     * Map associating to each worker's position the cells it can build on (mapped to the list of possible heights)
     */
    private final Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> buildableCells;
    /**
     * Boolean value determining if the current player can place a worker
     */
    private boolean canPlaceWorker = false;
    /**
     * Boolean value specifying if the current player can pass
     */
    private final boolean canPass;
    /**
     * Boolean value used to tell if a player can undo a move
     */
    private final boolean canUndo;

    /**
     * Default constructor for the message
     * @param playerTurn The player whose turn it is
     * @param boardUpdate The map of changed coordinates
     * @param reachableCells The map of coordinates that can be reached from each worker
     * @param buildableCells The map of coordinates on which the workers can build (with possible heights)
     * @param canPass Boolean specifying if the player can pass the turn
     * @param canUndo Boolean specifying if the player can undo an action
     */
    public UpdateMessage(String playerTurn, Map<SimpleCoordinates, BoardUpdate> boardUpdate, Map<SimpleCoordinates, List<SimpleCoordinates>> reachableCells, Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> buildableCells, boolean canPass, boolean canUndo) {
        super(playerTurn);
        this.boardUpdate = boardUpdate;
        this.reachableCells = reachableCells;
        this.buildableCells = buildableCells;
        this.canPass = canPass;
        this.canUndo = canUndo;
    }

    /**
     * Constructor for the message, used during the worker placement phase
     * @param playerTurn The player whose turn it is
     * @param boardUpdate The map of changed coordinates
     * @param reachableCells The map of coordinates that can be reached from each worker
     * @param buildableCells The map of coordinates on which the workers can build (with possible heights)
     * @param canPass Boolean specifying if the player can pass the turn
     * @param canUndo Boolean specifying if the player can undo an action
     * @param canPlaceWorker Boolean used to specify if the player can place a worker on the board
     */
    public UpdateMessage(String playerTurn, Map<SimpleCoordinates, BoardUpdate> boardUpdate, Map<SimpleCoordinates, List<SimpleCoordinates>> reachableCells, Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> buildableCells, boolean canPass, boolean canUndo, boolean canPlaceWorker) {
        this(playerTurn, boardUpdate, reachableCells, buildableCells, canPass, canUndo);
        this.canPlaceWorker = canPlaceWorker;
    }

    /**
     * Getter for the map of changed coordinates
     * @return The map of changed coordinates
     */
    public Map<SimpleCoordinates, BoardUpdate> getBoardUpdate() {
        return boardUpdate;
    }

    /**
     * Getter for the map of reachable cells from each worker
     * @return The worker's coordinates mapped to the list of its reachable cells
     */
    public Map<SimpleCoordinates, List<SimpleCoordinates>> getReachableCells() {
        return reachableCells;
    }

    /**
     * Getter for the map of buildable cells for each worker
     * @return The worker's coordinates mapped to the coordinates it can build on (mapped to the possible heights)
     */
    public Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> getBuildableCells() {
        return buildableCells;
    }

    /**
     * Getter for the canPlaceWorker attribute
     * @return True if the player can place a worker during the action
     */
    public boolean canPlaceWorker() {
        return canPlaceWorker;
    }

    /**
     * Getter for the canPass attribute
     * @return True if the player can pass during the following action
     */
    public boolean canPass() {
        return canPass;
    }

    /**
     * Getter for the canUndo attribute
     * @return True if the player can undo an action
     */
    public boolean canUndo() {
        return canUndo;
    }

    /**
     * Overridden makeLight, used to create the message to be sent to the inactive players
     * @return The light update message
     */
    @Override
    public SimpleMessage makeLight() {
        return new UpdateMessage(getPlayer(), this.getBoardUpdate(), new HashMap<>(), new HashMap<>(), false, false, false);
    }
}
