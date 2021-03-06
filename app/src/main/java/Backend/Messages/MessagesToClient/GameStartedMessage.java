package Backend.Messages.MessagesToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Backend.Client.GameClient;
import Backend.Player.PlayerInformation;
import Backend.Server.ClientConnection;

public class GameStartedMessage {

    public static void submit(ClientConnection connection, List<PlayerInformation> playerInformations){
        connection.getClientOutput().println("GAME_STARTED");
        for (PlayerInformation playerInformation : playerInformations){
            connection.getClientOutput().println(playerInformation.getUsername());
            connection.getClientOutput().println(playerInformation.getUserID());
        }
    }

    public static void submitAll(List<ClientConnection> connections, List<PlayerInformation> playerInformations) {
        for (ClientConnection connection : connections){
            submit(connection, playerInformations);
        }
    }

    public static void receive(GameClient client) throws IOException {
        List<PlayerInformation> result = new ArrayList<>();
        for (int i = 0; i < client.getConnectedPlayer().getPlayersNumber(); i++){
            String username = client.getClientInput().readLine();
            String userID = client.getClientInput().readLine();
            result.add(new PlayerInformation(username, userID));
        }

        client.getConnectedPlayer().setPlayersInformations(result);
    }

}
