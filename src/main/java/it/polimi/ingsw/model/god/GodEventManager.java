package it.polimi.ingsw.model.god;

import it.polimi.ingsw.misc.Observable;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.god.log.LogTurn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used in the context of an observer pattern; it's used by a God to notify all events of interest to others
 */
public class GodEventManager {
    /**
     * Map associating an event (as String) to the list of its observables
     */
    private final Map<String, Observable<GodTrigger>> observables;
    /**
     * The associated god
     */
    private final God god;

    /**
     * Creator for the class
     * @param god The owner of the event manager
     */
    public GodEventManager(God god) {
        observables = new HashMap<>();
        this.god = god;
    }

    /**
     * Adds a listener to the relative list, based on a specified event
     * @param event Event the listener is subscribing to
     * @param listener Listener to be subscribed to the event
     */
    public void subscribe(String event, Observer<GodTrigger> listener) {
        Observable<GodTrigger> observable = observables.get(event);

        if(observable == null) {
            observables.put(event, new Observable<>());
            observable = observables.get(event);
        }

        observable.addObserver(listener);
        listener.update(new GodTrigger(event,0));
    }

    /**
     * Adds a list of listeners to the relative list, based on a specified event
     * @param event Event the listener is subscribing to
     * @param listeners List of listeners to be subscribed to the event
     */
    public void subscribeAll(String event, List<Observer<GodTrigger>> listeners) {
        for(Observer<GodTrigger> l: listeners) {
            subscribe(event, l);
        }
    }

    /**
     * Removes a listener from an event list
     * @param event Event to be unsubscribed from
     * @param listener Listener to be unsubscribed from the event
     */
    public void unsubscribe(String event, Observer<GodTrigger> listener) {
        Observable<GodTrigger> observable = observables.get(event);

        if(observable != null) {
            observable.removeObserver(listener);
        }
    }

    /**
     * Removes all listeners from an event list
     * @param eventType Event from which all listeners have to be unsubscribed
     */
    public void unsubscribeAll(String eventType) {
        observables.get(eventType).removeAll();
    }

    /**
     * Notifies all listeners of the occurrence of an event, logging it
     * @param event Event to notify
     * @param data Value connected to the event
     */
    public void notifyTrigger(String event, Integer data) {
        modifyTrigger(event, data);
        // Logging the notification
        god.getLogTurn().addEvent(LogTurn.order.AFTER,this, event, data);
    }

    /**
     * Notifies all listeners of the occurrence of an event
     * @param event Event to notify
     * @param data Value connected to the event
     */
    public void modifyTrigger(String event, Integer data) {
        Observable<GodTrigger> observing = observables.get(event);

        if(observing != null) {
            observing.notify(new GodTrigger(event, data));
        }
    }
}