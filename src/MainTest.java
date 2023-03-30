import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeEach
    void setUp() {
        System.out.println("Before test");
    }

    @AfterEach
    void tearDown() {
        System.out.println("After test");
    }

    @Test
    void connectServer() throws IOException {
        Socket socket = Main.connectServer();
        assertNotNull(socket, "Socket is null. Connection failed.");
    }

    @Test
    void writeJson() {
    }

    @Test
    void createGetMessageInJson() {
    }

    @Test
    void createPostMessageInJson() {
    }
}