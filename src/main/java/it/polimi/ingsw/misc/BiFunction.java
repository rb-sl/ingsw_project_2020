package it.polimi.ingsw.misc;

/**
 * Interface to parametrize a function with one argument
 * @param <W> Type of the parameter passed to the function
 * @param <OUT> Type to be returned
 */
public interface BiFunction<W, OUT> {
    OUT applyPower(W wrapper);
}
