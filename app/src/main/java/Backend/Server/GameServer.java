package Backend.Server;

import Backend.GameHandler.GameHandler;
import Backend.Messages.MessagesToClient.GameFinishedMessage;

abstract class GameServer {

    static void startGame(GameHandler gameHandler) {
        new Thread(() -> {
            try {
                gameHandler.playGame();
            } catch (Exception e) {
                GameFinishedMessage.submitAll(
                        gameHandler.getConnections(), GameHandler.GameFinishedReasons.SOMEONE_HAS_DISCONNECTED);
            }
        }).start();
    }

}
