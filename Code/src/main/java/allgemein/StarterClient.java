// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package allgemein;

import client.YoolooClient;
import utils.PropertiesController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StarterClient {

    public static void main(String[] args) {
		Properties props = PropertiesController.getProperties("client");
        // Starte Client
        String hostname = props.get("connection.server.hostname") != null ? props.get("connection.server.hostname").toString() : "localhost";
        int port = props.get("connection.server.port") != null ? Integer.parseInt(props.get("connection.server.port").toString()) : 44137;
        boolean nameCheck = props.get("game.nameCheck") != null ? props.get("game.nameCheck").toString().equals("true") : false;
        YoolooClient client = new YoolooClient(hostname, port, nameCheck);
        client.startClient();

    }
}
