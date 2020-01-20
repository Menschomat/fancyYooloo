package persistance;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoolooFileWriter {

    private final String filePath = "users.data";

    Logger logger = Logger.getLogger(getClass().getName());


    public YoolooFileWriter() {
        File usersFile = new File(filePath);
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
                saveUsers(new YoolooPersistance());
            } catch (IOException e) {
                logger.severe("YoolooFileWriter| Encountered IOException: " + e.getMessage());
            }
        }
    }


    public void saveUsers(YoolooPersistance users) {
        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(new File(filePath)))) {
            writer.writeObject(users);
            logger.fine("saveUsers| Success");
        } catch(IOException e) {
            logger.severe("saveUsers| Encountered IOException: " + e.getMessage());
        }
    }

    public YoolooPersistance loadUsers() {
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(new File(filePath)))) {
            YoolooPersistance users = (YoolooPersistance) reader.readObject();
            logger.fine("loadUsers| Success");
            return users;
        } catch(IOException e) {
            logger.severe("loadUsers| Encountered IOException: " + e.getMessage());
        } catch(ClassNotFoundException e) {
            logger.severe("loadUsers| Encountered ClassNotFoundException: " + e.getMessage());
        }
        logger.info("loadUsers| Returning default empty Users");
        return new YoolooPersistance();
    }

}
