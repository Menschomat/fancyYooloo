package persistance;

import client.YoolooClient;
import org.junit.jupiter.api.Test;
import server.YoolooServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleUserNameCheckTest {

    @Test
    void userAlreadyInSessionTest() throws InterruptedException, ExecutionException {
        YoolooServer server = new YoolooServer(44137, 2, 2, 100000, YoolooServer.GameMode.GAMEMODE_SINGLE_GAME, true);
        YoolooClient client1 = new YoolooClient("localhost",44137, true);
        client1.setName("test");
        YoolooClient client2 = new YoolooClient("localhost",44137, true);
        client2.setName("test");
        final ExecutorService service = Executors.newFixedThreadPool(3);

        service.execute(() -> server.startServer());
        final Future<Integer> result = service.submit(() -> maxClientWatcher(3000, server));
        service.execute(() -> client1.startClient());
        Thread.sleep(100);
        service.execute(() -> client2.startClient());
        while (!result.isDone()) {
            Thread.sleep(300);
        }
        server.shutDownServer(543210);
        service.shutdownNow();
        assertEquals(1, result.get());
    }

    private int maxClientWatcher(long mils, YoolooServer server) {
        System.out.println(server);
        long start_mils = System.currentTimeMillis();
        while (System.currentTimeMillis() - start_mils < mils) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return server.getClientCount();
    }

}
