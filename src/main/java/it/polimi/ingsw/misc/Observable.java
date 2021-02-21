package it.polimi.ingsw.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create the Observer pattern
 * @param <T> first parameter
 */
public class Observable<T> {
    /**
     * List of observing objects
     */
    private final List<Observer<T>> observerList = new ArrayList<>();

    /**
     * Adds an observer to the list
     * @param o The observer to be added
     */
    public void addObserver(Observer<T> o){
        observerList.add(o);
    }

    /**
     * Adds multiple observers
     * @param l The list of observers to be added
     */
    public void addObservers(List<Observer<T>> l){
        observerList.addAll(l);
    }

    /**
     * Removes an observer from the list
     * @param o The observer to be removed
     */
    public void removeObserver(Observer<T> o){
        observerList.remove(o);
    }

    /**
     * Removes all the observers
     */
    public void removeAll(){
        observerList.clear();
    }

    /**
     * Notifies the message to all observers
     * @param message The message to be notified
     */
    public void notify(T message){
        for(Observer<T> observer : observerList){
            observer.update(message);
        }
    }
}
