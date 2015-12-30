package pl.mg.checkers.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.mg.checkers.service.LobbyService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by maciej on 25.12.15.
 */
@Component
@Scope("prototype")
public class Lobby {

    @Autowired
    private LobbyService lobbyService;
    private long id;
    private Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>());

    private Logger logger = LogManager.getLogger(Lobby.class);

    public synchronized Set<Client> getClients() {
        return clients;
    }

    public synchronized boolean addClient(Client client){
        if (clients.size()==2) return false; // sync issues
        if (clients.size()<2) clients.add(client);
        if (clients.size()==2) {
            logger.debug("Alredy 2 clients!!!");
            lobbyService.startGame(this);
        }
        return true;
    }

    public synchronized void removeClient(Client client){
        if (clients.size()==0) return; // sync issues?
        clients.remove(client);
        if (clients.size()==0) lobbyService.remove(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
