package it.polimi.ingsw.misc;

/**
 * Enum class representing the return type of win conditions
 */
public enum TripleState {
    /**
     * The three possible states of the computation
     */
    TRUE, FALSE, INDIFFERENT;

    /**
     * Compares two triple states according to the following conditions:
     * A    B   |  R
     * ---------|---
     * F    F   |  F
     * F    I   |  F
     * F    T   |  F
     * I    F   |  F
     * I    I   |  I
     * I    T   |  T
     * T    F   |  F
     * T    I   |  T
     * T    T   |  T
     * @param x The state to be compared to
     * @return A triple state according to the defined rules
     */
    public TripleState compare(TripleState x) {
       if(this.equals(INDIFFERENT)) {
           return x;
       }
       else if(this.equals(FALSE) || x.equals(FALSE)) {
           return FALSE;
       }
       else {
           return TRUE;
       }
    }

    /**
     * Transforms the triple state into a boolean
     * @return True iff the final computation is neither false nor indifferent
     */
    public boolean getOutcome() {
         return this == TRUE;
    }
}
