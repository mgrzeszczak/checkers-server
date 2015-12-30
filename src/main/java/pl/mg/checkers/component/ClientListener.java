package pl.mg.checkers.component;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.mg.checkers.message.MsgType;
import pl.mg.checkers.message.TypedMessage;
import pl.mg.checkers.message.msgs.*;
import pl.mg.checkers.representation.LobbyRepresentation;
import pl.mg.checkers.service.*;
import pl.mg.checkers.structure.ClientMessageListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * Created by maciej on 25.12.15.
 */
@Component
@Scope("prototype")
public class ClientListener implements Runnable{

    private static int BUFFER_SIZE = 1024;
    private Logger logger;

    @Autowired
    private JsonService jsonService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private MessengerService messengerService;
    @Autowired
    private GameService gameService;

    private Client client;
    private InputStream is;
    private OutputStream os;

    private ClientMessageListener listener;

    public void setUp(Client client) throws IOException{
        this.client = client;
        this.is = client.getSocket().getInputStream();
        this.os = client.getSocket().getOutputStream();
        this.logger = LogManager.getLogger(ClientListener.class);
    }

    public ClientMessageListener getListener() {
        return listener;
    }

    public void setListener(ClientMessageListener listener) {
        this.listener = listener;
    }

    public void run() {
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        client.disconnect();
    }

    private void listen() throws IOException,NumberFormatException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        int b;
        while ((b = is.read())!=-1){
            sb.append(Character.toChars(b));
            if (!sb.toString().contains("\r\n\r\n")) continue;
            int length = Integer.valueOf(sb.toString().substring(0,sb.toString().indexOf("\r\n\r\n")));
            while (length>0) {
                int read = is.read(buffer,0,BUFFER_SIZE);
                length-=read;
                baos.write(buffer,0,read);
            }
            String message = baos.toString("utf-8");
            jsonService.parseNode(message).ifPresent(this::handleMessage);
            sb.setLength(0);
            baos.reset();
        }
    }

    private void handleMessage(JsonNode node){
        String type = node.get("type").asText();
        String content = node.get("content").toString();

        logger.debug("RECEIVED from "+client.getNickname().toUpperCase()+": "+node.toString());

        if (!MsgType.isValidType(type)) return;

        switch (MsgType.valueOf(type)){
            case init:
                logger.debug(content);
                break;
            case changeName:
                jsonService.parseObject(content, ChangeNicknameMessage.class).ifPresent(m->client.setNickname(m.getNewNickname()));
                clientService.sendInitMessage(client);
                break;
            case createLobby:
                logger.debug(content);
                Lobby lobby = lobbyService.createLobby(client);
                messengerService.send(new TypedMessage(MsgType.createLobby, new CreateLobbyMessage(new
                        LobbyRepresentation(lobby))),client);
                break;
            case leaveLobby:
                jsonService.parseObject(content, LeaveLobbyMessage.class).ifPresent(m->{
                    lobbyService.clientLeaveLobby(client,m.getLobbyId());
                    messengerService.send(new TypedMessage(MsgType.leaveLobby,null),client);
                    //messengerService.send(new TypedMessage(MsgType.lobbyUpdate,new LobbyUpdateMessage(lobbyService
                    //        .getLobbies())),clientService.get());
                });
                break;
            case joinLobby:
                jsonService.parseObject(content, JoinLobbyMessage.class).ifPresent(m->{
                    Optional<Lobby> ret = lobbyService.clientJoinLobby(client,m.getLobbyId());
                    if (!ret.isPresent()) messengerService.send(new TypedMessage(MsgType.joinLobby,new
                            JoinLobbyMessage(false)),client);
                });
                break;
            case lobbyUpdate:
                messengerService.send(new TypedMessage(MsgType.lobbyUpdate,new LobbyUpdateMessage(lobbyService
                        .getLobbies())),client);
                break;
            case leaveGame:
                gameService.onClientError(client);
                messengerService.send(new TypedMessage(MsgType.leaveGame,null),client);
                break;
            case gamePawnMove:
                jsonService.parseObject(content,GamePawnMoveMessage.class).ifPresent(m->{
                    if (listener==null) {
                        logger.debug("LISTENER IS NULL AND RECEIVED GAME PAWN MOVE MESSAGE");
                        return;
                    }
                    listener.onGamePawnMoveMessage(m,client);
                });
                break;
            case gridUpdateAck:
                if (listener==null){
                    logger.debug("LISTENER IS NULL AND RECEIVED GRID UPDATE ACK MESSAGE");
                    return;
                }
                listener.sendTurnMessage(client);
                break;
            default:
                break;
        }
    }

}
