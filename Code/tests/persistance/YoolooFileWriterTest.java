package persistance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class YoolooFileWriterTest {

    YoolooFileWriter fileWriter;

    @BeforeEach
    void init() {
        fileWriter = new YoolooFileWriter();
    }

    @AfterEach
    void cleanup() {
        File file = new File("users.data");
        if (file.exists()) {
            file.delete();
        }
    }

    @org.junit.jupiter.api.Test
    void testFileWriter() {
        YoolooPersistance persistance = new YoolooPersistance();
        persistance.getUsers().put("test", new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)));
        fileWriter.saveUsers(persistance);
        persistance = fileWriter.loadUsers();
        assertTrue(persistance.getUsers().containsKey("test"));
        assertEquals(persistance.getUsers().get("test"), new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)));
    }
}