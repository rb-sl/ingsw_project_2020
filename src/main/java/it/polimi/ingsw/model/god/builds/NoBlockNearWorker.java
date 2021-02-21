package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.God;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Decorator preventing the build of any non-dome block near the workers of a specified god
 */
public class NoBlockNearWorker extends BaseBuildDecorator {
    /**
     * The name of the trigger used to recognize the caster
     */
    private static final String castedPower = "NoBlockNearWorker";

    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public NoBlockNearWorker(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, not allowing the action near the caster's workers
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        Coordinates coor;
        List<Integer> levelList = super.canBuild(worker, coordinates);

        List<Coordinates> casterCoordinates = worker.getPlayer().getOpponents().stream().filter(player -> !player.getGod().hasPower(God.PowerType.BUILD, castedPower))
                .flatMap(p -> p.getWorkers().stream()).map(Worker::getCurrentPosition).collect(Collectors.toList());

        // Checks for the caster's player positions; removes all blocks except domes if a caster's worker is nearby
        for(Coordinates c: casterCoordinates) {
            // Special case where the worker has hypothesized a move over a caster's worker
            if(c.equals(worker.getCurrentPosition())) {
                if(worker.getPlayer().getGod().hasPower(God.PowerType.MOVEMENT, "Swap") && worker.getPreviousPosition().isAdjacentTo(coordinates)
                        || worker.getPlayer().getGod().hasPower(God.PowerType.MOVEMENT, "Bump") && ((coor = worker.getCurrentPosition().getOpposedOf(worker.getPreviousPosition())) != null && coor.isAdjacentTo(coordinates))) {
                    levelList.removeIf(level -> level != coordinates.getBoard().getMaxLevel() + 1 ||
                        !coordinates.getHeight().equals(coordinates.getBoard().getMaxLevel()));
                }
            } else if(c.isAdjacentTo(coordinates))
                levelList.removeIf(level -> level != coordinates.getBoard().getMaxLevel() + 1 ||
                        !coordinates.getHeight().equals(coordinates.getBoard().getMaxLevel()));
        }

        return levelList;
    }
}
