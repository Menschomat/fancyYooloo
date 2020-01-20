package persistance;

import common.YoolooKarte;
import common.YoolooSpieler;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoolooUsers implements Serializable {

    Logger logger = Logger.getLogger(getClass().getName());

    private YoolooFileWriter fileWriter = new YoolooFileWriter();

    private YoolooPersistance persistance = new YoolooPersistance();

    public YoolooUsers() {
        persistance = fileWriter.loadUsers();
    }

    public synchronized void setUserCardOrder(String username, List<Integer> cardValuesInOrder) {
        persistance.getUsers().put(username, new ArrayList<>(cardValuesInOrder));
        fileWriter.saveUsers(persistance);
    }

    public synchronized YoolooKarte[] getUserCardOrder(YoolooSpieler meinSpieler) {
        if (persistance.getUsers().containsKey(meinSpieler.getName())) {
            logger.fine("getUserCardOrder| Username found, loading previous card ordering");
            List<Integer> values = persistance.getUsers().get(meinSpieler.getName());
            List<YoolooKarte> cards = Arrays.asList(meinSpieler.getAktuelleSortierung());
            YoolooKarte[] newOrder = new YoolooKarte[cards.size()];
            for (int i = 0; i < newOrder.length; i++) {
                int curVal = values.get(i);
                newOrder[i] = cards.stream().filter(c -> c.getWert() == curVal).findFirst().orElseThrow(() -> new IllegalArgumentException("Card not found: " + curVal));
            }
            return newOrder;
        }
        logger.fine("getUserCardOrder| User unknown, returning current card ordering");
        return meinSpieler.getAktuelleSortierung();
    }
}
