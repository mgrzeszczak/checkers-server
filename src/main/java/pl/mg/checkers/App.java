package pl.mg.checkers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.mg.checkers.component.Grid;
import pl.mg.checkers.config.AppConfig;
import pl.mg.checkers.service.ClientService;
import pl.mg.checkers.service.ServerService;

import java.io.IOException;

/**
 * Created by maciej on 24.12.15.
 */
public class App {

    private static int PORT = 8080;
    private static Logger logger = LogManager.getLogger("App");

    public static void main(String args[]){
        if (args.length>=1) {
            try {
                PORT = Integer.valueOf(args[0]);
            } catch (NumberFormatException e){
                e.printStackTrace();
                return;
            }
        }
        logger.debug("Starting the server at port: "+PORT);
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ServerService serverService = context.getBean(ServerService.class);
        try {
            serverService.start(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
