package pl.mg.checkers.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.mg.checkers.component.Client;
import pl.mg.checkers.component.Lobby;
import pl.mg.checkers.message.MsgType;
import pl.mg.checkers.message.TypedMessage;
import pl.mg.checkers.message.msgs.InitMessage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maciej on 24.12.15.
 */
@Service
public class ClientService {

    private Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>());
    private Logger logger = LogManager.getLogger(ClientService.class);

    @Autowired
    private MessengerService messengerService;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private GameService gameService;

    public Set<Client> get(){
        return clients;
    }
    public void add(Client client){
        clients.add(client);
        //Lobby lobby = context.getBean(Lobby.class);
        //lobby.addClient(client);
        //lobbyService.newLobby(lobby);
        //messengerService.send(new TypedMessage(MsgType.init,new InitMessage(lobbyService.getLobbies())),client);
    }

    public void sendInitMessage(Client client){
        messengerService.send(new TypedMessage(MsgType.init,new InitMessage(lobbyService.getLobbies())),client);
    }

    public void remove(Client client){
        logger.debug("Client {} disconnected",client.getNickname());
        lobbyService.onClientError(client);
        gameService.onClientError(client);
        clients.remove(client);
        Socket s =  client.getSocket();
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
