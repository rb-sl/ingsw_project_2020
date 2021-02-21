package it.polimi.ingsw.model.god.wins;

import it.polimi.ingsw.misc.TripleState;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodWin;

/**
 * Decorator Class inhibiting the possibility of winning on the perimeter
 */
public class NotOnPerimeter extends BaseWinDecorator {
    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public NotOnPerimeter(GodWin wrapped) {
        super(wrapped);
    }

    /**
     * Overridden isWinner, does not allow the win on perimeter
     * @param worker The worker checking its win conditions
     * @return A false triple state if on the perimeter, indifferent otherwise
     */
    @Override
    public TripleState isWinner(Worker worker) {
        TripleState state = super.isWinner(worker);
        if(!worker.getCurrentPosition().isOnPerimeter()) {
            return state.compare(TripleState.INDIFFERENT);
        }
        return state.compare(TripleState.FALSE);
    }
}
