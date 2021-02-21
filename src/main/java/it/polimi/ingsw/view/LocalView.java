package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.Player;

/**
 * This class is a local version of the view
 */
public class LocalView extends View{
    /**
     * Constructor for the class
     * @param player The player owning the view
     * @param godInitHandler The handler for godinit
     * @param chooseStarterHandler The handler for choosestarter
     * @param workerInitHandler The handler for workerinit
     * @param moveHandler The handler for the move
     * @param buildHandler The handler for the build
     * @param passHandler The handler for the pass
     * @param undoHandler The handler for undo
     * @param loseHandler The handler for lose
     */
    public LocalView(Player player, GodInitHandler godInitHandler, ChooseStarterHandler chooseStarterHandler, WorkerInitHandler workerInitHandler, MoveHandler moveHandler, BuildHandler buildHandler, PassHandler passHandler, UndoHandler undoHandler, LoseHandler loseHandler) {
        super(player, godInitHandler, chooseStarterHandler, workerInitHandler, moveHandler, buildHandler, passHandler, undoHandler, loseHandler);
    }

    /**
     * Update as per the observer pattern
     * @param t The message type
     * @param m The first message being notified
     */
    @Override
    public void update(MessageType t, SimpleMessage m) {
        // The local view does not use the pattern
    }

    /**
     * Prints a message
     * @param s The message to be printed
     */
    public void showMessage(String s) {
        System.out.println(getPlayer().getNickname()+" "+s);
    }

    /**
     * Overridden end game handler
     */
    @Override
    public void endGame(boolean isLose) {
        System.out.println("Game finished");
    }
}
