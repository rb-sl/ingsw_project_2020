package it.polimi.ingsw.view;

import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.misc.Observer;

/**
 * Class used to realize the observer pattern in the view
 */
public class MessageRouter implements Observer<MessageEnvelope> {
    /**
     * The connected view
     */
    private final View view;

    /**
     * Constructor for the class
     * @param view The connected view
     */
    public MessageRouter(View view) {
        this.view = view;
    }

    /**
     * Update as per the observer pattern
     * @param m The message being notified
     */
    @Override
    public void update(MessageEnvelope m) {
        view.handleMessage(m);
    }
}
