package pl.mg.checkers.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.mg.checkers.component.Client;
import pl.mg.checkers.message.TypedMessage;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by maciej on 25.12.15.
 */
@Service
@Scope("singleton")
public class MessengerService {

    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private JsonService jsonService;
    @Autowired
    private ClientService clientService;

    private Logger logger = LogManager.getLogger(MessengerService.class);

    public void send(TypedMessage message, Client client){
        threadPoolService.execute(()->jsonService.stringify(message).ifPresent(s->pass(client,s)));
    }

    private void pass(Client client, String stringified){
        try {
            logger.debug("SENDING to "+client.getNickname().toUpperCase()+": "+stringified);
            client.getOutputStream().write((stringified.length()+"\r\n\r\n"+stringified).getBytes());
        } catch (IOException e){
            e.printStackTrace();
            clientService.remove(client);
        }
    }

    public void send(TypedMessage message, Collection<Client> clients){
        threadPoolService.execute(()->jsonService.stringify(message).ifPresent(s->{
            for (Client c : clients) pass(c,s);
        }));
    }

    public void queue(Collection<Client> clients, TypedMessage... messages){

    }

    public void queue(Client client,TypedMessage... messages){
        threadPoolService.execute(()->{
            for (TypedMessage m : messages){
                jsonService.stringify(m).ifPresent(s->{
                    pass(client,s);
                });
            }
        });
    }
}
