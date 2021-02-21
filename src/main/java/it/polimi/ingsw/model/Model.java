package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.exception.EndGameException;
import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.god.God;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.model.map.SimpleCoordinates;
import it.polimi.ingsw.misc.BiObservable;
import it.polimi.ingsw.view.View;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that interfaces the model's classes with the rest of the application
 */
public class Model extends BiObservable<MessageType, SimpleMessage> {
    /**
     * Path to the gods' configuration file
     */
    private static final String godsConf = "config/gods.json";
    /**
     * Path to the board's configuration file
     */
    private static final String boardConf = "config/board.json";
    /**
     * List with the players that play the game
     */
    private List<Player> players;
    /**
     * Board of the game
     */
    private Board board;
    /**
     * Tracker of the current turn
     */
    private Integer currentTurn;
    /**
     * This attribute contains the challenger, extracted from the players; the challenger will choose the starter player
     */
    private Player challenger;
    /**
     * List with the names of the gods drawn
     */
    private List<ChosenGodMessage> chosenGods;
    /**
     * This attribute contains the number of the players of the game
     */
    private Integer nPlayers;
    /**
     * This attribute indicates if the game will use the god cards
     */
    private boolean withGods;

    /**
     * Gson object used to create messages
     */
    private final Gson gson;

    /**
     * List containing the list of messages related to gods that can still be chosen
     */
    private List<ChosenGodMessage> updatedChosenGods = new ArrayList<>();

    /**
     * Method that initializes the class
     * @param nPlayers the number of players for the game
     * @param withGods boolean flag that indicates if the game will use god cards
     */
    public Model(Integer nPlayers, boolean withGods) {
        this.nPlayers = nPlayers;
        this.players = new ArrayList<>();
        this.withGods = withGods;
        gson = new Gson().newBuilder().enableComplexMapKeySerialization().create();
    }

    /**
     * Getter for the attribute withGods
     * @return the boolean value of withGods
     */
    public boolean isWithGods() {
        return withGods;
    }

    /**
     * Getter for the attribute nPlayers
     * @return the Integer value of nPlayers
     */
    public Integer getnPlayers() {
        return nPlayers;
    }

    /**
     * Method that adds a list of Player to players
     * @param p this is the list that will be added
     */
    public void addPlayers(List<Player> p){
        players.addAll(p);
        board.addPlayers(p);
    }

    /**
     * Setter for the attribute current turn
     * @param currentTurn the value that will be set into this.currentTurn
     */
    public void setTurn(Integer currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * Getter for the attribute players
     * @return the list with the players that play the game
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * This method returns the nickname of the currentTurn's player
     * @return the the nickname of the currentTurn's player
     */
    public String getTurnPlayer(){
        return players.get(currentTurn).getNickname();
    }

    /**
     * Setter for the attribute Challenger
     */
    public void setChallenger(){
        Random random = new Random();
        random.setSeed(System.nanoTime());
        currentTurn = random.nextInt(players.size());
        challenger = players.get(currentTurn);
        updateTurn();
    }

    /**
     * This method gets the nickname of the challenger
     * @return the nickname of the challenger
     */
    public String getChallenger(){
        return challenger.getNickname();
    }

    /**
     * Creates default gods for a game without cards
     */
    public void atheistCreation(){
        String chal = getChallenger();
        createGod(players.get(currentTurn), "Atheist");
        do{
            createGod(players.get(currentTurn), "Atheist");
        }while(!players.get(currentTurn).getNickname().equals(chal));
        Map<String, String> playersMap= new HashMap<>();
        for(Player p: players)
            playersMap.put(p.getNickname(), p.getGod().getGodName());
        notify(MessageType.PLAYERSMESSAGE, new PlayersMessage(playersMap, getTurnPlayer()));
    }

    /**
     * This method randomly extracts the gods that will be used in this game and notifies these gods to the views
     */
    public void getGodList(){
        try (Reader reader = new FileReader(godsConf)) {
            // Convert JSON File to Java Object
            Random random = new Random();
            random.setSeed(System.nanoTime());
            Type godMapType = new TypeToken<Map<String, GodAttribute>>() {}.getType();
            Map<String, GodAttribute> attributes = gson.fromJson(reader, godMapType);
            List<Integer> indexList = new ArrayList<>();
            for(int i = 0; i < players.size(); i++ ) {
                Integer num = random.nextInt(attributes.size());
                while(indexList.contains(num))
                    num = random.nextInt(attributes.size());
                indexList.add(num);
            }
            List<String> godList = new ArrayList<>();
            godList.addAll(attributes.keySet());
            List<String> godNames = godList.stream().filter(x -> indexList.contains(godList.indexOf(x))).collect(Collectors.toList());

            chosenGods = new ArrayList<>();
            for(String n: godNames){
                ChosenGodMessage chosenGod = new ChosenGodMessage(n, attributes.get(n).description);
                chosenGods.add(chosenGod);
            }
            updatedChosenGods.addAll(chosenGods);
            GodListMessage g = new GodListMessage(getTurnPlayer(), chosenGods);
            notify(MessageType.GODLISTMESSAGE, g);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method initializes the board
     * @throws BadConfigurationException if the configuration parameters cannot be accepted
     */
    public void initBoard() throws BadConfigurationException {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(boardConf)) {
            // Convert JSON File to Java Object
            BoardAttribute attributes = gson.fromJson(reader, BoardAttribute.class);

            // Checks that all configuration parameters are acceptable
            if(attributes.nCol <= 0 || attributes.nRow <= 0 || attributes.nCol * attributes.nRow < attributes.workersPerPlayer) {
                throw new BadConfigurationException("Wrong board dimensions: " + attributes.nCol + " X " + attributes.nRow + " (Board must have at least " + attributes.workersPerPlayer + " cells");
            }
            if(attributes.workersPerPlayer <= 0) {
                throw new BadConfigurationException("Wrong number of workers per player: " + attributes.workersPerPlayer + " (Must be > 0)");
            }
            if(attributes.workerSex.size() != attributes.workersPerPlayer) {
                throw new BadConfigurationException("Number of workers per player and number of worker identifiers do not coincide: " + attributes.workersPerPlayer + " vs " + attributes.workerSex.size() + " (Numbers must coincide)");
            }
            for(Character c: attributes.workerSex) {
                if(attributes.workerSex.indexOf(c) != attributes.workerSex.lastIndexOf(c)) {
                    throw new BadConfigurationException("Duplicate Worker identifier: \"" + c + "\" (Worker identifiers must be unique)");
                }
            }

            // Creates the board
            board = new Board(attributes.nRow, attributes.nCol, attributes.workersPerPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the attribute board
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * This method undoes the last action done in the current turn by the player that invokes this
     * @param player that perform the undo
     */
    public void undo(Player player){
        player.getGod().getLogTurn().undo();
        sendUpdate();
    }

    /**
     * This method undoes all actions done in the current turn by the player that invokes this
     * @param player that perform the undoAll
     */
    public void undoAll(Player player){
        player.getGod().getLogTurn().undoAll();
        sendUpdate();
    }

    /**
     * This method initializes one worker and places it on the board
     * @param p player that invokes the method and will own the worker
     * @param sex1 sex of the worker
     * @param c1 coordinates where the worker will be placed
     * @return true if the the cell with coordinates c1 is free and the worker is initialized and placed in the cell,
     * false if the cell with coordinates c1 is not free
     */
    public boolean initWorker(Player p, char sex1, Coordinates c1){
        if(!c1.getTopBlock().isFree())
            return false;
        Worker w1 = new Worker(p, sex1, c1);
        p.addWorker(w1);
        try{
            c1.setWorkerOnTop(w1);
            sendInitUpdate();
        }catch (NotFreeException ex){
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * This method creates the god and add it to the player
     * @param p player that own the god
     * @param name name of the god
     */
    public void createGod(Player p, String name){
        God god = new God(name, p);
        p.setGod(god);
        if(withGods) {
            for(ChosenGodMessage c: chosenGods){
                if(c.getName().equals(name))
                    updatedChosenGods.remove(c);
            }
            if(!players.get(currentTurn).equals(challenger))
            updateTurn();
            if(players.get(currentTurn).getGod()==null)
            notify(MessageType.GODLISTMESSAGE, new GodListMessage(getTurnPlayer(), updatedChosenGods));
        }else{
            if(!players.get(currentTurn).equals(challenger))
                updateTurn();
        }
    }

    /**
     * This method initializes the power of the gods in game, it reads a config file where there are the information about the gods
     */
    public void initGods() {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(godsConf)) {
            // Convert JSON File to Java Object
            Type godMapType = new TypeToken<Map<String, GodAttribute>>() {}.getType();
            Map<String, GodAttribute> attributes = gson.fromJson(reader, godMapType);

            for (Player p: players) {
                God god = p.getGod();
                List<God> opponentGods = p.getOpponents().stream().map(Player::getGod).collect(Collectors.toList());
                GodAttribute godAttribute = attributes.get(god.getGodName());

                if(godAttribute == null) {
                    throw new BadConfigurationException("Godname: " + god.getGodName());
                }

                // Initializing the base values
                god.setMovableTimes(godAttribute.movableTimes);
                god.setBuildableTimes(godAttribute.buildableTimes);

                // Subscribing both the god and its opponents to the given events
                for(String event: godAttribute.events.get(GodAttribute.Subject.SELF)) {
                    god.getEventManager().subscribe(event, god);
                }
                for(String event: godAttribute.events.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        god.getEventManager().subscribe(event, g);
                    }
                }

                // Initializing both the god's movements and applying effects to opponents'
                for(String selfMovement: godAttribute.movements.get(GodAttribute.Subject.SELF)) {
                    god.decorateMovement(selfMovement);
                }
                for(String opponentMovement: godAttribute.movements.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        g.decorateMovement(opponentMovement);
                    }
                }

                // Initializing both the god's builds and applying effects to opponents'
                for(String selfBuild: godAttribute.builds.get(GodAttribute.Subject.SELF)) {
                    god.decorateBuild(selfBuild);
                }
                for(String opponentBuild: godAttribute.builds.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        g.decorateBuild(opponentBuild);
                    }
                }

                // Initializing both the god's wins and applying effects to opponents'
                for(String selfWin: godAttribute.wins.get(GodAttribute.Subject.SELF)) {
                    god.decorateWin(selfWin);
                }
                for(String opponentWin: godAttribute.wins.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        g.decorateWin(opponentWin);
                    }
                }
            }
            Map<String, String> playersMap= new HashMap<>();
            for(Player p: players)
                playersMap.put(p.getNickname(), p.getGod().getGodName());
            notify(MessageType.PLAYERSMESSAGE, new PlayersMessage(playersMap, getTurnPlayer()));
        } catch (IOException | BadConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method initializes the power of the gods in game, it reads a config file where there are the information about the gods
     */
    public void initGodsLater() {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(godsConf)) {
            // Convert JSON File to Java Object
            Type godMapType = new TypeToken<Map<String, GodAttribute>>() {}.getType();
            Map<String, GodAttribute> attributes = gson.fromJson(reader, godMapType);

            for (Player p: players) {
                God god = p.getGod();
                List<God> opponentGods = p.getOpponents().stream().map(Player::getGod).collect(Collectors.toList());
                GodAttribute godAttribute = attributes.get(god.getGodName());

                if(godAttribute == null) {
                    throw new BadConfigurationException("Godname: " + god.getGodName());
                }

                // Initializing the base values
                god.setMovableTimes(godAttribute.movableTimes);
                god.setBuildableTimes(godAttribute.buildableTimes);

                // Subscribing both the god and its opponents to the given events
                for(String event: godAttribute.events.get(GodAttribute.Subject.SELF)) {
                    god.getEventManager().subscribe(event, god);
                }
                for(String event: godAttribute.events.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        god.getEventManager().subscribe(event, g);
                    }
                }

                // Initializing both the god's movements and applying effects to opponents'
                for(String selfMovement: godAttribute.movements.get(GodAttribute.Subject.SELF)) {
                    god.decorateMovement(selfMovement);
                }
                for(String opponentMovement: godAttribute.movements.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        g.decorateMovement(opponentMovement);
                    }
                }

                // Initializing both the god's builds and applying effects to opponents'
                for(String selfBuild: godAttribute.builds.get(GodAttribute.Subject.SELF)) {
                    god.decorateBuild(selfBuild);
                }
                for(String opponentBuild: godAttribute.builds.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        g.decorateBuild(opponentBuild);
                    }
                }

                // Initializing both the god's wins and applying effects to opponents'
                for(String selfWin: godAttribute.wins.get(GodAttribute.Subject.SELF)) {
                    god.decorateWin(selfWin);
                }
                for(String opponentWin: godAttribute.wins.get(GodAttribute.Subject.OPPONENTS)) {
                    for(God g: opponentGods) {
                        g.decorateWin(opponentWin);
                    }
                }
            }
            Map<String, String> playersMap= new HashMap<>();
            for(Player p: players)
                playersMap.put(p.getNickname(), p.getGod().getGodName());
        } catch (IOException | BadConfigurationException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method returns a list of SimpleCoordinates that are reachable for a worker
     * @param w worker of which we want to know the reachable cells
     * @return the worker's reachable cells
     */
    private List<SimpleCoordinates> getReachableCells(Worker w){
        return w.reachableCells().stream().map(Coordinates::getSimpleCoor).collect(Collectors.toList());
    }

    /**
     * This method returns a map with the simple coordinates that are eligible for a build by the worker (in the key set) and
     * a list with the levels that the worker can build in each coordinates (in the value set)
     * @param w worker of which we want to know the buildhable cells
     * @return the worker's buildable cells
     */
    private Map<SimpleCoordinates, List<Integer>> getBuildableCells(Worker w){
        Map<Coordinates, List<Integer>> map = w.buildableCells();
        return map.keySet().stream().collect(Collectors.toMap(Coordinates::getSimpleCoor, map::get));
    }

    /**
     * This method manages the movement of a worker and notifies to the views the updates
     * @param w coordinates of the worker that will move
     * @param c coordinates of the destination
     * @throws EndGameException this exception is thrown when, after a move, the owner of the worker wins. This exception is
     * caught by the controller that invokes this method
     */
    public void workerMove(Coordinates w, Coordinates c) throws EndGameException{
        Worker worker = w.getTopBlock().getWorkerOnTop();
        try {
            worker.moveTo(c);
            sendUpdate();
        } catch (HasWonException e) {
            sendLastUpdate();
            notify(MessageType.ENDGAME, new EndGameMessage(worker.getPlayer().getNickname(), true));
            throw new EndGameException("Winner: " + worker.getPlayer().getNickname());
        }
    }

    /**
     * This method manages the build of a map component; after the build it notifies to the views the updates
     * @param w coordinate of the worker that builds
     * @param c coordinates where the worker builds
     * @param level level of the new map component
     */
    public void workerBuild(Coordinates w, Coordinates c, Integer level){
        w.getTopBlock().getWorkerOnTop().buildTo(c,level);
        sendUpdate();
    }

    /**
     * This method removes a player from the game. If after the remove there will is only one player, this method will notify to the views the winner
     * @param p player that will be removed
     * @param v server view part of pattern observer
     * @throws EndGameException this exception is thrown when, after the remove, there is only one player which will be
     * the winner. This exception will be caught by the controller that invoked this method
     */
    public void removePlayer(Player p, View v) throws EndGameException{
        for(Worker worker: p.getWorkers()) {
            worker.getCurrentPosition().removeWorkerOnTop();
        }
        players.remove(p);
        if(currentTurn == players.size()) {
            currentTurn = 0;
        }
        this.removeObserver(v);
        if(players.size()==1) {
            sendLastUpdate(p);
            notify(MessageType.ENDGAME, new EndGameMessage(players.get(0).getNickname(), true));
            throw new EndGameException(p.getNickname());
        }else{
            for(Player player : players){
                player.getGod().resetPowers();
                initGodsLater();
            }
        }
        sendUpdate();
    }

    /**
     * This method updates the turn
     */
    public void updateTurn(){
        currentTurn = (currentTurn+1)%players.size();
    }

    /**
     * This method manages the end of the turn, it resets the triggers of the player that end the turn, updates the turn
     * and resets the timesMoved and timesBuild of the player, then it notifies to the
     * views the information about the new turn
     */
    public void endTurn() {
        Player endingTurn = players.get(currentTurn);

        endingTurn.getGod().resetTriggers();
        endingTurn.getGod().getLogTurn().reset();

        for(Worker worker: endingTurn.getWorkers()) {
            worker.setTimesMoved(0);
            worker.setTimesBuilt(0);
        }

        updateTurn();

        sendUpdate();
    }

    /**
     * Method used to send a normal update message; compiles all the actions the players can take, then notifies it
     */
    public void sendUpdate() {
        Map<SimpleCoordinates,List<SimpleCoordinates>> reach = new HashMap<>();
        Player currentPlayer = players.get(currentTurn);
        Map<SimpleCoordinates,Map<SimpleCoordinates, List<Integer>>> build = new HashMap<>();

        for(Worker worker: currentPlayer.getWorkers()) {
            reach.put(worker.getCurrentPosition().getSimpleCoor(), getReachableCells(worker));
            build.put(worker.getCurrentPosition().getSimpleCoor(), getBuildableCells(worker));
        }

        notify(MessageType.UPDATE, new UpdateMessage(currentPlayer.getNickname(), board.getUpdate(), reach, build, currentPlayer.canPass(), currentPlayer.getGod().getLogTurn().canUndo()));
    }
    /**
     * Special update, used for the update caused by a winning move
     */
    public void sendLastUpdate() {
        Player currentPlayer = players.get(currentTurn);
        notify(MessageType.UPDATE, new UpdateMessage(currentPlayer.getNickname(), board.getUpdate(), new HashMap<>(), new HashMap<>(), false, false, false));
    }

    public void sendLastUpdate(Player p) {
        notify(MessageType.UPDATE, new UpdateMessage(p.getNickname(), board.getUpdate(), new HashMap<>(), new HashMap<>(), false, false, false));
    }

    /**
     * Special update, used during the phase of worker placement
     */
    public void sendInitUpdate() {
        int i = 0;
        Player currentPlayer = players.get(currentTurn);
        boolean canPass = currentPlayer.getWorkers() != null && currentPlayer.getWorkers().size() == board.getNWorkers();
        if(canPass){
            updateTurn();
            currentPlayer = players.get(currentTurn);
            canPass = currentPlayer.getWorkers() != null && currentPlayer.getWorkers().size() == board.getNWorkers();
        }
        for (Player p: players) {
            if(p.getWorkers().size() < board.getNWorkers())
                i++;
        }
        if(i>0)
            notify(MessageType.UPDATE, new UpdateMessage(currentPlayer.getNickname(), board.getUpdate(), new HashMap<>(), new HashMap<>(), canPass, currentPlayer.getGod().getLogTurn().canUndo(), !canPass));
        else
            sendUpdate();
    }
}
