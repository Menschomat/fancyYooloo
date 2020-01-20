package persistance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class YoolooUsers implements Serializable {

    private Map<String, YoolooPersistance> users = new HashMap<>();

    public Map<String, YoolooPersistance> getUsers() {
        return users;
    }

    public void setUsers(Map<String, YoolooPersistance> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "YoolooUsers{" +
                "users=" + users +
                '}';
    }
}
