package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.*;

public abstract class PropertiesController {
    private static final ClassLoader classLoader = PropertiesController.class.getClassLoader();
    private static final Properties properties = new Properties();

    public static Properties getProperties(String name) {
        String path = name + ".properties";
        try {
            InputStream fis = new FileInputStream(path);
            properties.load(fis);
        } catch (IOException e) {
            try (InputStream input = Objects.requireNonNull(classLoader.getResource(name + ".properties")).openStream()) {
                properties.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return properties;
    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        Properties properties = getProperties("logging");
        Level level = Level.parse(properties.getProperty(".level"));
        logger.setLevel(level);
        Handler[] handlers = logger.getHandlers();
        if (handlers.length == 0) {
            Handler handler = new ConsoleHandler();
            handler.setLevel(level);
            logger.addHandler(handler);
        }
        return logger;
    }
}
