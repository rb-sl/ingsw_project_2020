package it.polimi.ingsw.model.map;

import it.polimi.ingsw.model.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.exception.NotFreeException;

/**
 * Tests for dome class
 */
public class DomeTest {
    /**
     * Test for setWorkerOnTopTest
     */
    @Test
    public void setWorkerOnTopTest(){
        Dome d = new Dome();
        try {
            d.setWorkerOnTop(new Worker(null, 'F'));
        }catch (NotFreeException ex){
            assertTrue(true);
        }
    }
}
