package pl.mg.checkers.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.mg.checkers.service.ClientService;
import pl.mg.checkers.service.ThreadPoolService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by maciej on 25.12.15.
 */
@Component
@Scope("prototype")
public class Client {

    private static int count=0;

    @Autowired
    private ClientListener clientListener;
    private Socket socket;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ThreadPoolService threadPoolService;
    private String nickname = "user"+(++count);
    private OutputStream outputStream;

    private Logger logger = LogManager.getLogger(Client.class);

    public void init(Socket socket){
        logger.debug("Client {} init {}",nickname,socket);
        this.socket = socket;
        try {
            this.outputStream = socket.getOutputStream();
            clientListener.setUp(this);
            threadPoolService.execute(clientListener);
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect(){
        clientService.remove(this);
    }

    public Socket getSocket(){
        return socket;
    }

    public String getInetAddress(){
        return socket.getInetAddress().toString();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        logger.debug("New nickname: {} @{}",nickname,getInetAddress());
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String toString() {
        return "Client{" +
                "nickname='" + nickname + '\'' +
                '}';
    }

    public ClientListener getClientListener() {
        return clientListener;
    }
}
