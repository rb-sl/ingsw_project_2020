package it.polimi.ingsw.model.god;

import it.polimi.ingsw.misc.TripleState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test checking the functionality of triple state elements
 */
class TripleStateTest {
    /**
     * Checks that only a True state yields a true outcome
     */
    @Test
    public void outcomeTest() {
        assertTrue(!TripleState.FALSE.getOutcome());
        assertTrue(!TripleState.INDIFFERENT.getOutcome());
        assertTrue(TripleState.TRUE.getOutcome());
    }

    /**
     * Checks that the truth tables are respected
     */
    @Test
    public void tableTest() {
        assertTrue(TripleState.FALSE.compare(TripleState.FALSE).equals(TripleState.FALSE));
        assertTrue(TripleState.FALSE.compare(TripleState.INDIFFERENT).equals(TripleState.FALSE));
        assertTrue(TripleState.FALSE.compare(TripleState.TRUE).equals(TripleState.FALSE));

        assertTrue(TripleState.INDIFFERENT.compare(TripleState.FALSE).equals(TripleState.FALSE));
        assertTrue(TripleState.INDIFFERENT.compare(TripleState.INDIFFERENT).equals(TripleState.INDIFFERENT));
        assertTrue(TripleState.INDIFFERENT.compare(TripleState.TRUE).equals(TripleState.TRUE));

        assertTrue(TripleState.TRUE.compare(TripleState.FALSE).equals(TripleState.FALSE));
        assertTrue(TripleState.TRUE.compare(TripleState.INDIFFERENT).equals(TripleState.TRUE));
        assertTrue(TripleState.TRUE.compare(TripleState.TRUE).equals(TripleState.TRUE));
    }
}