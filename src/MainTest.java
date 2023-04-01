import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
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
    void createGetMessageAsJson() {

        // Skapar en get förfråga
        String method = "GET";
        String motorcycleType = "sport";
        String expectedJson = "{\"URLParametrar\":\"\\/sport\",\"ContentType\":\"application\\/json\",\"HTTPMethod\":\"GET\"}";
        String actualJson = Main.createGetMessageAsJson(method, motorcycleType);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    void sendGetMethodAndReadRespFromServer() throws ParseException, IOException {

        /* Obsevera, här fick jag återskapa socket på grund av att min sparas i statiskt global scope
        och inte i själva metoden. Därför fick jag kopiera innehållet rakt av sendToServer(writeJson()); */

        // connectServer()
        Socket socket = new Socket("localhost", 4444);

        String method = "get";
        String motorcycleType = "classic";

        // writeJson()
        String jsonString = "{\"HTTPMethod\":\"" + method + "\",\"ContentType\":\"application/json\",\"URLParametrar\":\"/" + motorcycleType + "\"}";


        // sendToServer(expectedJson)
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(jsonString);
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String resp = reader.readLine();

        // Unpack the response from Server to get Http status code to compare it with assertTrue
        JSONParser parser = new JSONParser();
        JSONObject serverResponse = (JSONObject) parser.parse(resp);
        String httpStatusCode = serverResponse.get("httpStatusCode").toString();

        boolean ifSucce;

        if (httpStatusCode.equals("200")) {
            ifSucce = true;
        } else if (httpStatusCode.equals("202")) {
            ifSucce = true;
        } else {
            ifSucce = false;
        }

        assertTrue(ifSucce);
    }

    @Test
    void sendPostMethodAndReadRespFromServer() throws ParseException, IOException {

        /* Obsevera, här fick jag återskapa socket på grund av att min sparas i statiskt global scope
        och inte i själva metoden. Därför fick jag kopiera innehållet rakt av sendToServer(writeJson()); */

        // connectServer()
        Socket socket = new Socket("localhost", 4444);

        // writeJson()
        String jsonString = "{\"URLParametrar\":\"\\/classic\",\"ContentType\":\"application\\/json\",\"HTTPMethod\":\"post\",\"Body\":{\"motorcycle\":{\"classic\":{\"Speed\":\"test\",\"Year\":\"test\",\"Model\":\"test\",\"Make\":\"test\",\"Weight\":\"test\",\"Power\":\"test\"}}}}";

        // sendToServer(expectedJson)
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(jsonString);
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String resp = reader.readLine();

        System.out.println(resp);

        // Unpack the response from Server to get Http status code to compare it with assertTrue
        JSONParser parser = new JSONParser();
        JSONObject serverResponse = (JSONObject) parser.parse(resp);
        String httpStatusCode = serverResponse.get("httpStatusCode").toString();

        boolean ifSuccess;

        if (httpStatusCode.equals("200")) {
            ifSuccess = true;
        } else if (httpStatusCode.equals("202")) {
            ifSuccess = true;
        } else {
            ifSuccess = false;
        }
        assertTrue(ifSuccess);
    }


    @Test
    void unpackResponse() {
        // Här testar vi en likadan response som man får från servern efter att ha skickat in "post"-metoden
        String resp = "{\"Body\":\"successfully\",\"httpStatusCode\":\"202\"}";
        assertTrue(Main.unpackResponse(resp));
    }

}