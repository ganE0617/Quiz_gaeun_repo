import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

/**
 * QuizClient
 */
public class QuizClient {
    public static void main(String[] args) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("server_info.dat")));
            String host = fileReader.readLine().split(":")[1];
            int port = Integer.parseInt(fileReader.readLine().split(":")[1]);
            fileReader.close();

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            String uuid;

            // Request UUID from the server
            HashMap<String, String> header = new HashMap<>();
            HashMap<String, String> body = new HashMap<>();
            QuizProtocol request = new QuizProtocol(QuizMethod.GET_UUID, header, body); // create a request for UUID

            // Create a socket and send the request to the server
            Socket socket = new Socket(host, port);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeBytes(request.toString());
            outToServer.flush();

            // Receive the response from the server
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseString = CommonUtil.readFromSocketBuffer(inFromServer);

            // Close the socket
            outToServer.close();
            inFromServer.close();
            socket.close();
            header.clear();
            body.clear();

            QuizProtocol response = QuizProtocol.fromString(responseString); // parse the UUID response
            uuid = response.body.get("uuid");
            System.out.println("Received UUID: " + uuid); // Print the received UUID

            // Start quiz
            header.put("uuid", uuid);
            QuizProtocol startRequest = new QuizProtocol(QuizMethod.START, header, body);

            // Create a socket and send the request to the server
            socket = new Socket(host, port);
            outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeBytes(startRequest.toString());
            outToServer.flush();

            // Receive the response from the server
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseString = CommonUtil.readFromSocketBuffer(inFromServer);

            // Close the socket
            outToServer.close();
            inFromServer.close();
            socket.close();
            header.clear();
            body.clear();

            response = QuizProtocol.fromString(responseString); // parse the start response
            System.out.println(response.body.get("message"));

            while (true) {
                System.out.println("Requesting a new question...");
                
                // Create a request for a new question
                HashMap<String, String> body_ = new HashMap<>();
                HashMap<String, String> header_ = new HashMap<>();
                header_.put("uuid", uuid);
                QuizProtocol request_ = new QuizProtocol(QuizMethod.ASK_QUESTION, header_, body_); // create a request

                // Create a socket and send the request to the server
                socket = new Socket(host, port);
                outToServer = new DataOutputStream(socket.getOutputStream());
                outToServer.writeBytes(request_.toString());
                outToServer.flush();

                // Receive the response from the server
                inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                responseString = CommonUtil.readFromSocketBuffer(inFromServer);

                // Close the socket
                outToServer.close();
                inFromServer.close();
                socket.close();
                header_.clear();
                body_.clear();

                response = QuizProtocol.fromString(responseString); // parse a response

                if (!response.getStatusCode().equals("200 OK")) {
                    System.out.println("No more questions available. Ending the quiz.");
                    break;
                }

                // Print out the question and get the answer
                System.out.println("Question: " + response.body.get("question"));
                System.out.print("Your answer: ");
                String userAnswer = inFromUser.readLine();

                // Send the answer back to the server
                header_.put("uuid", uuid);
                body_.put("answer", userAnswer);
                request_ = new QuizProtocol(QuizMethod.SUBMIT_ANSWER, header_, body_); // create a request for submitting the answer

                // Create a new socket and send the answer
                socket = new Socket(host, port);
                outToServer = new DataOutputStream(socket.getOutputStream());
                outToServer.writeBytes(request_.toString());
                outToServer.flush();

                // Receive the feedback from the server
                inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                responseString = CommonUtil.readFromSocketBuffer(inFromServer);

                // Close the socket
                outToServer.close();
                inFromServer.close();
                socket.close();
                header_.clear();
                body_.clear();

                response = QuizProtocol.fromString(responseString); // parse the feedback response

                // Print out the feedback
                Set<String> bodyKeySet = response.body.keySet();
                System.out.println("\n" + response.getStatusCode() + " -------------------");
                for (String key : bodyKeySet) {
                    System.out.println(key + ": " + response.body.get(key));
                }
                System.out.println("-------------------\n");
            }

            // After exiting the quiz loop
            System.out.println("Requesting score...");

            // Create a request for the score
            HashMap<String, String> scoreBody = new HashMap<>();
            HashMap<String, String> scoreHeader = new HashMap<>();
            scoreHeader.put("uuid", uuid);
            QuizProtocol scoreRequest = new QuizProtocol(QuizMethod.GET_SCORE, scoreHeader, scoreBody); // create a request for score

            // Create a socket and send the request to the server
            socket = new Socket(host, port);
            outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeBytes(scoreRequest.toString());
            outToServer.flush();

            // Receive the response from the server
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseString = CommonUtil.readFromSocketBuffer(inFromServer);

            // Close the socket
            outToServer.close();
            inFromServer.close();
            socket.close();

            // Parse the score response
            response = QuizProtocol.fromString(responseString); // parse the score response

            // Print out the score
            System.out.println("\n" + response.getStatusCode() + " -------------------");
            Set<String> scoreBodyKeySet = response.body.keySet();
            for (String key : scoreBodyKeySet) {
                System.out.println(key + ": " + response.body.get(key));
            }
            System.out.println("-------------------\n");

            // Optionally, send an end request
            HashMap<String, String> endBody = new HashMap<>();
            HashMap<String, String> endHeader = new HashMap<>();
            endHeader.put("uuid", uuid);
            QuizProtocol endRequest = new QuizProtocol(QuizMethod.END, endHeader, endBody); // create a request to end the quiz

            // Create a socket and send the end request to the server
            socket = new Socket(host, port);
            outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeBytes(endRequest.toString());
            outToServer.flush();

            // Receive the response from the server
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseString = CommonUtil.readFromSocketBuffer(inFromServer);

            // Close the socket
            outToServer.close();
            inFromServer.close();
            socket.close();

            System.out.println("Quiz ended.");
        } catch (Exception e) {
            System.out.println("----- exception -----");
            e.printStackTrace();
            System.out.println("---------------------\n");
        }
    }
} 