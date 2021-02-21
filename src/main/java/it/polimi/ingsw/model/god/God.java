package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.god.builds.*;
import it.polimi.ingsw.model.god.log.LogTurn;
import it.polimi.ingsw.model.god.movements.*;
import it.polimi.ingsw.model.god.wins.*;
import it.polimi.ingsw.misc.Observer;

import java.util.*;

/**
 * Class used to implement the gods. It allows creating and applying all effects associated with a god card in the game
 */
public class God implements Observer<GodTrigger>  {
    /**
     * God's name as per card
     */
    private final String godName;
    /**
     * Player using the god
     */
    private final Player player;

    /**
     * Defines the times a worker associated to this god can move in a turn
     */
    private Integer movableTimes;
    /**
     * Defines the times a worker associated to this god can build in a turn
     */
    private Integer buildableTimes;

    /**
     * Object defining the movement behaviour for associated workers
     */
    private GodMovement movement;
    /**
     * Object defining the building behaviour for associated workers
     */
    private GodBuild build;
    /**
     * Object defining the win behaviour for associated workers
     */
    private GodWin win;

    /**
     * Enumeration specifying the type of power
     */
    public enum PowerType { MOVEMENT, BUILD, WIN }

    /**
     * Map containing the lists of powers applied by the god
     */
    private final Map<PowerType, List<String>> godPowers;

    /**
     * Map associating a trigger to the number of times it is applied to the god
     */
    private final Map<String, Integer> triggers;
    /**
     * Object used to handle event updates in the observer pattern
     */
    private final GodEventManager eventManager;

    /**
     * Logger of events regarding the god
     */
    private final LogTurn logTurn;

    /**
     * Constructor for the class; creates base behaviours
     * @param godName Name of the god
     * @param player The player associated with this god card
     */
    public God(String godName, Player player)  {
        this.godName = godName;
        this.player = player;

        this.movableTimes = 1;
        this.buildableTimes = 1;

        logTurn = new LogTurn();

        movement = new BaseMovement();
        build = new BaseBuild();
        win = new BaseWin();

        godPowers = new HashMap<>();
        godPowers.put(PowerType.MOVEMENT, new ArrayList<>());
        godPowers.put(PowerType.BUILD, new ArrayList<>());
        godPowers.put(PowerType.WIN, new ArrayList<>());

        triggers = new HashMap<>();
        eventManager = new GodEventManager(this);
    }

    /**
     * Decorates the movement based on the given power
     * @param movement The behaviour to add
     * @throws BadConfigurationException If the movement doesn't correspond to any known power
     */
    public void decorateMovement(String movement) throws BadConfigurationException {
        List<String> moveList = godPowers.get(PowerType.MOVEMENT);
        if(moveList.contains(movement)) {
            return;
        }

        moveList.add(movement);
        this.movement = GodPowers.decorateMove(movement, this.movement);
    }

    /**
     * Decorates the build behaviour based on the given power
     * @param build The behaviour to add
     * @throws BadConfigurationException If the movement doesn't correspond to any known power
     */
    public void decorateBuild(String build) throws BadConfigurationException {
        List<String> buildList = godPowers.get(PowerType.BUILD);
        if(buildList.contains(build)) {
            return;
        }

        buildList.add(build);
        this.build = GodPowers.decorateBuild(build, this.build);
    }

    /**
     * Decorates the win behaviour based on the given power
     * @param win The behaviour to add
     * @throws BadConfigurationException If the win doesn't correspond to any known power
     */
    public void decorateWin(String win) throws BadConfigurationException {
        List<String> winList = godPowers.get(PowerType.WIN);
        if(winList.contains(win)) {
            return;
        }

        winList.add(win);
        this.win = GodPowers.decorateWin(win, this.win);
    }

    /**
     * Resets the powers
     */
    public void resetPowers() {
        movement = new BaseMovement();
        build = new BaseBuild();
        win = new BaseWin();

        godPowers.forEach((k,e) -> e.clear());
    }

    /**
     * Checks if a trigger is active
     * @param event The event to check
     * @return True if the trigger is active
     */
    public Boolean isTriggered(String event) {
        Integer trigNumber = triggers.get(event);
        return !(trigNumber == null) && trigNumber > 0;
    }

    /**
     * Checks if the god has been decorated with a specified behaviour
     * @param powerType The type of power being investigated
     * @param power The name of the power being investigated
     * @return True if the god has been decorated with the given power
     */
    public boolean hasPower(PowerType powerType, String power) {
        return godPowers.get(powerType).contains(power);
    }

    /**
     * Updates or creates a trigger as per the observer pattern
     * @param trigger The trigger's information
     */
    @Override
    public void update(GodTrigger trigger) {
        if (triggers.containsKey(trigger.getEvent())) {
            try {
                triggers.compute(trigger.getEvent(), (e, d) -> (Math.max(d + trigger.getData(), 0)));
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
        else {
            triggers.put(trigger.getEvent(), trigger.getData());
        }
    }

    /**
     * Resets the god's triggers; needs to be called at the end of each turn
     */
    public void resetTriggers() {
        triggers.replaceAll((event, value) -> value = 0);
    }

    /**
     * Getter for the god's name
     * @return The god's name
     */
    public String getGodName() {
        return godName;
    }

    /**
     * Getter for the movement behaviour
     * @return The object connected to the movement behaviour
     */
    public GodMovement getMove() {
        return movement;
    }

    /**
     * Getter for the building behaviour
     * @return The object connected to the building behaviour
     */
    public GodBuild getBuild() {
        return build;
    }

    /**
     * Getter for the winning behaviour
     * @return The object connected to the winning behaviour
     */
    public GodWin getWin() {
        return win;
    }

    /**
     * Setter for the movement behaviour
     * @param movement The behaviour to set
     */
    public void setMove(GodMovement movement) {
        this.movement = movement;
    }

    /**
     * Setter for the build behaviour
     * @param build The behaviour to set
     */
    public void setBuild(GodBuild build) {
        this.build = build;
    }

    /**
     * Setter for the win behaviour
     * @param win The behaviour to set
     */
    public void setWin(GodWin win) {
        this.win = win;
    }

    /**
     * Getter for the maximum number of times a connected worker can move
     * @return The number of times a worker may move
     */
    public Integer getMovableTimes() {
        return movableTimes;
    }

    /**
     * Getter for the maximum number of times a connected worker can build
     * @return The number of times a worker may build
     */
    public Integer getBuildableTimes() {
        return buildableTimes;
    }

    /**
     * Setter for the movableTimes attribute
     * @param movableTimes The value to be set
     */
    public void setMovableTimes(Integer movableTimes) {
        this.movableTimes = movableTimes;
    }

    /**
     * Setter for the buildableTimes attribute
     * @param buildableTimes The value to be set
     */
    public void setBuildableTimes(Integer buildableTimes) {
        this.buildableTimes = buildableTimes;
    }

    /**
     * Getter for the associated player
     * @return The associated player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Getter for the event manager
     * @return The god's event manager
     */
    public GodEventManager getEventManager() {
        return eventManager;
    }

    /**
     * Getter for the logger
     * @return The god's event logger
     */
    public LogTurn getLogTurn() {
        return logTurn;
    }

    /**
     * Confronts two gods - autogenerated
     * @param o The object to confront
     * @return True if the two objects represent the same god
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        God god = (God) o;
        return godName.equals(god.godName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(godName);
    }
}
