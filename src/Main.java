import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    static String host = "localhost";
    static int port = 4444;
    static Socket socket = null;

    public static void main(String[] args) {

        System.out.println("Welcome to Client!");

        // Run socket with host and port
        socket = connectServer();

        // If we are connected to the Server
        if (socket != null) {

            // Write message, send message to Server and get response
            String resp = sendToServer(writeJson());

            // Packup the response
            unpackResponse(resp);

        }
    }

    static Socket connectServer() {
        try {

            socket = new Socket(host, port);
            System.out.println("Server connected...");
            return socket;

        } catch (Exception e) {

            System.out.println("Socket is null. Connection failed.");
            return null;
        }
    }

    static String writeJson() {
        Scanner sc = new Scanner(System.in);
        String method;
        String message;
        String motorcycleClass;

        while (true) {

            System.out.println("Receive or post information?");
            System.out.print("Type \"get\" or \"post\" and ENTER: ");
            method = sc.nextLine().toLowerCase();

            if (method.equals("get") || method.equals("post")) {
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }

        if (method.equals("post")) {

            while (true) {

                System.out.println("What class?");
                System.out.println("Type \"Classic\" or \"Sport\" ");
                motorcycleClass = sc.nextLine().toLowerCase();

                if (motorcycleClass.equals("classic") || motorcycleClass.equals("sport")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }
            // Call method with input values
            message = createPostMessageAsJson(method, motorcycleClass);

        } else {

            while (true) {

                System.out.println("What class?");
                System.out.println("Type \"Classic\" or \"Sport\" ");
                motorcycleClass = sc.nextLine().toLowerCase();

                if (motorcycleClass.equals("classic") || motorcycleClass.equals("sport")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }

            // Call method with input values
            message = createGetMessageAsJson(method, motorcycleClass);
        }

        return message;
    }
    static String createGetMessageAsJson(String method, String motorcycleType) {
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

    static String createPostMessageAsJson(String method, String motorcycleType) {
        try {

            JSONObject clientObj = new JSONObject();
            JSONObject motorcycle = new JSONObject();
            JSONObject motorcycleClass = new JSONObject();
            JSONObject motorcycleSpecs = new JSONObject();

            clientObj.put("HTTPMethod", method);
            clientObj.put("ContentType", "application/json");
            clientObj.put("URLParametrar", "/" + motorcycleType);

            // Ask the user for data
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

            System.out.println(clientObj);

            return clientObj.toJSONString();

        } catch (Exception e) {
            System.out.println("sendJsonToServer(): " + e.getMessage());
            return "jsonReturn failed";
        }
    }

    static String sendToServer(String message) {
        try {

            // comment here
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Sending this to Server: " + message);
            writer.write(message);
            writer.newLine();
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resp = reader.readLine();

            return resp;

        } catch (Exception e) {
            System.out.println("Failure at try send a message to server: " + e.getMessage());
            return null;
        }
    }

    static boolean unpackResponse(String resp){
        try {

            // comment here
            System.out.println("Response: " + resp);

            JSONParser parser = new JSONParser();
            JSONObject serverResponse = (JSONObject) parser.parse(resp);

            String httpStatusCode = serverResponse.get("httpStatusCode").toString();

            if (httpStatusCode.equals("200")) {

                // Fetch JSON array from the "Body" attribute
                JSONArray data = (JSONArray) parser.parse((String) serverResponse.get("Body"));

                // Loop through each JSON object in the array
                for (Object obj : data) {
                    JSONObject motorcycle = (JSONObject) obj;
                    System.out.println(motorcycle.get("Model"));
                }
                return true;

            } else if (httpStatusCode.equals("202")) {

                System.out.println("Upload successful!");
                return true;

            } else {

                System.out.println("Technical problem with the server, please try again later.");
                return false;

            }

        } catch (Exception e) {
            System.out.println("There's error with response by Server");
            return false;
        }
    }
}
