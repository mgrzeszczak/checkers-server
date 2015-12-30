package pl.mg.checkers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.mg.checkers.component.Client;
import pl.mg.checkers.component.Lobby;
import pl.mg.checkers.message.MsgType;
import pl.mg.checkers.message.TypedMessage;
import pl.mg.checkers.message.msgs.LobbyUpdateMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maciej on 25.12.15.
 */
@Service
@Scope("singleton")
public class LobbyService {

    @Autowired
    private GameService gameService;
    @Autowired
    private MessengerService messengerService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ApplicationContext context;

    private static long count = 0;
    private Map<Long,Lobby> lobbyMap = new ConcurrentHashMap<Long,Lobby>();

    public Lobby createLobby(Client client){
        Lobby lobby = context.getBean(Lobby.class);
        lobby.addClient(client);
        newLobby(lobby);
        //messengerService.send(new TypedMessage(MsgType.lobbyUpdate,new LobbyUpdateMessage(lobbyMap)),
        //        clientService.get());
        return lobby;
    }

    public void newLobby(Lobby lobby){
        lobby.setId(++count);
        lobbyMap.put(lobby.getId(),lobby);
    }

    public void startGame(Lobby lobby){
        lobbyMap.remove(lobby.getId());
        gameService.newGame(lobby.getClients());
    }

    public Optional<Lobby> clientJoinLobby(Client client, long lobbyId){
        if (!lobbyMap.containsKey(lobbyId)) return Optional.empty();
        Lobby lobby = lobbyMap.get(lobbyId);
        boolean success = lobby.addClient(client);
        return success? Optional.of(lobby) : Optional.empty();
    }

    public void clientLeaveLobby(Client client, long lobbyId){
        if (!lobbyMap.containsKey(lobbyId)) return;
        Lobby lobby = lobbyMap.get(lobbyId);
        lobby.removeClient(client);
    }

    public Map<Long,Lobby> getLobbies(){
        return lobbyMap;
    }

    public void remove(long id){
        lobbyMap.remove(id);
    }

    public void onClientError(Client client){
        for (Lobby lobby : lobbyMap.values()){
            if (lobby.getClients().contains(client)){
                lobby.removeClient(client);
                break;
            };
        }
    }
}
