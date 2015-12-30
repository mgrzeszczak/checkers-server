package pl.mg.checkers.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import pl.mg.checkers.*;
import pl.mg.checkers.component.Client;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by maciej on 24.12.15.
 */
@Service
@Scope(value = "singleton")
public class ServerService {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ThreadPoolService threadPoolService;

    private ServerSocket serverSocket;

    private Logger logger = LogManager.getLogger(ServerService.class);

    public void start(int port) throws IOException{
        logger.debug("ServerService started");
        serverSocket = new ServerSocket(port);
        Socket clientSocket = null;
        while (true){
            clientSocket = serverSocket.accept();
            logger.debug("new client joined " +clientSocket.toString());
            Client client = context.getBean(Client.class);
            client.init(clientSocket);
            clientService.add(client);
        }
    }

}
