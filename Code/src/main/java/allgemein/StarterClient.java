// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package allgemein;

import client.YoolooClient;
import utils.PropertiesController;

import java.util.Properties;

public class StarterClient {
    public static void main(String[] args) {
		Properties props = PropertiesController.getProperties("client");
        // Starte Client
        String hostname = props.get("connection.server.hostname") != null ? props.get("connection.server.hostname").toString() : "localhost";
        int port = props.get("connection.server.port") != null ? Integer.parseInt(props.get("connection.server.port").toString()) : 44137;
        YoolooClient client = new YoolooClient(hostname, port);
        client.startClient();

    }
}
