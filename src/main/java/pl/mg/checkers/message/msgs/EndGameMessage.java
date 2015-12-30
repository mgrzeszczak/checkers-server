package pl.mg.checkers.message.msgs;

import pl.mg.checkers.representation.LobbyRepresentation;

import java.util.Map;

/**
 * Created by maciej on 29.12.15.
 */
public class EndGameMessage {

    private boolean won;
    private boolean left;

    public EndGameMessage(boolean won, boolean left) {
        this.won = won;
        this.left = left;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
}
