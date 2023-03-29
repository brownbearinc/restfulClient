import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    static String host = "localhost";
    static int port = 4333;
    static Socket socket = null;
    static BufferedReader reader = null;
    static BufferedWriter writer = null;

    public static void main(String[] args) {

        System.out.println("Welcome to Client!");

        boolean ifConnectServer = connectServer();

        String message = createMessageToServer();
        sendDataToServer(message);
        getResponse();

    }

    static boolean connectServer() {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Server connected...");
            return true;

        } catch (Exception e){
            System.out.println("Unable to connect Server...");
            return false;
        }
    }

    static String createMessageToServer() {

        System.out.println("Receive or post information?");
        System.out.print("Type \"GET\" or \"POST\" and ENTER: ");

        Scanner sc = new Scanner(System.in);

        String method = sc.nextLine().toLowerCase();
        String message;
        String motorcycleClass;

        System.out.println("Vilken klass?");

        if (method.equals("post")) {

            System.out.println("Ange \"CLASSIC\" eller \"SPORT\" ");
            motorcycleClass = sc.nextLine().toLowerCase();
            message = createPostMessageInJson(method, motorcycleClass);

        } else {

            System.out.println("Ange \"ALLA\",\"CLASSIC\" eller \"SPORT\" ");
            motorcycleClass = sc.nextLine().toLowerCase();
            message = createGetMessageInJson(method, motorcycleClass);

        }

        return message;
    }

    static String createGetMessageInJson(String method, String motorcycleType) {

        try {
            JSONObject clientObj = new JSONObject();
            JSONObject motorcycle = new JSONObject();
            JSONObject motorcycleClass = new JSONObject();
            JSONObject motorcycleSpecs = new JSONObject();

            clientObj.put("HTTPMethod", method);
            clientObj.put("ContentType", "application/json");

            clientObj.put("URLParametrar", "/" + motorcycleType );

            return clientObj.toJSONString();

        } catch (Exception e) {
            System.out.println("sendJsonToServer(): " + e.getMessage());
            return "jsonReturn failed";
        }
    }

    static String createPostMessageInJson(String method, String motorcycleType) {

        try {
            JSONObject clientObj = new JSONObject();
            JSONObject motorcycle = new JSONObject();
            JSONObject motorcycleClass = new JSONObject();
            JSONObject motorcycleSpecs = new JSONObject();

            clientObj.put("HTTPMethod", method);
            clientObj.put("ContentType", "application/json");
            clientObj.put("URLParametrar", "/" + motorcycleType);

            // Request user about data
            Scanner sc = new Scanner(System.in);
            System.out.print("Make: ");
            String make = sc.nextLine();

            System.out.print("Model: ");
            String model = sc.nextLine();

            System.out.print("Year: ");
            String year = sc.nextLine();

            System.out.print("Weight: ");
            String weight = sc.nextLine();

            System.out.print("Power: ");
            String power = sc.nextLine();

            System.out.print("Speed: ");
            String speed = sc.nextLine();

            motorcycleSpecs.put("Make", make);
            motorcycleSpecs.put("Model", model);
            motorcycleSpecs.put("Year", year);
            motorcycleSpecs.put("Weight", weight);
            motorcycleSpecs.put("Power", power);
            motorcycleSpecs.put("Speed", speed);

            motorcycleClass.put(motorcycleType, motorcycleSpecs);
            motorcycle.put("motorcycle", motorcycleClass);
            clientObj.put("Body", motorcycle);

            return clientObj.toJSONString();

        } catch (Exception e) {
            System.out.println("sendJsonToServer(): " + e.getMessage());
            return "jsonReturn failed";
        }
    }

    static void sendDataToServer(String message) {
        try {

            System.out.println("Sending this to Server: " + message);
            writer.write(message);
            writer.newLine();
            writer.flush();

        } catch (Exception e) {
            System.out.println("Failure at try send a message to server: " + e.getMessage());
        }
    }

    static void getResponse(){
        try {
            //Hämta response från server
            String resp = reader.readLine();
            System.out.println("Response: " + resp);

            //Init Parser för att parsa till JSON Objekt
            JSONParser parser = new JSONParser();

            //Skapar ett JSON objekt från server respons
            JSONObject serverResponse = (JSONObject) parser.parse(resp);

            String httpStatusCode = serverResponse.get("httpStatusCode").toString();

            if (httpStatusCode.equals("200")) {
                String testReturn = "";

                // Hämta JSON array från "Body" attributet
                JSONArray data = (JSONArray) parser.parse((String) serverResponse.get("Body"));

                // Loopa igenom varje JSON objekt i arrayen
                for (Object obj : data) {
                    JSONObject motorcycle = (JSONObject) obj;
                    System.out.println(motorcycle.get("Model"));
                    // testReturn += motorcycle.get("Model");
                }
            } else if (httpStatusCode.equals("202")) {
                System.out.println("Uppladdningen lyckats!");
            } else {
                System.out.println("Tyvärr så Servern har tekniska problem, vänligen försök igen senare.");
            }

            // OBS Body från Server innehåller bara data, inga klass


        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
