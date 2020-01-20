package server;

import common.YoolooSpieler;

import java.net.Socket;

public class FancyYoolooClientHandler extends YoolooClientHandler {

    public FancyYoolooClientHandler(YoolooServer yoolooServer, Socket clientSocket) {
        super(yoolooServer, clientSocket);
    }

    @Override
    private void registriereSpielerInSession(YoolooSpieler meinSpieler) {
        System.out
                .println("[ClientHandler" + clientHandlerId + "] registriereSpielerInSession " + meinSpieler.getName());
        session.getAktuellesSpiel().spielerRegistrieren(meinSpieler);
    }
}
