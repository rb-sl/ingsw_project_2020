package it.polimi.ingsw.messages;

import it.polimi.ingsw.exception.MessageTypeNotFound;
import it.polimi.ingsw.misc.UniFunction;
import it.polimi.ingsw.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the type of message incoming allowing to avoid switch..case block statement
 */
public class MessageAllocator {
    /**
     * Map containing the type of the message as key and a reference to a function to invoke depending on the
     * message type
     */
    private static final Map<MessageType, UniFunction<MessageEnvelope>> messageMap = new HashMap<>();

    /**
     * Method to initialize messageMap
     * @param view object from which the method in the map are invoked
     */
    public static void initMessageAllocator(View view) {
        messageMap.put(MessageType.GODINIT, (message) -> view.godInitAction(message));
        messageMap.put(MessageType.CHOSESTARTER,  (message) -> view.chooseStarterAction(message));
        messageMap.put(MessageType.WORKERINIT,  (message) -> view.workerInitAction(message));
        messageMap.put(MessageType.MOVE,  (message) -> view.moveAction(message));
        messageMap.put(MessageType.BUILD,  (message) -> view.buildAction(message));
        messageMap.put(MessageType.UNDO,  (message) -> view.undoAction(message));
        messageMap.put(MessageType.PASS,  (message) -> view.passAction(message));
        messageMap.put(MessageType.LOSE,  (message) -> view.loseAction(message));
    }

    /**
     * Method to retrieve the right function based on the type of the message contained in envelope
     * @param envelope envelope containing the message
     * @throws MessageTypeNotFound thrown when the specified message type is not a valid type
     */
    public static void allocateMessage(MessageEnvelope envelope) throws MessageTypeNotFound {
        UniFunction<MessageEnvelope> uniFunction = messageMap.get(envelope.getType());
        if (uniFunction == null)
            throw new MessageTypeNotFound(envelope.getType().toString());
        uniFunction.allocateMessage(envelope);
    }
}
