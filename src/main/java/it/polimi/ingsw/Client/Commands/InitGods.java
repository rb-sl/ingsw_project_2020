package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.*;

/**
 * Command used to choose the gods
 */
public class InitGods extends Command {
    /**
     * The gson object
     */
    private final Gson gson;
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * The message from the server
     */
    private final String serverMessage;

    /**
     * Constructor for the class
     * @param view The connected view
     * @param gson The gson object
     * @param serverMessage The message from the server
     */
    public InitGods(ViewInterface view, Gson gson, String serverMessage) {
        this.gson = gson;
        this.view = view;
        this.serverMessage = serverMessage;
    }

    @Override
    public void execute() {
        GodListMessage godList;

        //Choose gods
        godList = gson.fromJson(serverMessage, GodListMessage.class);
        if (view.getGods() == null)
            view.setGods(godList.getGodList());
        view.showGod(godList, gson);
    }
}
