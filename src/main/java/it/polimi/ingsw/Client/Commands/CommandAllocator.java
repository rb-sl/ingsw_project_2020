package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.exception.MessageTypeNotFound;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageType;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.misc.QuadriFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to handle and convert commands
 */
public class CommandAllocator {
    /**
     * Map associating the server messageType to the right command
     */
    private static final Map<MessageType, QuadriFunction<ViewInterface, Gson, String, Command>> commandMap = new HashMap<>();
    /**
     * Map associating the commandType given by the player to the right command
     */
    private static final Map<CommandType, QuadriFunction<ViewInterface, Gson, UpdateMessage, Command>> chooseMap = new HashMap<>();
    /**
     * Map associating the server messageType used during initialization to the right command
     */
    private static final Map<MessageType, QuadriFunction<ViewInterface, Gson, String, Command>> initMap = new HashMap<>();

    static {
        initMap.put(MessageType.GODLISTMESSAGE, (view, gson, serverMessage) -> new InitGods(view, gson, serverMessage));
        initMap.put(MessageType.PLAYERSMESSAGE, (view, gson, serverMessage) -> new InitChallenger(view, gson, serverMessage));
        initMap.put(MessageType.INFO, (view, gson, serverMessage) -> new InfoCommand(view, gson, serverMessage));
        initMap.put(MessageType.ENDGAME, (view, gson, serverMessage) -> new EndGameCommand(view, gson, serverMessage));
        initMap.put(MessageType.UPDATE, (view, gson, serverMessage) -> new InitWorkers(view, gson, serverMessage));

        commandMap.put(MessageType.UPDATE, (view, gson, serverMessage) -> new UpdateCommand(view, gson, serverMessage));
        commandMap.put(MessageType.INFO, (view, gson, serverMessage) -> new InfoCommand(view, gson, serverMessage));
        commandMap.put(MessageType.ENDGAME, (view, gson, serverMessage) -> new EndGameCommand(view, gson, serverMessage));

        chooseMap.put(CommandType.MOVE, (view, gson, updateMessage) -> new MoveCommand(view, gson, updateMessage));
        chooseMap.put(CommandType.BUILD, (view, gson, updateMessage) -> new BuildCommand(view, gson, updateMessage));
        chooseMap.put(CommandType.PASS, (view, gson, updateMessage) -> new PassCommand(view, gson, updateMessage));
        chooseMap.put(CommandType.UNDO, (view, gson, updateMessage) -> new UndoCommand(view, gson, updateMessage));
        chooseMap.put(CommandType.HELP, (view, gson, updateMessage) -> new HelpCommand(view, gson, updateMessage));
    }

    /**
     * Allocates the command based on the message received
     * @param view The connected view
     * @param gson The gson object
     * @param envelope The message to be converted to a command
     * @return The new command
     * @throws MessageTypeNotFound If the message type does not exist
     */
    public static Command allocateMessage(ViewInterface view, Gson gson, MessageEnvelope envelope) throws MessageTypeNotFound {
        QuadriFunction<ViewInterface, Gson, String, Command> function = commandMap.get(envelope.getType());
        if (function == null)
            throw new MessageTypeNotFound(envelope.getType().toString());
        return function.applyFunction(view, gson, envelope.getMessage());
    }

    /**
     * Creates a new command based on the commandType
     * @param view The connected view
     * @param gson The gson object
     * @param command The commandtype to be created
     * @param message The UpdateMessage sent by the server
     * @return The new command
     */
    public static Command allocateCommand(ViewInterface view, Gson gson, UpdateMessage message, CommandType command) {
        QuadriFunction<ViewInterface, Gson, UpdateMessage, Command> function = chooseMap.get(command);
        return function.applyFunction(view, gson, message);
    }

    /**
     * Allocates the command based on the initialization message received
     * @param view The connected view
     * @param gson The gson object
     * @param envelope The message to be converted to a command
     * @return The new command
     * @throws MessageTypeNotFound If the message type does not exist
     */
    public static Command allocateInitMessage(ViewInterface view, Gson gson, MessageEnvelope envelope) throws MessageTypeNotFound {
        QuadriFunction<ViewInterface, Gson, String, Command> function = initMap.get(envelope.getType());
        if (function == null)
            throw new MessageTypeNotFound(envelope.getType().toString());
        return function.applyFunction(view, gson, envelope.getMessage());
    }
}
