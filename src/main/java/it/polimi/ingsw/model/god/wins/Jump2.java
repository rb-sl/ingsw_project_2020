package it.polimi.ingsw.model.god.wins;

import it.polimi.ingsw.misc.TripleState;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodWin;

/**
 * Decorator class used to win if a worker moves down two or more levels
 */
public class Jump2 extends BaseWinDecorator {
    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public Jump2(GodWin wrapped) {
        super(wrapped);
    }

    /**
     * Overridden isWinner
     * @param worker The worker checking its win conditions
     * @return A triple state true if the height difference is at least 2, indifferent otherwise
     */
    @Override
    public TripleState isWinner(Worker worker) {
        TripleState state = super.isWinner(worker);
        if(worker.getPreviousPosition().heightDifference(worker.getCurrentPosition()) >= 2) {
            return state.compare(TripleState.TRUE);
        }
        return state.compare(TripleState.INDIFFERENT);
    }
}
