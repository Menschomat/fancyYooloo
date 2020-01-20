package persistance;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoolooFileWriter {

    private final String filePath = "/user";

    Logger logger = Logger.getLogger("YoolooFileWriter");

    public void saveUsers(YoolooUsers users) {
        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(new File(filePath)))) {
            writer.writeObject(users);
            logger.log(Level.FINE, "saveUsers| Success");
        } catch(IOException e) {
            logger.log(Level.SEVERE, "saveUsers| Encountered IOException: " + e.getMessage());
        }
    }

    public YoolooUsers loadUsers() {
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(new File("users")))) {
            YoolooUsers users = (YoolooUsers) reader.readObject();
            logger.log(Level.FINE, "loadUsers| Success");
            return users;
        } catch(IOException e) {
            logger.log(Level.SEVERE, "loadUsers| Encountered IOException: " + e.getMessage());
        } catch(ClassNotFoundException e) {
            logger.log(Level.SEVERE, "loadUsers| Encountered ClassNotFoundException: " + e.getMessage());
        }
        logger.log(Level.SEVERE, "loadUsers| Returning default empty Users");
        return new YoolooUsers();
    }

}
