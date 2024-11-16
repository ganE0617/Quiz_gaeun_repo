import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * QuizServer class
 */
public class QuizServer {
    public static void main(String[] args) {
        ServerSocket welcomeSocket = null;
        try {
            // Define quiz questions and answers
            ArrayList<Question> questions = new ArrayList<>();
            questions.add(new Question("What is the capital of France?", "Paris"));
            questions.add(new Question("What is 2 + 2?", "4"));
            questions.add(new Question("What is the capital of Spain?", "Madrid"));

            // Save User data
            HashMap<String, HashMap<Question, String>> userAnswer = new HashMap<>();
            HashMap<String, Integer> userQuestionIndex = new HashMap<>();

            // Read server configuration from file
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("server_info.dat")));
            String host = fileReader.readLine().split(":")[1];
            int port = Integer.parseInt(fileReader.readLine().split(":")[1]);
            fileReader.close();

            welcomeSocket = new ServerSocket(port); // welcome socket

            while (true) {
                Socket connectionSocket = null;
                try {
                    connectionSocket = welcomeSocket.accept(); // connection with the client created
                    new QuizServerInstance(connectionSocket, questions, userAnswer, userQuestionIndex).start(); // handle the quiz session
                } catch (Exception instanceException) {
                    try {
                        if (connectionSocket != null) {
                            connectionSocket.close();
                        }
                    } catch (Exception ignored) {

                    }
                    System.out.println(instanceException.getMessage());
                }
            } // Server never dies....
        } catch (Exception e) {
            try {
                if (welcomeSocket != null) {
                    welcomeSocket.close();
                }
            } catch (Exception ignored) {

            }
            System.out.println(e.getMessage());
        }
    }
} 