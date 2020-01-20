package server.bot;

import client.YoolooClient;
import server.YoolooServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class YoolooBotServer extends YoolooServer {
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Logger logger = Logger.getLogger("YoolooBotServer");
    private static final long LOBBY_WAIT = 10; // In milliseconds

    public YoolooBotServer(int port, final int spielerProRunde, GameMode gameMode) {
        super(port, spielerProRunde, gameMode);
        threadPool.execute(new Runnable() {
            private YoolooServer yoolooServer;

            public Runnable init(YoolooServer yoolooServer) {
                this.yoolooServer = yoolooServer;
                return (this);
            }

            @Override
            public void run() {
                try {
                    logger.info("WAITING FOR CONNECTIONS...");
                    TimeUnit.SECONDS.sleep(LOBBY_WAIT);
                    spawnBots(spielerProRunde - yoolooServer.getClientCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.init(this));


    }

    private void spawnBots(int count) {
        for (int i = 0; i < count; i++) {
            logger.info("SPAWNING BOT" + i);
            threadPool.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            new YoolooClient().startClient();
                        }
                    });
        }
    }


}
