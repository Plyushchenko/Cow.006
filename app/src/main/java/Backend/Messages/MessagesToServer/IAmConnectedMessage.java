package Backend.Messages.MessagesToServer;

import java.io.IOException;

import Backend.Client.GameClient;
import Backend.Server.ClientConnection;

public class IAmConnectedMessage {

    public static void submit(GameClient client){
        client.getClientOutput().println("I_AM_CONNECTED");
    }

    public static void receive(ClientConnection connection) throws IOException {
        connection.getClientInput().readLine();
    }
}
