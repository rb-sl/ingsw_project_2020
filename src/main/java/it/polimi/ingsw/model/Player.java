package it.polimi.ingsw.model;

import it.polimi.ingsw.model.god.God;
import it.polimi.ingsw.model.map.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class modelling the Player playing at the game
 */
public class Player {
    /**
     * Board the Player is playing on. Used to separate different players for different matches.
     */
    private Board board;
    /**
     * Nickname of the Player
     */
    private String name;
    /**
     * God card the Player is playing with
     */
    private God god;
    /**
     * Player's miniature Worker list
     */
    private List<Worker> workers;

    /**
     * Player class constructor for a player playing on the passed board
     * @param name Nickname of the Player
     * @param board Board the Player is playing on
     */
    public Player (String name, Board board) {
        this.name = name;
        this.board = board;
        workers = new ArrayList<Worker>();
    }

    /**
     * Getter for Player's Nickname
     * @return Player's Nickname
     */
    public String getNickname() {
        return name;
    }

    /**
     * Getter for Player's God card
     * @return God card of this Player
     */
    public God getGod() {
        return god;
    }

    /**
     * Getter for Board
     * @return Board the Player's is playing on
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter to retrieve a Worker at a given index
     * @param index index of the Worker
     * @return Worker present in the list at the specified index
     */
    public Worker getWorker(Integer index) {
        if (index >= 0 && index < workers.size())
            return workers.get(index);
        return null;
    }

    /**
     * Getter for all the workers this Player is managing
     * @return list of Worker of this Player
     */
    public List<Worker> getWorkers() {
        List<Worker> copyWorkers = new ArrayList<>(workers);
        return copyWorkers;
    }

    /**
     * Setter for the Board the Player wants to play on
     * @param board board assigned to this Player
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Setter for God card
     * @param god God card to be set
     */
    public void setGod(God god) {
        this.god = god;
    }


    /**
     * Method to add Worker to the Workers list of this Player
     * @param w Worker to be added
     */
    public void addWorker(Worker w) {
        if (w != null)
            workers.add(w);
    }

    /**
     * Method to remove a Worker from this Player list
     * @param w Worker to be removed
     */
    public void removeWorker(Worker w) {
        if (w != null)
            workers.remove(w);
    }

    /**
     * Method to remove a Worker from this Player list
     * @param index of the Worker to be removed
     */
    public void removeWorker(int index) {
        if (index >= 0 && index < workers.size())
            workers.remove(index);
    }

    /**
     * Checks if the player can pass
     * @return True if the player can pass
     */
    public boolean canPass() {
        return getGod().getLogTurn().hasMovedThenBuilt();
    }

    /**
     * Methods to return all the opponents (other Players) of the Player is invoked on
     * @return  List of the opponents of this Player
     */
    public List<Player> getOpponents() {
        return board.getOpponents(this);
    }

    /**
     * Compares two players
     * @param o The object to be compared against
     * @return True if the two objects represent the same player
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }
}
