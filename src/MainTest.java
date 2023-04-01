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

    // -Er server skall bli testade via testmetoder, minst en för GET och en för POST

    @Test
    void connectServer() throws IOException {
        Socket socket = Main.connectServer();
        assertNotNull(socket, "Socket is null. Connection failed.");
    }
    @Test
    void testCreateGetMessageInJson() {
        // Skapar en get förfråga
        String method = "GET";
        String motorcycleType = "sport";
        String expectedJson = "{\"URLParametrar\":\"\\/sport\",\"ContentType\":\"application\\/json\",\"HTTPMethod\":\"GET\"}";
        String actualJson = Main.createGetMessageAsJson(method, motorcycleType);

        assertEquals(expectedJson, actualJson);
    }
    @Test
    void packUpResponse() {
        // Här testar vi en likadan response som man får från servern efter att ha skickat in "post"-metoden
        String resp = "{\"Body\":\"successfully\",\"httpStatusCode\":\"202\"}";
        assertTrue(Main.unpackResponse(resp));
    }

}