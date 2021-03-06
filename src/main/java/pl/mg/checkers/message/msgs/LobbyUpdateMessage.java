package pl.mg.checkers.message.msgs;

import pl.mg.checkers.component.Lobby;
import pl.mg.checkers.representation.LobbyRepresentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maciej on 29.12.15.
 */
public class LobbyUpdateMessage {

    private Map<Long,LobbyRepresentation> lobbies;

    public LobbyUpdateMessage(Map<Long, Lobby> lobbies) {
        this.lobbies = new HashMap<>();
        for (Map.Entry<Long,Lobby> e : lobbies.entrySet()){
            this.lobbies.put(e.getKey(),new LobbyRepresentation(e.getValue().getId(),e.getValue().getClients()));
        }
    }

    public LobbyUpdateMessage() {

    }

    public Map<Long, LobbyRepresentation> getLobbies() {
        return lobbies;
    }

    public void setLobbies(Map<Long, LobbyRepresentation> lobbies) {
        this.lobbies = lobbies;
    }

}
