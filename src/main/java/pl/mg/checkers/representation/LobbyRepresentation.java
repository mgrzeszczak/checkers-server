package pl.mg.checkers.representation;

import pl.mg.checkers.component.Client;
import pl.mg.checkers.component.Lobby;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by maciej on 25.12.15.
 */
public class LobbyRepresentation {

    private long id;
    private Set<ClientRepresentation> clients;

    public LobbyRepresentation(long id, Set<Client> clients) {
        this.id = id;
        this.clients = new HashSet<>();
        for (Client c : clients){
            this.clients.add(new ClientRepresentation(c.getNickname()));
        }
    }

    public LobbyRepresentation(Lobby lobby){
        this.id = lobby.getId();
        this.clients = new HashSet<>();
        for (Client c : lobby.getClients()){
            this.clients.add(new ClientRepresentation(c.getNickname()));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<ClientRepresentation> getClients() {
        return clients;
    }

    public void setClients(Set<ClientRepresentation> clients) {
        this.clients = clients;
    }
}
