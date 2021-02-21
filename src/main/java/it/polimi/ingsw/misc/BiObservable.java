package it.polimi.ingsw.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create the pattern Observer over two message types
 * @param <T> first message type
 * @param <V> second message type
 */
public class BiObservable<T, V> {
    /**
     * List of observers
     */
    private List<BiObserver<T, V>> biObserverList = new ArrayList<>();

    /**
     * Adds a biobserver to the observer list
     * @param b The biobserver to be added
     */
    public void addObserver(BiObserver<T, V> b){
        biObserverList.add(b);
    }

    /**
     * Adds a list of biobservers
     * @param l The list containing the biobservers to be added
     */
    public void addObservers(List<BiObserver<T, V>> l){
        biObserverList.addAll(l);
    }

    /**
     * Removes a biobserver
     * @param o The observer to be removes
     */
    public void removeObserver(BiObserver<T, V> o){
        biObserverList.remove(o);
    }

    /**
     * Notifies all the biobservers of the occurrence of an event
     * @param message The first message to be notified
     * @param v The second message to be notified
     */
    public void notify(T message, V v){
        for(BiObserver<T,V> biObserver : biObserverList){
            biObserver.update(message, v);
        }
    }
}
