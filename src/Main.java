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

    public static void main(String[] args) {

        System.out.println("Welcome to Client!");

        // comment here
        if (connectServer()) {

            // comment here
            sendToServer(writeJson());

            // comment here
            getResponse();

        }

    }

    static boolean connectServer() {
        try {
            // comment here
            socket = new Socket(host, port);
            System.out.println("Server connected...");
            return true;

        } catch (Exception e){
            System.out.println("Unable to connect the Server...");
            return false;
        }
    }

    static String writeJson() {

        // comment here

        System.out.println("Receive or post information?");
        System.out.print("Type \"get\" or \"post\" and ENTER: ");

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

            // comment here

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

    static void sendToServer(String message) {
        try {

            // comment here
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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

            // comment here
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resp = reader.readLine();
            System.out.println("Response: " + resp);

            JSONParser parser = new JSONParser();
            JSONObject serverResponse = (JSONObject) parser.parse(resp);

            String httpStatusCode = serverResponse.get("httpStatusCode").toString();

            if (httpStatusCode.equals("200")) {

                // Hämta JSON array från "Body" attributet
                JSONArray data = (JSONArray) parser.parse((String) serverResponse.get("Body"));

                // Loopa igenom varje JSON objekt i arrayen
                for (Object obj : data) {
                    JSONObject motorcycle = (JSONObject) obj;
                    System.out.println(motorcycle.get("Model"));
                }

            } else if (httpStatusCode.equals("202")) {

                System.out.println("Uppladdningen lyckats!");

            } else {

                System.out.println("Tekniska problem hos Server, try again later.");

            }

        } catch (Exception e) {
            System.out.println("There's error with response by Server");
        }
    }
}
