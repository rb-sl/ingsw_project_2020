package it.polimi.ingsw.Client.Commands;

import java.io.PrintWriter;

/**
 * Class used to define the commands a player can give to the application (through the command pattern)
 */
public abstract class Command {
    /**
     * The output towards the player
     */
    private static PrintWriter clientOutput;

    /**
     * Executes the command
     */
    public abstract void execute();

    /**
     * Sets the output towards the client
     * @param clientOutput The printWriter to set
     */
    public static void setClientOutput(PrintWriter clientOutput) {
        Command.clientOutput = clientOutput;
    }

    /**
     * Sends a message to the client
     * @param message The message to be sent
     */
    public static void sendMessage(String message) {
        //Assuming PrintWriter is not thread-safe by itself
        synchronized (clientOutput) {
            if (!checkError()) {
                clientOutput.println(message);
                clientOutput.flush();
            }
        }
    }

    /**
     * Calls the checkError on the printWriter
     * @return The standard return
     */
    public static boolean checkError() {
        return clientOutput.checkError();
    }
}
