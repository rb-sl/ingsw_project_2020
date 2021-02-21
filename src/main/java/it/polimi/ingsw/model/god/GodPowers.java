package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.misc.BiFunction;
import it.polimi.ingsw.model.god.builds.*;
import it.polimi.ingsw.model.god.movements.*;
import it.polimi.ingsw.model.god.wins.BaseWinDecorator;
import it.polimi.ingsw.model.god.wins.Jump2;
import it.polimi.ingsw.model.god.wins.NotOnPerimeter;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the type of power to assign to each god through decorator, allowing to avoid switch..case block statement
 */
public class GodPowers {
    /**
     * Map containing each movement type available for gods and a reference to the right constructor for that type
     */
    private static final Map<String, BiFunction<GodMovement, BaseMovementDecorator>> moveMap = new HashMap<>();
    /**
     * Map containing each build type available for gods and a reference to the right constructor for that type
     */
    private static final Map<String, BiFunction<GodBuild, BaseBuildDecorator>> buildMap = new HashMap<>();
    /**
     * Map containing each win type available for gods and a reference to the right constructor for that type
     */
    private static final Map<String, BiFunction<GodWin, BaseWinDecorator>> winMap = new HashMap<>();

    /*
      Executed once, only when for the first time one object of this class is instantiated or a static
      method is used. It's executed before the constructor.
     */
    static {
        moveMap.put("Bump", (godMovement) -> new Bump(godMovement));
        moveMap.put("CanMoveAndBuild", (godMovement) -> new CanMoveAndBuild(godMovement));
        moveMap.put("CantMoveHighest", (godMovement) -> new CantMoveHighest(godMovement));
        moveMap.put("CantMoveUp", (godMovement) -> new CantMoveUpTriggered(godMovement));
        moveMap.put("IndefinitelyOnPerimeter", (godMovement) -> new IndefinitelyOnPerimeter(godMovement));
        moveMap.put("InhibitMoveUp", (godMovement) -> new InhibitMoveUp(godMovement));
        moveMap.put("MoveAfterBuild", (godMovement) -> new MoveAfterBuild(godMovement));
        moveMap.put("MoveOnOpponent", (godMovement) -> new MoveOnOpponent(godMovement));
        moveMap.put("NotOnPreviousPosition", (godMovement) -> new NotOnPreviousPosition(godMovement));
        moveMap.put("Swap", (godMovement) -> new Swap(godMovement));

        buildMap.put("BuildBeforeMove", (godBuild) -> new BuildBeforeMove(godBuild));
        buildMap.put("CanBuildAndMove", (godBuild) -> new CanBuildAndMove(godBuild));
        buildMap.put("DomeEverywhere", (godBuild) -> new DomeEverywhere(godBuild));
        buildMap.put("NoBlockNearWorker", (godBuild) -> new NoBlockNearWorker(godBuild));
        buildMap.put("NoDomeOnSecond", (godBuild) -> new NoDomeOnSecond(godBuild));
        buildMap.put("NoPerimeterOnSecond", (godBuild) -> new NoPerimeterOnSecond(godBuild));
        buildMap.put("NotOnPreviousBuild", (godBuild) -> new NotOnPreviousBuild(godBuild));
        buildMap.put("OnPreviousBuild", (godBuild) -> new OnPreviousBuild(godBuild));

        winMap.put("Jump2", (godWin) -> new Jump2(godWin));
        winMap.put("NotOnPerimeter", (godWin) -> new NotOnPerimeter(godWin));
    }

    /**
     * Method called to decorate the move power of the god
     * @param movement The type of movement
     * @param wrapper The object to be decorated
     * @return The decorated object
     * @throws BadConfigurationException When the specified movement power is not found
     */
    public static BaseMovementDecorator decorateMove(String movement, GodMovement wrapper) throws BadConfigurationException {
        BiFunction<GodMovement, BaseMovementDecorator> biFunction = moveMap.get(movement);
        if (biFunction == null)
            throw new BadConfigurationException("Movement power not found: " + movement);
        return biFunction.applyPower(wrapper);
    }

    /**
     * Method called to decorate the build power of the god
     * @param build The type of build
     * @param wrapper The object to be decorated
     * @return The decorated object
     * @throws BadConfigurationException When the specified build power is not found
     */
    public static BaseBuildDecorator decorateBuild(String build, GodBuild wrapper) throws BadConfigurationException {
        BiFunction<GodBuild, BaseBuildDecorator> biFunction = buildMap.get(build);
        if (biFunction == null)
            throw new BadConfigurationException("Build power not found: " + build);
        return biFunction.applyPower(wrapper);
    }

    /**
     * Method called to decorate the win power of the god
     * @param win The type of win
     * @param wrapper The object to be decorated
     * @return The decorated object
     * @throws BadConfigurationException When the specified win power is not found
     */
    public static BaseWinDecorator decorateWin(String win, GodWin wrapper) throws BadConfigurationException {
        BiFunction<GodWin, BaseWinDecorator> biFunction = winMap.get(win);
        if (biFunction == null)
            throw new BadConfigurationException("Win power not found: " + win);
        return biFunction.applyPower(wrapper);
    }
}
