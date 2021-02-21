package it.polimi.ingsw.model.god;

import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.log.LogTurn;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing for gods' observer-related classes
 */
class GodEventManagerTest {
    /**
     * This test covers all methods used by a god's event manager
     */
    @Test
    void MultipleSubscribeTest() {
        // Setup
        Board board = new Board(5,5, 2);

        Player testPlayer = new Player("testPlayer", board);
        board.addPlayers(testPlayer);

        God god = new God("name", testPlayer);
        GodEventManager godEventManager = new GodEventManager(god);

        String testEvent = "testEvent";
        List<Observer<GodTrigger>> testGodList = new ArrayList<>();
        God testGod0 = new God("testGod0", testPlayer);
        God testGod1 = new God("testGod1", testPlayer);
        God testGod2 = new God("testGod2", testPlayer);

        testGodList.add(testGod0);
        testGodList.add(testGod1);
        testGodList.add(testGod2);

        // Adding all test gods to the event, then notifying them
        godEventManager.subscribeAll(testEvent, testGodList);
        god.getLogTurn().addBuild(LogTurn.order.MAIN, new Worker(testPlayer, 'm'), new Coordinates(0,0, board));
        godEventManager.notifyTrigger(testEvent,1);

        assertTrue(testGod0.isTriggered(testEvent) && testGod1.isTriggered(testEvent) && testGod2.isTriggered(testEvent));

        // Unsubscribing one god to test the correctness
        godEventManager.unsubscribe(testEvent, testGod2);
        testGod2.resetTriggers();

        assertTrue(testGod0.isTriggered(testEvent));
        assertTrue(testGod1.isTriggered(testEvent));
        assertFalse(testGod2.isTriggered(testEvent));

        godEventManager.notifyTrigger(testEvent, 1);

        assertTrue(testGod0.isTriggered(testEvent));
        assertTrue(testGod1.isTriggered(testEvent));
        assertFalse(testGod2.isTriggered(testEvent));

        // Checking the correct unsub of all gods
        godEventManager.unsubscribeAll(testEvent);
        testGod0.resetTriggers();
        testGod1.resetTriggers();

        assertFalse(testGod0.isTriggered(testEvent));
        assertFalse(testGod1.isTriggered(testEvent));
        assertFalse(testGod2.isTriggered(testEvent));

        godEventManager.notifyTrigger(testEvent,1);

        assertFalse(testGod0.isTriggered(testEvent));
        assertFalse(testGod1.isTriggered(testEvent));
        assertFalse(testGod2.isTriggered(testEvent));
    }
}