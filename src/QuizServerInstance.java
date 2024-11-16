import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * QuizServerInstance class
 */
public class QuizServerInstance extends Thread {
    private final Socket socket;
    public ArrayList<Question> questions;
    private BufferedReader inFromClient = null;
    private DataOutputStream outToClient = null;
    private HashMap<String, HashMap<Question, String>> userAnswer;
    private HashMap<String, Integer> userQuestionIndex;

    public QuizServerInstance(Socket socket, ArrayList<Question> questions, HashMap<String, HashMap<Question, String>> userAnswer, HashMap<String, Integer> userQuestionIndex) {
        this.socket = socket;
        this.questions = questions;
        this.userAnswer = userAnswer;
        this.userQuestionIndex = userQuestionIndex;
    }
    /**
     * Get uuid
     *
     * @param request Original request
     * @return ServiceResponse of the request
     */
    private ServiceResponse<HashMap<String, String>> getUuid(QuizProtocol request) {
        HashMap<String, String> body = new HashMap<>();
        body.put("uuid", UUID.randomUUID().toString());
        return ServiceResponse.OK(body);
    }

    /**
     * Start quiz
     *
     * @param request Original request
     * @return ServiceResponse of the request
     */
    private ServiceResponse<HashMap<String, String>> start(QuizProtocol request) {
        String uuid = request.header.get("uuid");
        userAnswer.put(uuid, new HashMap<>());
        userQuestionIndex.put(uuid, 0);

        HashMap<String, String> body = new HashMap<>();
        body.put("message", "quiz started");
        return ServiceResponse.OK(body);
    }

    /**
     * Ask question
     *
     * @param request Original request
     * @return ServiceResponse of the request
     */
    private ServiceResponse<HashMap<String, String>> askQuestion(QuizProtocol request) {
        String uuid = request.header.get("uuid");

        if (userQuestionIndex.get(uuid) >= questions.size()) {
            return ServiceResponse.BadRequest(new HashMap<>() {{
                put("exception", "no more questions");
            }});
        }

        HashMap<String, String> body = new HashMap<>();
        body.put("question", questions.get(userQuestionIndex.get(uuid)).question);
        return ServiceResponse.OK(body);
    }

    /**
     * Submit answer
     *
     * @param request Original request
     * @return ServiceResponse of the request
     */
    private ServiceResponse<HashMap<String, String>> submitAnswer(QuizProtocol request) {
        Question question = questions.get(userQuestionIndex.get(request.header.get("uuid")));

        String uuid = request.header.get("uuid");
        if (!request.body.containsKey("answer")) {
            return ServiceResponse.BadRequest(new HashMap<>() {{
                put("exception", "answer is not valid");
            }});
        }
        String answer = request.body.get("answer");
        userAnswer.get(uuid).put(question, answer);
        userQuestionIndex.put(uuid, userQuestionIndex.get(uuid) + 1);

        boolean isCorrect = question.isCorrect(answer);

        HashMap<String, String> body = new HashMap<>();
        body.put("message", isCorrect ? "Correct!" : "Incorrect!");
        return ServiceResponse.OK(body);
    }

    /**
     * Get score
     *
     * @param request Original request
     * @return ServiceResponse of the request
     */
    private ServiceResponse<HashMap<String, String>> getScore(QuizProtocol request) {
        String uuid = request.header.get("uuid");
        if (userAnswer.get(uuid).size() != questions.size()) {
            return ServiceResponse.BadRequest(new HashMap<>() {{
                put("exception", "not all questions are answered");
            }});
        }
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (question.isCorrect(userAnswer.get(uuid).get(question))) {
                score++;
            }
        }
        HashMap<String, String> body = new HashMap<>();
        body.put("score", String.valueOf(score) + " / " + String.valueOf(questions.size()));
        return ServiceResponse.OK(body);
    }

    /**
     * End quiz
     *
     * @param request Original request
     * @return ServiceResponse of the request
     */
    private ServiceResponse<HashMap<String, String>> end(QuizProtocol request) {
        String uuid = request.header.get("uuid");
        HashMap<String, String> body = new HashMap<>();
        body.put("message", "quiz ended");
        userAnswer.remove(uuid);
        userQuestionIndex.remove(uuid);
        return ServiceResponse.OK(body);
    }

    /**
     * Thread run()
     */
    @Override
    public void run() {
        super.run();
        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream())); //input from the client

            String requestString = CommonUtil.readFromSocketBuffer(inFromClient); //read message string from the client
            QuizProtocol request = QuizProtocol.fromString(requestString); //parse a message

            System.out.println("----- request -----");
            System.out.println(socket.getInetAddress().toString());
            System.out.println(request);

            QuizMethod method = request.method;

            ServiceResponse<HashMap<String, String>> result = null; //service response

            if (method.equals(QuizMethod.GET_UUID)) {
                result = getUuid(request);
            } else if (method.equals(QuizMethod.START)) {
                result = start(request);
            } else if (method.equals(QuizMethod.ASK_QUESTION)) {
                result = askQuestion(request);
            } else if (method.equals(QuizMethod.SUBMIT_ANSWER)) {
                result = submitAnswer(request);
            } else if (method.equals(QuizMethod.GET_SCORE)) {
                result = getScore(request);
            } else if (method.equals(QuizMethod.END)) {
                result = end(request);
            }

            QuizProtocol response = QuizProtocol.preProcessResponse(result); //response WBP message

            System.out.println("----- response -----");
            System.out.println(response);
            System.out.println("--------------------");

            outToClient = new DataOutputStream(socket.getOutputStream());
            outToClient.writeBytes(response.toString()); //send the response message to the client
            outToClient.flush();

            outToClient.close();
            inFromClient.close();
            socket.close();
        } catch (Exception e) {
            try {
                if (inFromClient != null) {
                    inFromClient.close();
                }
                if (outToClient != null) {
                    outToClient.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignored) {

            }
            throw new RuntimeException(e);
        }
    }
}

