package it.polimi.ingsw.misc;

/**
 * Class used to parametrize a function with three parameters
 * @param <V> The first parameter type
 * @param <G> The second parameter type
 * @param <S> The third parameter type
 * @param <OUT> The return type
 */
public interface QuadriFunction<V, G, S, OUT> {
    OUT applyFunction(V view, G gson, S message);
}
