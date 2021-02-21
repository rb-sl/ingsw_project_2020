package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.exception.MessageTypeNotFound;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.misc.BiObservable;
import it.polimi.ingsw.misc.BiObserver;

/**
 * Abstract class for the view
 */
public abstract class View implements BiObserver<MessageType, SimpleMessage> {
    /**
     * Observable for the godInit handler
     */
    private final BiObservable<GodInitMessage, View> godInitBiObservable;
    /**
     * Observable for the choseStarter handler
     */
    private final BiObservable<StarterMessage, View> choseStarterBiObservable;
    /**
     * Observable for the workerInit handler
     */
    private final BiObservable<WorkerInitMessage, View> workerInitBiObservable;
    /**
     * Observable for the move handler
     */
    private final BiObservable<MoveMessage, View> moveBiObservable;
    /**
     * Observable for the build handler
     */
    private final BiObservable<BuildMessage, View> buildBiObservable;
    /**
     * Observable for the pass handler
     */
    private final BiObservable<SimpleMessage, View> passBiObservable;
    /**
     * Observable for the undo handler
     */
    private final BiObservable<UndoMessage, View> undoBiObservable;
    /**
     * Observable for the lose handler
     */
    private final BiObservable<SimpleMessage, View> loseBiObservable;
    /**
     * Player that owns the view
     */
    private final Player player;
    /**
     * Flag stating the endGame for the view
     */
    private boolean end = false;
    /**
     * The gson object
     */
    private final Gson gson;

    /**
     * Constructor
     * @param player this is the owner of the view
     * @param godInitHandler Handler for godInit actions
     * @param chooseStarterHandler Handler for the chose of the starter player
     * @param workerInitHandler Handler for the initialization of the workers
     * @param moveHandler Handler for the move action
     * @param buildHandler Handler for the build action
     * @param passHandler Handler for the pass
     * @param undoHandler Handler for the undo
     * @param loseHandler Handler for the lose
     */
    protected View(Player player, GodInitHandler godInitHandler, ChooseStarterHandler chooseStarterHandler, WorkerInitHandler workerInitHandler, MoveHandler moveHandler, BuildHandler buildHandler, PassHandler passHandler, UndoHandler undoHandler, LoseHandler loseHandler) {
        gson = new Gson();
        this.godInitBiObservable = new BiObservable<>();
        this.choseStarterBiObservable = new BiObservable<>();
        this.workerInitBiObservable = new BiObservable<>();
        this.moveBiObservable = new BiObservable<>();
        this.buildBiObservable = new BiObservable<>();
        this.passBiObservable = new BiObservable<>();
        this.undoBiObservable = new BiObservable<>();
        this.loseBiObservable = new BiObservable<>();
        this.player = player;
        godInitBiObservable.addObserver(godInitHandler);
        choseStarterBiObservable.addObserver(chooseStarterHandler);
        workerInitBiObservable.addObserver(workerInitHandler);
        moveBiObservable.addObserver(moveHandler);
        buildBiObservable.addObserver(buildHandler);
        passBiObservable.addObserver(passHandler);
        undoBiObservable.addObserver(undoHandler);
        loseBiObservable.addObserver(loseHandler);
        MessageAllocator.initMessageAllocator(this);
    }

    /**
     * Getter for the attribute player
     * @return the value of player
     */
    protected Player getPlayer(){
        return player;
    }

    /**
     * This method will show a message
     * @param o the message that will be showed
     */
    public abstract void showMessage(String o);

    /**
     * This method manages the end of the game
     * @param isLose True if the owner has lost, False otherwise
     */
    public abstract void endGame(boolean isLose);

    /**
     * This method deserializes the message that arrives from the client and, according to the type, sends it to the right handler
     * @param envelope message that will be deserialized
     */
    public void handleMessage(MessageEnvelope envelope){
        MessageAllocator.initMessageAllocator(this);
        try {
            MessageAllocator.allocateMessage(envelope);
        }
        catch(MessageTypeNotFound exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method notifies the godInit handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void godInitAction(MessageEnvelope envelope){
        GodInitMessage message = gson.fromJson(envelope.getMessage(), GodInitMessage.class);
        message.setType(envelope.getType());
        godInitBiObservable.notify(message, this);
    }

    /**
     * This method notifies the chooseStarter handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void chooseStarterAction(MessageEnvelope envelope){
        StarterMessage message = gson.fromJson(envelope.getMessage(), StarterMessage.class);
        message.setType(envelope.getType());
        choseStarterBiObservable.notify(message, this);
    }

    /**
     * This method notifies the workerInit handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void workerInitAction(MessageEnvelope envelope){
        WorkerInitMessage message = gson.fromJson(envelope.getMessage(), WorkerInitMessage.class);
        message.setType(envelope.getType());
        workerInitBiObservable.notify(message, this);
    }

    /**
     * This method notifies the build handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void buildAction(MessageEnvelope envelope){
        BuildMessage message = gson.fromJson(envelope.getMessage(), BuildMessage.class);
        message.setType(envelope.getType());
        buildBiObservable.notify(message, this);
    }

    /**
     * This method notifies the move handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void moveAction(MessageEnvelope envelope){
        MoveMessage message = gson.fromJson(envelope.getMessage(), MoveMessage.class);
        message.setType(envelope.getType());
        moveBiObservable.notify(message, this);
    }

    /**
     * This method notifies the undo handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void undoAction(MessageEnvelope envelope){
        UndoMessage message = gson.fromJson(envelope.getMessage(), UndoMessage.class);
        message.setType(envelope.getType());
        undoBiObservable.notify(message, this);
    }

    /**
     * This method notifies the pass handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void passAction(MessageEnvelope envelope) {
        SimpleMessage message = gson.fromJson(envelope.getMessage(), SimpleMessage.class);
        message.setType(envelope.getType());
        passBiObservable.notify(message, this);
    }

    /**
     * This method notifies the lose handler with the message and the view (this)
     * @param envelope the envelope of the message containing the type of the message it is carrying
     */
    public void loseAction(MessageEnvelope envelope) {
        SimpleMessage message = gson.fromJson(envelope.getMessage(), SimpleMessage.class);
        message.setType(envelope.getType());
        loseBiObservable.notify(message, this);
    }

    /**
     * Getter for the end attribute
     * @return The end flag
     */
    public boolean isEnd() {
        return end;
    }

    /**
     * Setter for the end flag
     * @param end The parameter to be set
     */
    public void setEnd(boolean end) {
        this.end = end;
    }
}

