package it.polimi.ingsw.Client.Commands;

/**
 * Enum listing the command types
 */
public enum CommandType {
    MOVE("Move"),
    BUILD("Build"),
    UNDO("Undo"),
    PASS("Pass"),
    HELP("Help");

    /**
     * The saved command
     */
    String command;

    /**
     * Creator of the enum
     * @param command The command to set
     */
    CommandType(String command) {
        this.command = command;
    }

    /**
     * Getter for the command
     * @return The command value
     */
    public String getCommand() {
        return command;
    }
}
