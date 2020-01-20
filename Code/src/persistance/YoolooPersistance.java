package persistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoolooPersistance implements Serializable {


    public Map<String, List<Integer>> users = new HashMap<>();


    public Map<String, List<Integer>> getUsers() {
        return users;
    }

    public void setUsers(Map<String, List<Integer>> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "YoolooPersistance{" +
                "users=" + users +
                '}';
    }
}
