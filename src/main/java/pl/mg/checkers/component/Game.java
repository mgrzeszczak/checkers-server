package pl.mg.checkers.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.mg.checkers.message.Message;
import pl.mg.checkers.message.MsgType;
import pl.mg.checkers.message.TypedMessage;
import pl.mg.checkers.message.msgs.EndGameMessage;
import pl.mg.checkers.message.msgs.GamePawnMoveMessage;
import pl.mg.checkers.message.msgs.GameTurnMessage;
import pl.mg.checkers.message.msgs.StartGameMessage;
import pl.mg.checkers.representation.GameRepresentation;
import pl.mg.checkers.service.ClientService;
import pl.mg.checkers.service.GameLogicService;
import pl.mg.checkers.service.GameService;
import pl.mg.checkers.service.MessengerService;
import pl.mg.checkers.structure.ClientMessageListener;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by maciej on 25.12.15.
 */
@Component
@Scope("prototype")
public class Game implements ClientMessageListener {

    private static Random rand = new Random();
    private Logger logger = LogManager.getLogger(Game.class);

    private int whoseTurn;

    private Client[] clients = new Client[2];

    @Autowired
    private Grid grid;
    @Autowired
    private GameService gameService;
    @Autowired
    private MessengerService messengerService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private GameLogicService gameLogicService;



    public synchronized void setUp(Set<Client> clients){
        int which =  rand.nextInt(2);
        Client[] cArr = new Client[2];
        int iterator = 0;
        for (Client c : clients){
            cArr[iterator] = c;
            if (++iterator==2) break;
        }
        this.clients[0] = cArr[which]; // WHITE PLAYER
        this.clients[1] = cArr[(which+1)%2]; // BLACK PLAYER
        cArr[0].getClientListener().setListener(this);
        cArr[1].getClientListener().setListener(this);
        // notify
        whoseTurn = 0;
        messengerService.send(new TypedMessage(MsgType.gameStarted,new StartGameMessage(1,new GameRepresentation
                (grid,cArr[1]))),this.clients[0]);
        messengerService.send(new TypedMessage(MsgType.gameStarted,new StartGameMessage(2,new GameRepresentation
                (grid,cArr[0]))),this.clients[1]);
    }

    public synchronized Client[] clients(){ return clients;}

    @Override
    public synchronized void onGamePawnMoveMessage(GamePawnMoveMessage message, Client c) {
        if (!clients[whoseTurn].equals(c)) {
            logger.debug("NOT YOUR TURN");
            return;
        }
        int pawnIndex = message.getPawnIndex();
        int destinationIndex = message.getDestinationIndex();
        Map<Integer, List<Integer>> moveMap = gameLogicService.calculateMoves(grid.getGrid(), whoseTurn + 1);
        moveMap.forEach((i,l)->{
            logger.debug("MOVES for "+i);
            l.forEach(j->logger.debug(j));
        });

        if (moveMap.containsKey(pawnIndex) && moveMap.get(pawnIndex).contains(destinationIndex)){
            gameLogicService.updateGrid(grid.getGrid(),pawnIndex,destinationIndex);

            boolean gameEnd = gameLogicService.checkWinningConditions(grid.getGrid(),whoseTurn+1);
            if (gameEnd){
                clients[0].getClientListener().setListener(null);
                clients[1].getClientListener().setListener(null);

                messengerService.send(new TypedMessage(MsgType.gameEnded,new EndGameMessage(true,false)),c);
                messengerService.send(new TypedMessage(MsgType.gameEnded,new EndGameMessage(false,false)),
                        clients[(whoseTurn+1)%2]);
                gameService.remove(this);
                return;
            }
            switchTurn();
            messengerService.send(new TypedMessage(MsgType.gamePawnMove,new GamePawnMoveMessage(grid
                    .getGrid())),clients[0]);
            messengerService.send(new TypedMessage(MsgType.gamePawnMove,new GamePawnMoveMessage(grid
                    .getGrid())),clients[1]);

        } else {
            logger.debug("MOVE INVALID ???");
            clients[0].getClientListener().setListener(null);
            clients[1].getClientListener().setListener(null);
            messengerService.send(new TypedMessage(MsgType.gameEnded,new EndGameMessage(false,false)),c);
            messengerService.send(new TypedMessage(MsgType.gameEnded,new EndGameMessage(true,false)),
                    clients[(whoseTurn+1)%2]);
            gameService.remove(this);
            return;
        }
    }

    @Override
    public void sendTurnMessage(Client client) {
        boolean playerTurn = false;
        if (clients[whoseTurn].equals(client)) playerTurn=true;
        messengerService.send(new TypedMessage(MsgType.gameTurn,new GameTurnMessage(playerTurn)),client);
    }

    private void switchTurn(){
        whoseTurn = (whoseTurn+1)%2;
    }
}
