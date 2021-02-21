package it.polimi.ingsw.Client;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.Commands.CommandType;
import it.polimi.ingsw.Client.GUI.WelcomeController;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.BoardUpdate;
import it.polimi.ingsw.model.map.SimpleCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to group both gui and cli view variants
 */
public abstract class ViewInterface {
    /**
     * The nickname of the view owner
     */
    private String ownerNick = null;
    /**
     * Map associating the list of gods to their descriptions
     */
    private final Map<String, String> godMap = new HashMap<>();
    /**
     * Map associating the players to their gods
     */
    private Map<String, String> players;
    /**
     * Map associating the workers' identifiers to their position
     */
    private final Map<Character, SimpleCoordinates> workerMap = new HashMap<>();
    /**
     * List of cells to be changed after an update
     */
    private List<SimpleCoordinates> modifiedCoordinates;
    /**
     * Map associating to every message code the relative message (from the configuration file)
     */
    private Map<String, Info> infoCodes;
    /**
     * List containing the commands available to the player at a given time
     */
    private List<CommandType> commandsList;

    /**
     * Board parameters
     */
    private int nRow, nCol;
    /**
     * Matrix representing the board
     */
    private BoardUpdate[][] map;
    /**
     * List of workers' identifiers
     */
    private List<Character> workerSex;
    /**
     * List of sections for the help menu
     */
    private List<HelpSection> helpSections;
    /**
     * Flag indicating if the player can pass
     */
    private boolean canPass;
    /**
     * Flag indicating if the application is in the initialization state
     */
    private boolean isInit;
    /**
     * Flag indicating if the player is quitting the game
     */
    private boolean isQuitting = false;
    /**
     * Flag indicating if the player has moved during the turn
     */
    private boolean hasMoved = false;

    /**
     * Shows a message
     * @param info The message to display
     */
    public abstract void showInfo(String info);

    /**
     * Shows an error message
     * @param error The message to display
     */
    public abstract void showError(String error);

    /**
     * Shows the message when waiting for players
     */
    public abstract void waitMessage();

    /**
     * Shows the message when waiting for the challenger choose
     * @param challengerNick The nickname of the challenger
     */
    public abstract void challengerMessage(String challengerNick);

    /**
     * Shows the message when waiting for a player to choose their god
     * @param activePlayer The nickname of the active player
     */
    public abstract void waitForChoose(String activePlayer);

    /**
     * Resets the view for a new game
     */
    public abstract void reset();

    /**
     * Performs the operations needed to start a match
     * @return The message to be sent to the server
     */
    public abstract InitMatchMessage startNewMatch();

    /**
     * Performs the operations needed to join a match
     * @return The message to be sent to the server
     */
    public abstract JoinMessage joinMatch();

    /**
     * Performs the output actions when the pass timer runs out
     */
    public abstract void timeout();

    /**
     * Gets the user's action
     * @return The desired command type
     */
    public abstract CommandType chooseAction();

    //Usable methods in GUI

    /**
     * Shows the welcome screen when connecting
     */
    public abstract void showWelcome();

    /**
     * Performs a worker move
     * @param reachableCells The map of workers associated to their reachable cells
     * @return The message to be sent to the server
     */
    public abstract MoveMessage move(Map<SimpleCoordinates, List<SimpleCoordinates>> reachableCells);

    /**
     * Performs a build
     * @param buildableCells Map associating the workers to their buildable cells and heights
     * @return The message to be sent to the server
     */
    public abstract BuildMessage build(Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> buildableCells);

    /**
     * Shows the game board
     */
    public abstract void showBoard();

    /**
     * Places a worker on the board
     * @param sex The worker's identifier
     */
    public abstract void placeWorker(char sex);

    /**
     * Updates the possible actions of a player
     * @param updateMessage The update from the server
     */
    public abstract void updateMenu(UpdateMessage updateMessage);

    /**
     * Performs the pass
     */
    public abstract void pass();

    /**
     * Shows the gods during the god choosing phase
     * @param godList The list of choosable gods
     * @param gson The gson object
     */
    public abstract void showGod(GodListMessage godList, Gson gson);

    /**
     * Shows the opponents list
     * @param players The list of players
     * @param challenger The challenger
     */
    public abstract void showOpponents(PlayersMessage players, String challenger);

    /**
     * Shows the player's god
     */
    public abstract void showOwnerGod();

    /**
     * Shows the rules
     */
    public abstract void showHelp();

    /**
     * Shows the message asking to wait for the turn
     * @param opponent The active player
     */
    public abstract void waitForTurn(String opponent);

    /**
     * Shows the lose message
     */
    public abstract void showLose();

    /**
     * Shows the endgame message when its due to a player's disconnection
     * @param player The player who disconnected
     */
    public abstract void disconnected(String player);

    /**
     * Shows the endgame message
     * @param winner The winning player
     */
    public abstract void endGame(String winner);

    /**
     * Updates the board matrix
     * @param newMap The modified coordinates
     */
    public void updateBoard(Map<SimpleCoordinates, BoardUpdate> newMap) {
        BoardUpdate update;
        modifiedCoordinates = new ArrayList<>(newMap.keySet());
        for (SimpleCoordinates coor : newMap.keySet()) {
            update = newMap.get(coor);
            map[coor.getRow()][coor.getColumn()].setLevelList(update.getLevelList());
            if (update.getWorkerOwner() != null) {
                if (update.getWorkerOwner().equals(ownerNick) && !isInit())
                    workerMap.replace(update.getWorkerSex(), coor);
                map[coor.getRow()][coor.getColumn()].setWorkerOwner(update.getWorkerOwner());
                map[coor.getRow()][coor.getColumn()].setWorkerSex(update.getWorkerSex());
            }
            else
            {
                if (map[coor.getRow()][coor.getColumn()].getWorkerOwner() != null) {
                    map[coor.getRow()][coor.getColumn()].setWorkerOwner(null);
                    map[coor.getRow()][coor.getColumn()].setWorkerSex(null);
                }
            }
        }
    }

    /**
     * Initializes the board matrix
     */
    public void initBoard() {
        map = new BoardUpdate[nRow][nCol];
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                List<Integer> level = new ArrayList<>();
                level.add(0);
                map[i][j] = new BoardUpdate(level);
            }
        }
    }

    /**
     * Initializes the view parameters
     * @param nRow The number of board's rows
     * @param nCol The number of columns
     * @param workerSex The list of worker identifiers
     * @param helpSections The list of rules
     * @param infoCodes The association between code and message
     */
    public void initialize(int nRow, int nCol, List<Character> workerSex, List<HelpSection> helpSections, Map<String, Info> infoCodes) {
        this.nRow = nRow;
        this.nCol = nCol;
        this.workerSex = workerSex;
        this.helpSections = helpSections;
        this.infoCodes = infoCodes;
        commandsList = new ArrayList<>();
        setMap(new BoardUpdate[getnRow()][getnCol()]);
    }

    /**
     * Checks if a string represents a number
     * @param string The string to check
     * @return True if the string represents a number
     */
    public static boolean isNumber(String string) {
        if (string == null || string.length() == 0)
            return false;
        return string.chars().allMatch(c -> Character.isDigit(c));
    }

    /**
     * Checks if a string is made of letters
     * @param string The string to check
     * @return True if the string is made of letters
     */
    public boolean isLetter(String string) {
        if (string == null || string.length() == 0 || string.length() > 2)
            return false;
        return string.chars().allMatch(c -> Character.isLetter(c));
    }

    /**
     * Getter gor the view owner's nickname
     * @return The nickname
     */
    public String getOwnerNick() {
        return ownerNick;
    }

    /**
     * Setter for the owner nickname attribute
     * @param ownerNick The nickname to set
     */
    public void setOwnerNick(String ownerNick) {
        this.ownerNick = ownerNick;
    }

    /**
     * Sets the welcome controller
     * @param controller The controller to set
     */
    public abstract void setWelcomeController(WelcomeController controller);

    /**
     * Getter for the god map
     * @return The god map
     */
    public Map<String, String> getGods() {
        return godMap;
    }

    /**
     * Setter for the god map
     * @param godList The list of gods associated to their descriptions
     */
    public void setGods(List<ChosenGodMessage> godList) {
        for (ChosenGodMessage g : godList) {
            godMap.put(g.getName(), g.getDescription());
        }
    }

    /**
     * Getter for the modified coordinates
     * @return The modified coordinates
     */
    public List<SimpleCoordinates> getModifiedCoordinates() {
        return modifiedCoordinates;
    }

    /**
     * Getter for the isQuitting attribute
     * @return The isQuitting flag
     */
    public boolean isQuitting() {
        return isQuitting;
    }

    /**
     * Setter for the isQuitting flag
     * @param quitting The value to set
     */
    public void setQuitting(boolean quitting) {
        isQuitting = quitting;
    }

    /**
     * Getter for the isInit flag
     * @return The isInit flag
     */
    public boolean isInit() {
        return isInit;
    }

    /**
     * Setter for the isInit flag
     * @param init The value to set
     */
    public void setInit(boolean init) {
        isInit = init;
    }

    /**
     * Getter for the players
     * @return The map of players
     */
    public Map<String, String> getPlayers() {
        return players;
    }

    /**
     * Setter for the players map
     * @param players The map to set
     */
    public void setPlayers(Map<String, String> players) {
        this.players = players;
    }

    /**
     * Getter for the workers
     * @return The workers' map
     */
    public Map<Character, SimpleCoordinates> getWorkers() {
        return workerMap;
    }

    /**
     * Resets the object's variables
     */
    public void resetData() {
        workerMap.clear();
        godMap.clear();
        isInit = true;
        if (players != null)
            players.clear();
    }

    /**
     * Getter for the nRow attribute
     * @return The number of rows
     */
    public int getnRow() {
        return nRow;
    }

    /**
     * Setter for the nRow attribute
     * @param nRow The number to set
     */
    public void setnRow(int nRow) {
        this.nRow = nRow;
    }

    /**
     * Getter for the nCol attribute
     * @return The number of columns
     */
    public Integer getnCol() {
        return nCol;
    }

    /**
     * Setter for the nCol attribute
     * @param nCol The number to set
     */
    public void setnCol(int nCol) {
        this.nCol = nCol;
    }

    /**
     * Getter for the game board
     * @return The game board as a matrix
     */
    public BoardUpdate[][] getMap() {
        return map;
    }

    /**
     * Sets the game board
     * @param map The board map
     */
    public void setMap(BoardUpdate[][] map) {
        this.map = map;
    }

    /**
     * Getter for the list of worker identifiers
     * @return The list of worker identifiers
     */
    public List<Character> getWorkerSex() {
        return workerSex;
    }

    /**
     * Getter for the rules
     * @return The list of helpSections
     */
    public List<HelpSection> getHelpSections() {
        return helpSections;
    }

    /**
     * Setter for the helpSection
     * @param helpSections The helpSections to set
     */
    public void setHelpSections(List<HelpSection> helpSections) {
        this.helpSections = helpSections;
    }

    /**
     * Setter for the worker identifiers
     * @param workerSex List of worker identifiers
     */
    public void setWorkerSex(List<Character> workerSex) {
        this.workerSex = workerSex;
    }

    /**
     * Getter for the canPass attribute
     * @return True if the player can pass
     */
    public boolean canPass() {
        return canPass;
    }

    /**
     * Setter for the canPass attribute
     * @param canPass The value to set
     */
    public void setCanPass(boolean canPass) {
        this.canPass = canPass;
    }

    /**
     * Getter for the hasMoved attribute
     * @return True if the player has moved
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Setter for the hasMoved attribute
     * @param hasMoved The value to set
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Getter for the list of available commands
     * @return The list of commands
     */
    public List<CommandType> getUserCommands() {
        return commandsList;
    }

    /**
     * Resets the command list
     */
    public void clearUserCommands() {
        commandsList.clear();
    }

    /**
     * Adds a command to the command list
     * @param command The command to add
     */
    public void addUserCommand(CommandType command) {
        commandsList.add(command);
    }

    /**
     * Getter for the info codes
     * @return The map of info codes
     */
    public Map<String, Info> getInfoCodes() {
        return infoCodes;
    }
}
