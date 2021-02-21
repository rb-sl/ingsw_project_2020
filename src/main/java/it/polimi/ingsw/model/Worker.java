package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.god.GodMovement;
import it.polimi.ingsw.model.god.GodWin;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to represent the real worker miniature
 */
public class Worker {
    /**
     * Sex of the Worker (required by some advanced Gods)
     */
    private char sex;
    /**
     * Current position of the Worker on the board
     */
    private Coordinates currentPos;
    /**
     * Previous position the Worker had on the board
     */
    private Coordinates previousPos;
    /**
     * Player managing the Worker
     */
    private Player player;
    /**
     * Counter for the number of times the Worker has moved in the current round
     */
    private Integer timesMoved;
    /**
     * Counter for the number of times the Worker has built something in the current round
     */
    private Integer timesBuilt;

    /**
     * Worker constructor for a Worker created at the beginning of the game and placed on the board
     * @param player Player managing this Worker
     * @param sex sex of this Worker
     * @param position initial position on the board of this Worker
     */
    public Worker(Player player, char sex, Coordinates position) {
        this(player, sex);
        // setWorkerOnTop must be put externally after the Worker's creation
        this.currentPos = position;
    }

    /**
     * Generic constructor for a Worker when its position is not known at the time of creation
     * @param player Player managing this Worker
     * @param sex sex of this Worker
     */
    public Worker(Player player, char sex) {
        this.player = player;
        this.sex = sex;
        this.previousPos = null;
        timesBuilt = 0;
        timesMoved = 0;
    }

    /**
     * Getter for the worker's sex
     * @return sex of this Worker
     */
    public char getSex() {
        return sex;
    }

    /**
     * Getter for the worker's current position
     * @return current position of this Worker
     */
    public Coordinates getCurrentPos() {
        return currentPos;
    }

    /**
     * Getter for the worker's previous position
     * @return previous position of this Worker
     */
    public Coordinates getPreviousPos() {
        return previousPos;
    }

    /**
     * Getter for the worker's player
     * @return Player managing this Worker
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Getter for the current position of the worker
     * @return current position of this Worker
     */
    public Coordinates getCurrentPosition() {
        return currentPos;
    }

    /**
     * Getter for the previous position the worker had on the board
     * @return previous position of this Worker
     */
    public Coordinates getPreviousPosition() {
        return previousPos;
    }

    /**
     * Setter for the current position of the worker
     * @param currentPos new current position of this Worker
     */
    public void setCurrentPosition(Coordinates currentPos) {
        this.currentPos = currentPos;
    }

    /**
     * Setter for the previous position of the worker
     * @param previousPos previous position of this Worker
     */
    public void setPreviousPosition(Coordinates previousPos) {
        this.previousPos = previousPos;
    }

    /**
     * Getter for the number of times the worker has moved during the current round
     * @return number of times this Worker has moved
     */
    public Integer getTimesMoved() {
        return timesMoved;
    }

    /**
     * Getter for the number of times the worker has built something during the current round
     * @return number of times this Worker has built something
     */
    public Integer getTimesBuilt() {
        return timesBuilt;
    }

    /**
     * Setter to initialize the times the worker has moved at the beginning of each turn
     * @param timesMoved number of times to initialize the attribute timesMoved (usually 0)
     */
    public void setTimesMoved(Integer timesMoved) {
        this.timesMoved = timesMoved;
    }

    /**
     * Setter to initialize the times the worker has built at the beginning of each turn
     * @param timesBuilt number of times to initialize the attribute timesBuilt (usually 0)
     */
    public void setTimesBuilt(Integer timesBuilt) {
        this.timesBuilt = timesBuilt;
    }

    /**
     * Method to move the Worker on the board and place it on the passed coordinates in the way specified by the
     * God influencing the Worker
     * @param coordinates position this Worker should be placed into
     * @throws HasWonException if the move generates a win
     */
    public void moveTo(Coordinates coordinates)  throws HasWonException {
        GodMovement ability = player.getGod().getMove();
        try {
            ability.moveTo(this, coordinates);
            timesMoved++;
            if (isWinner()) {
                throw new HasWonException();
            }
        }
        catch (NotFreeException | CantReachException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to make the Worker build something on the specified position in the way specified by the God influencing
     * the Worker
     * @param coordinates position this Worker should build something onto
     * @param level The desired block level to be added
     */
    public void buildTo(Coordinates coordinates, Integer level) {
        GodBuild ability = player.getGod().getBuild();
        try {
            ability.buildTo(this, coordinates, level);
            timesBuilt++;
        } catch (CantBuildException | WrongBlockException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to force the Worker onto a specified position. It affects previous position and current position
     * @param coordinates new current position the Worker should be forced to
     * @throws NotFreeException exception thrown when the specified position is occupied by something preventing
     * the Worker to be forced
     */
    public void forceTo(Coordinates coordinates) throws NotFreeException {
        Coordinates previous;
        //Changes the block status before to ensure nothing is changed if NotFreeException is thrown
        coordinates.setWorkerOnTop(this);
        //Changing position and removing the Worker from the block it was occupying
        setPreviousPosition(currentPos);
        setCurrentPosition(coordinates);
        if((previous = getPreviousPosition()) != null && this.equals(previous.getTopBlock().getWorkerOnTop()))
            previousPos.getTopBlock().removeWorkerOnTop();
    }

    /**
     * Method to retrieve the valid cells around the Worker invoking this method that it can move onto
     * @return list of reachable and valid cells around this Worker
     */
    public List<Coordinates> reachableCells() {
        GodMovement ability = player.getGod().getMove();
        List<Coordinates> reachables;
        List<Coordinates> adjacents = getCurrentPosition().getAdjacentCells();
        //removes from the adjacentCells the ones where the Worker can't move on
        reachables = adjacents.stream().filter(x-> ability.canReach(this, x)).collect(Collectors.toList());
        return reachables;
    }

    /**
     * Method to retrieve the valid cells around the Worker invoking this method that it can built something onto
     * @return list of buildable and valid cells around this Worker
     */
    public Map<Coordinates, List<Integer>> buildableCells() {
        GodBuild ability = player.getGod().getBuild();
        List<Integer> buildables;
        Map<Coordinates, List<Integer>> map = new HashMap<>();

        List<Coordinates> adjacents = this.getCurrentPosition().getAdjacentCells();

        for(Coordinates c: adjacents) {
            buildables = ability.canBuild(this, c);
            if(!buildables.isEmpty())
                map.put(c, buildables);
        }

        return map;
    }

    /**
     * Method to know whether the Worker has fulfilled the condition required to win at the game
     * @return true if this Worker has won, false otherwise
     */
    public boolean isWinner() {
        GodWin ability = player.getGod().getWin();
        return ability.isWinner(this).getOutcome();
    }

    /**
     * Compares two workers
     * @param o The objectbeing compared
     * @return True if the objects represent the same worker
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(currentPos, worker.currentPos);
    }
}
