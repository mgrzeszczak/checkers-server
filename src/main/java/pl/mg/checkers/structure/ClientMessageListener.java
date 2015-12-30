package pl.mg.checkers.structure;

import pl.mg.checkers.component.Client;
import pl.mg.checkers.message.Message;
import pl.mg.checkers.message.MsgType;
import pl.mg.checkers.message.msgs.GamePawnMoveMessage;

/**
 * Created by maciej on 25.12.15.
 */
public interface ClientMessageListener {

    void onGamePawnMoveMessage(GamePawnMoveMessage message, Client c);
    void sendTurnMessage(Client client);

}
