package it.polimi.ingsw.model.god.log;

import it.polimi.ingsw.model.god.GodEventManager;

import java.util.List;

/**
 * Class used to log and undo a trigger notification
 */
public class LogTrigger extends LogAction {
    /**
     * The object used to synchronize the revert of parallel trigger actions
     */
    private static final Object triggerLock = new Object();
    /**
     * The referenced event manager
     */
    private final GodEventManager eventManager;
    /**
     * The logged event
     */
    private final String event;
    /**
     * The connected data
     */
    private final Integer data;

    /**
     * Constructor for the class
     * @param eventManager The event manager notifying the event
     * @param event The event being notified
     * @param data The connected data
     */
    public LogTrigger(GodEventManager eventManager, String event, Integer data) {
        super.setType(actionType.TRIGGER);
        this.eventManager = eventManager;
        this.event = event;
        this.data = data;
    }

    /**
     * Reverts the notification of an event
     */
    @Override
    public void revert() {
        List<Thread> threadList = super.revertSubActions();
        synchronized (triggerLock) {
            eventManager.modifyTrigger(event, -data);
        }

        // Waits for the subactions to be reverted by the created threads
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}