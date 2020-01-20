package bot;

import client.YoolooClient;
import server.YoolooServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class YoolooBotServer extends YoolooServer {
    private ExecutorService botThreadPool = Executors.newCachedThreadPool();
    private Logger logger = Logger.getLogger("YoolooBotServer");
    private static final  long LOBBY_WAIT = 10000; // In milliseconds

    public YoolooBotServer(int port, int spielerProRunde, GameMode gameMode) {
        super(port, spielerProRunde, gameMode);
        botThreadPool.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        new YoolooClient().startClient();
                    }
                });
        botThreadPool.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        new YoolooClient().startClient();
                    }
                });
    }


}
