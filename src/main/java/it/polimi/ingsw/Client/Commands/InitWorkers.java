package it.polimi.ingsw.Client.Commands;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.ViewInterface;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.map.SimpleCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used to place the workers
 */
public class InitWorkers extends Command {
    /**
     * The connected view
     */
    private final ViewInterface view;
    /**
     * The gson object
     */
    private final Gson gson;
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
    public InitWorkers(ViewInterface view, Gson gson, String serverMessage) {
        this.view = view;
        this.gson = gson;
        this.serverMessage = serverMessage;
    }

    @Override
    public void execute() {
        UpdateMessage updateMessage;
        Character workerSex;

        updateMessage = gson.fromJson(serverMessage, UpdateMessage.class);
        view.updateBoard(updateMessage.getBoardUpdate());
        if (!updateMessage.getBoardUpdate().isEmpty()) {
            List<SimpleCoordinates> workerPos = new ArrayList<>(updateMessage.getBoardUpdate().keySet());
            if (workerPos.size() == 1) {
                if (updateMessage.getBoardUpdate().get(workerPos.get(0)).getWorkerOwner().equals(view.getOwnerNick())) {
                    view.getWorkers().put(updateMessage.getBoardUpdate().get(workerPos.get(0)).getWorkerSex(), workerPos.get(0));
                }
            }
        }
        view.showBoard();
        if (updateMessage.canPlaceWorker()) {
            //Choose where to place the workers
            workerSex = view.getWorkerSex().get(view.getWorkers().size());
            view.placeWorker(workerSex);
        }
        else
        {
            //First message after workers placing is at first addressed here to know when the game can start
            if (view.getWorkers().size() != 0) {
                view.setInit(false);
            }
            view.waitForTurn(updateMessage.getPlayer());
        }
    }
}
