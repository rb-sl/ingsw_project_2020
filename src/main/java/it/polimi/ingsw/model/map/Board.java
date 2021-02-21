package it.polimi.ingsw.model.map;

import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.BoardUpdate;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing the board of the game
 */
public class Board
{
    /**
     * Cells the real board is divided into
     */
    private final ArrayList<LinkedList<MapComponent>> boardCells;
    /**
     * Creator used to abstract creation of domes and blocks
     */
    private Creator creator;
    /**
     * Maximum height of the blocks stack built over any cell
     */
    private final Integer maxLevel = 3;
    /**
     * Number of rows and columns the board is divided into
     */
    final private Integer nRow, nCol;
    /**
     * List of players playing at the game using this Board
     */
    private final List<Player> players;

    /**
     * Buffer containing the cells modified during an action
     */
    private final Map<SimpleCoordinates, BoardUpdate> updateBuffer;

    /**
     * Number of workers a player has
     */
    private final Integer workersPerPlayer;

    /**
     * Board constructor
     * @param nRow number of rows
     * @param nCol number of columns
     * @param nWorkers number of workers for each player
     */
    public Board(Integer nRow, Integer nCol, Integer nWorkers) {
        boardCells = new ArrayList<>();
        players = new ArrayList<Player>();
        this.nRow = nRow;
        this.nCol = nCol;
        this.workersPerPlayer = nWorkers;
        //Board initialization
        for (Integer i = 0; i < nRow*nCol; i++) {
            LinkedList<MapComponent> newStack = new LinkedList<>();
            newStack.push(new Block(0));
            boardCells.add(newStack);
        }
        updateBuffer = new HashMap<>();
    }

    /**
     * Getter for the number of rows this Board is divided into
     * @return number of rows
     */
    public Integer getnRow() {
        return nRow;
    }

    /**
     * Getter for the number of columns this Board is divided into
     * @return number of columns
     */
    public Integer getnCol() {
        return nCol;
    }

    /**
     * Getter for the maximum level of MapComponents stacks
     * @return maximum level
     */
    public Integer getMaxLevel() {
        return maxLevel;
    }

    /**
     * Getter for the number of workers
     * @return The number of worker per player
     */
    public Integer getNWorkers() {
        return workersPerPlayer;
    }

    /**
     * Method to remove a player from the list of Player of this Board (
     * @param player Player to be removed
     */
    public void removePlayer(Player player) {
        player.getGod().resetTriggers();
        this.players.remove(player);
    }

    /**
     * Getter for the list of players currently playing on this Board
     * @return list of players playing on this Board
     */
    public List<Player> getPlayersList() {
        List<Player> copyPlayers = new ArrayList<>(players);
        return copyPlayers;
    }

    /**
     * Method to translate board coordinates into indexes of the array of cell
     * @param cr Coordinates to be translated
     * @return Index of the array corresponding to Coordinates passed to this method
     */
    private Integer coordinateToIndex(Coordinates cr) {
        return cr.getRow()*nCol+cr.getColumn();
    }

    /**
     * Method to translate indexes of the cells array into board coordinates
     * @param i index to be translated
     * @return object Coordinates corresponding to the index passed to this method
     */
    private Coordinates indexToCoordinate(Integer i) {
        return new Coordinates(i/nRow, i%nCol, this);
    }

    /**
     * Method to retrieve the top block of the block stack built over specified Coordinates
     * @param coor position on the board to be examined
     * @return top block found over passed Coordinates
     */
    public MapComponent getTopBlock(Coordinates coor) {
        Integer index = coordinateToIndex(coor);
        return boardCells.get(index).peek();
    }

    /**
     * Method to retrieve the height of the block stack built over specified Coordinates
     * @param coor position on the board to be examined
     * @return Height of the stack found over passed Coordinates
     */
    public Integer getHeight(Coordinates coor) {
        Integer index = coordinateToIndex(coor);
        return boardCells.get(index).size() - 1;
    }

    /**
     * Method to add a new block over the existing ones on the passed Coordinates
     * @param coor coordinates you want the block to be placed over
     * @param level level of the block to be added on the existing stack
     * @throws WrongBlockException exception thrown when the specified level does not correspond to a valid block
     */
    public void addMapComponent(Coordinates coor, Integer level) throws WrongBlockException {
        MapComponent newComponent = null;
        Integer index = -1;
        index = coordinateToIndex(coor);
        LinkedList<MapComponent> stack = boardCells.get(index);
        //if the passed level is minor to the stack size it can't be build over the existing top MapComponent
        if (level <= stack.size() - 1)
            throw new WrongBlockException(level.toString());
        if (level > 0 && level < maxLevel + 1)
            creator = new BlockCreator();
        else
            if (level == maxLevel + 1)
                creator = new DomeCreator();
            else
                throw new WrongBlockException(level.toString());
        newComponent = creator.createMapComponent(level);
        stack.push(newComponent);
    }

    /**
     * Method to remove the block on the top of the stack built over this Coordinate
     * @param cr target Coordinate to remove the top block from
     */
    public void removeTopBlock(Coordinates cr) {
        Integer index = coordinateToIndex(cr);
        boardCells.get(index).pop();
    }

    /**
     * Method to add a player to the list of players playing on this Board
     * @param player Player to be added
     */
    public void addPlayers(Player player) {
        players.add(player);
    }

    /**
     * Method to add list of players playing on this Board
     * @param playersList list of players to be set on this Board
     */
    public void addPlayers(List<Player> playersList) {
        players.addAll(playersList);
    }

    /**
     * Method to retrieve all the opponents of the specified Player
     * @param player player to be excluded from the list to find all his opponents
     * @return list of Player except the one passed to this method
     */
    public List<Player> getOpponents(Player player){
        //Remove from the list of players to be returned the passed Player
        List<Player> copyPlayers = players.stream().filter(x -> x != player).collect(Collectors.toList());
        return copyPlayers;
    }

    /**
     * Getter for the levels built on specified coordinates
     * @param coordinates The queried coordinates
     * @return A list containing the levels built on the coordinates
     */
    public List<Integer> getLevelList(Coordinates coordinates) {
        Integer index = coordinateToIndex(coordinates);
        List<Integer> levelList = new ArrayList<>();
        for(MapComponent mapComponent: boardCells.get(index)) {
            levelList.add(mapComponent.getLevel());
        }
        return levelList;
    }

    /**
     * Returns all the coordinates and their properties, then clears the buffer
     * @return The updates
     */
    public Map<SimpleCoordinates, BoardUpdate> getUpdate() {
        Map<SimpleCoordinates, BoardUpdate> copy = new HashMap<>(updateBuffer);
        updateBuffer.clear();
        return copy;
    }

    /**
     * Adds the specified coordinates to the buffer
     * @param coordinates The coordinates to register
     */
    public void addUpdate(Coordinates coordinates) {
        BoardUpdate update = new BoardUpdate(getLevelList(coordinates));

        if(coordinates.getTopBlock().hasWorkerOnTop()) {
            Worker worker = coordinates.getTopBlock().getWorkerOnTop();
            update.setWorkerOwner(worker.getPlayer().getNickname());
            update.setWorkerSex(worker.getSex());
        }

        if(updateBuffer.containsKey(coordinates.getSimpleCoor())) {
            updateBuffer.replace(coordinates.getSimpleCoor(), update);
        }
        else {
            updateBuffer.put(coordinates.getSimpleCoor(), update);
        }
    }
}
