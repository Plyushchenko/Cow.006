package Backend.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import Backend.Messages.MessagesToClient.*;
import Backend.Messages.MessagesToServer.*;
import Backend.Messages.MessagesToClient.BuildFinalResultsMessages.*;
import Backend.Messages.MessagesToServer.ResultsBuiltMessage.*;
import Backend.Player.*;

import static java.util.Collections.max;

public class GameClient extends Client{

    private AbstractPlayer connectedPlayer;

    public GameClient(AbstractPlayer connectedPlayer){
        this.connectedPlayer = connectedPlayer;
        isClosed = true;
    }

    public void requestGame(String host, int portNumber) throws IOException {
        connectToServer(host, portNumber);
        if (connectedPlayer instanceof Player) {
            PlayersNumberMessage.submit(this, connectedPlayer.getPlayersNumber());
        }
        PlayerInformationMessage.submit(this, connectedPlayer.getPlayerInformation());
        isClosed = false;
        receiveAndSubmitMessagesAboutGameSession();
    }

    private void receiveAndSubmitMessagesAboutGameSession() throws IOException {
        String messageType;
        while (!isClosed) {
            messageType = clientInput.readLine();
            switch (messageType) {
                case "BUILD_MULTI_PLAY_FINAL_RESULTS":
                    BuildMultiPlayFinalResultsMessage.receive(this);
                    MultiPlayFinalResultsBuiltMessage.submit(this);
                    break;
                case "BUILD_SINGLE_PLAY_FINAL_RESULTS":
                    BuildSinglePlayFinalResultsMessage.receive(this);
                    SinglePlayFinalResultsBuiltMessage.submit(this);
                    break;
                case "CURRENT_ROUND":
                    CurrentRoundMessage.receive(this);
                    break;
                case "DEAL_STARTED":
                    DealStartedMessage.receive(this);
                    break;
                case "GAME_FINISHED":
                    isClosed = true;
                    GameFinishedMessage.receive(this);
                    break;
                case "GAME_STARTED":
                    GameStartedMessage.receive(this);
                    break;
                case "IS_CONNECTED":
                    IAmConnectedMessage.submit(this);
                    break;
                case "SEND_CARD":
                    CardSelectedMessage.submit(this, connectedPlayer.chooseCard());
                    break;
                case "SEND_MAX_SCORE":
                    MaxScoreSentMessage.submit(this, max(connectedPlayer.getScores()));
                    break;
                case "SEND_ROW":
                    RowSelectedMessage.submit(this, connectedPlayer.chooseRow());
                    break;
                case "SEND_SCORES":
                    ScoresSentMessage.submit(this, connectedPlayer.getScores());
                    break;
                case "SMALLEST_CARD_TURN":
                    SmallestCardTurnMessage.receive(this);
                    break;
            }
        }
    }

    public void disconnectFromServer() throws IOException {
        isClosed = true;
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    public AbstractPlayer getConnectedPlayer() {
        return connectedPlayer;
    }

    public BufferedReader getClientInput() {
        return clientInput;
    }

    public PrintWriter getClientOutput() {
        return clientOutput;
    }


}