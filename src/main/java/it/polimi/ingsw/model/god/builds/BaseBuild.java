package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.exception.CantBuildException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.god.log.LogTurn;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used as base building behaviour
 */
public class BaseBuild implements GodBuild {
    /**
     * Checks if a worker can build the specified block on the specified coordinates
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        List<Integer> levelList = new ArrayList<>();
        if(worker.getTimesMoved() > 0
                && worker.getPlayer().getWorkers().stream().map(w -> worker.equals(w) || w.getTimesMoved() == 0 && w.getTimesBuilt() == 0).reduce(true, (a,b) -> a && b)
                && worker.getTimesBuilt() < worker.getPlayer().getGod().getBuildableTimes()
                && worker.getCurrentPosition().isAdjacentTo(coordinates)
                && coordinates.getTopBlock().isFree())
            levelList.add(coordinates.getHeight() + 1);
        return levelList;
    }

    /**
     * Default build behaviour
     * @param worker Worker creating the block
     * @param coordinates Coordinates destination of the new build
     * @param buildType The level (1 to 4) of the new build
     * @throws CantBuildException If the control is negative
     * @throws WrongBlockException If the height consists in an illegal value
     */
    @Override
    public void buildTo(Worker worker, Coordinates coordinates, Integer buildType) throws CantBuildException, WrongBlockException {
        if(!worker.getPlayer().getGod().getBuild().canBuild(worker, coordinates).contains(buildType)) {
            throw new CantBuildException();
        }
        coordinates.addBlockOnTop(buildType);

        // Logs the build action
        worker.getPlayer().getGod().getLogTurn().addBuild(LogTurn.order.MAIN, worker, coordinates);
    }
}
