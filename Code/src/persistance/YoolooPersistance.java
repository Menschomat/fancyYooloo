package persistance;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class YoolooPersistance implements Serializable {

    public Set<Integer> lastPlayedOrder = new HashSet<>();

    public Set<Integer> getLastPlayedOrder() {
        return lastPlayedOrder;
    }

    public void setLastPlayedOrder(Set<Integer> lastPlayedOrder) {
        this.lastPlayedOrder = lastPlayedOrder;
    }

    @Override
    public String toString() {
        return "YoolooPersistance{" +
                "lastPlayedOrder=" + lastPlayedOrder +
                '}';
    }
}
