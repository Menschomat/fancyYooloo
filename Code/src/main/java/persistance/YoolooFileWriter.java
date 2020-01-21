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
                logger.severe("IOException: " + e.getMessage());
            }
        }
    }


    public void saveUsers(YoolooPersistance users) {
        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(new File(filePath)))) {
            writer.writeObject(users);
            logger.fine("Spielerdatei gespeichert");
        } catch(IOException e) {
            logger.severe("IOException: " + e.getMessage());
        }
    }

    public YoolooPersistance loadUsers() {
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(new File(filePath)))) {
            YoolooPersistance users = (YoolooPersistance) reader.readObject();
            logger.fine("Spielerdatei geladen");
            return users;
        } catch(IOException e) {
            logger.severe("IOException: " + e.getMessage());
        } catch(ClassNotFoundException e) {
            logger.severe("ClassNotFoundException: " + e.getMessage());
        }
        logger.info("Spielerdatei konnte nicht geladen werden, gebe leeres Objekt zurück");
        return new YoolooPersistance();
    }

}
