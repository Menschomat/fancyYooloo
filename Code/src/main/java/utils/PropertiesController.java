package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public abstract class PropertiesController {
    private static final ClassLoader classLoader = PropertiesController.class.getClassLoader();
    private static final Properties properties = new Properties();
    public static Properties getProperties() {
        String path = "./application.properties";
        try {
            InputStream fis = new FileInputStream(path);
            properties.load(fis);
        } catch (IOException e) {
            try (InputStream input = Objects.requireNonNull(classLoader.getResource("application.properties")).openStream()) {
                properties.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return properties;
    }
}
