package pl.mg.checkers.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.mg.checkers.component.Client;
import pl.mg.checkers.component.Game;
import pl.mg.checkers.message.MsgType;
import pl.mg.checkers.message.TypedMessage;
import pl.mg.checkers.message.msgs.EndGameMessage;
import pl.mg.checkers.message.msgs.StartGameMessage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/**
 * Created by maciej on 25.12.15.
 */
@Service
@Scope("singleton")
public class GameService {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private MessengerService messengerService;
    @Autowired
    private ClientService clientService;

    private Logger logger = LogManager.getLogger(GameService.class);

    private Set<Game> games = Collections.synchronizedSet(new HashSet<>());

    public void onClientError(Client client){
        games.stream().filter(game->game.clients()[0].equals(client)||game.clients()[1].equals(client)).findFirst()
                .ifPresent(game->{
                    logger.debug(client.getNickname()+ " left GAME\n\n");
                    Client other = game.clients()[0].equals(client)? game.clients()[1] : game.clients()[0];
                    messengerService.send(new TypedMessage(MsgType.gameEnded,new EndGameMessage(true,true)),other);
                    games.remove(game);
                });
    }

    public Game newGame(Set<Client> clients){
        Game newGame = context.getBean(Game.class);
        newGame.setUp(clients);
        games.add(newGame);
        return null;
    }

    public void remove(Game game){
        if (games.contains(game))
            games.remove(game);
    }

}
