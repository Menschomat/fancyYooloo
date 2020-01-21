package persistance;

import com.sun.istack.internal.NotNull;
import common.YoolooKarte;
import common.YoolooSpieler;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoolooUsers implements Serializable {

    Logger logger = Logger.getLogger(getClass().getName());

    private YoolooFileWriter fileWriter = new YoolooFileWriter();

    private YoolooPersistance persistance;

    public YoolooUsers() {
        persistance = fileWriter.loadUsers();
    }

    public synchronized void setUserCardOrder(String username, List<Integer> cardValuesInOrder) {
        persistance.getUsers().put(username, new ArrayList<>(cardValuesInOrder));
        fileWriter.saveUsers(persistance);
    }

    public synchronized YoolooKarte[] getUserCardOrder(YoolooSpieler meinSpieler) {
        if (persistance.getUsers().containsKey(meinSpieler.getName())) {
            List<Integer> values = persistance.getUsers().get(meinSpieler.getName());
            if (valuesAreValid(values)) {
                logger.fine("Benutzer gefunden. Überschreibe default reihenfolge mit letzter gespielten Reihenfolge");
                List<YoolooKarte> cards = Arrays.asList(meinSpieler.getAktuelleSortierung());
                YoolooKarte[] newOrder = new YoolooKarte[cards.size()];
                for (int i = 0; i < newOrder.length; i++) {
                    int curVal = values.get(i);
                    newOrder[i] = cards.stream().filter(c -> c.getWert() == curVal).findFirst().orElseThrow(() -> new IllegalArgumentException("Wertkarte nicht gefunden: " + curVal));
                }
                return newOrder;
            }
        }
        logger.fine("Benutzer nicht gefunden oder ungültige Daten, gebe default Reihenfolge zurück");
        return meinSpieler.getAktuelleSortierung();
    }

    private boolean valuesAreValid(List<Integer> values) {
        return values.size() == 10 && values.stream().allMatch(Objects::nonNull) && new HashSet<>(values).size() == 10;
    }
}
