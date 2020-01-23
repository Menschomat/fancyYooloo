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
    private static FileHandler fileHandler;
    private static ConsoleHandler consoleHandler;
    private static Level logLevel;

    static {
        Properties properties = getProperties("logging");
        logLevel = Level.parse(properties.getProperty(".level"));
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        try {
            fileHandler = new FileHandler("yooloo.log",true);
            fileHandler.setLevel(logLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties(String name) {
        String path = "./" + name + ".properties";
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
        logger.setLevel(logLevel);
        Handler[] handlers = logger.getHandlers();
        if (handlers.length == 0) {
            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);
        }
        return logger;
    }

}
