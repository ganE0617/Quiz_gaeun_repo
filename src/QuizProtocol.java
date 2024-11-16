import java.util.HashMap;
import java.util.Set;

/**
 * Class implementation of the QuizProtocol
 */
public class QuizProtocol {
    public QuizMethod method; // method
    HashMap<String, String> header; // header of the protocol
    HashMap<String, String> body; // body of the protocol

    /**
     * Constructor used at client-side
     *
     * @param method QuizMethod
     * @param header header of the protocol
     * @param body   body of the protocol
     */
    QuizProtocol(QuizMethod method, HashMap<String, String> header, HashMap<String, String> body) {
        this.method = method;
        this.header = header;
        this.body = body;
        header.put("version", "1.0"); // version information for future updates
    }

    /**
     * Constructor used at server-side
     *
     * @param method     QuizMethod
     * @param header     header of the protocol
     * @param body       body of the protocol
     * @param statusCode status code of the response
     */
    QuizProtocol(QuizMethod method, HashMap<String, String> header, HashMap<String, String> body, String statusCode) {
        this.method = method;
        this.header = header;
        this.body = body;
        header.put("version", "1.0"); // version information for future updates
        header.put("statusCode", statusCode); // status code of the response
    }

    /**
     * @param s Quiz message String wanted to be parsed
     * @return QuizProtocol object
     * @throws Exception if it's not parsable.
     */
    public static QuizProtocol fromString(String s) throws Exception {
        QuizMethod method = null;
        HashMap<String, String> header = new HashMap<>();
        HashMap<String, String> body = new HashMap<>();
        String[] splitString = s.split("\r\n"); // all lines have '\r\n' at their tail

        // validate method
        for (QuizMethod temp : QuizMethod.values()) {
            if (splitString[0].equals(temp.name())) {
                method = temp;
                break;
            }
        }
        if (method == null) {
            throw new Exception("Invalid method"); // throws an exception when the method is invalid
        }

        boolean isHeader = true;

        for (int i = 1; i < splitString.length; i++) {
            String entity = splitString[i];
            if (entity.equals("")) {
                isHeader = false; // '\r\n' is added at the end of the header
                continue;
            }
            String key = entity.split(": ")[0]; // parse the line
            String value = entity.substring(key.length() + 2); // parse the line
            if (isHeader) {
                header.put(key, value);
            } else {
                body.put(key, value);
            }
        }

        return new QuizProtocol(method, header, body);
    }

    /**
     * Use to pre-process the response
     *
     * @param result ServiceResponse object wanted to be processed
     * @return QuizProtocol object
     */
    public static QuizProtocol preProcessResponse(ServiceResponse<HashMap<String, String>> result) {
        HashMap<String, String> header = new HashMap<>();
        HashMap<String, String> body = new HashMap<>();
        String statusCode;
        if (result != null) { // method is valid
            body.putAll(result.data);
            statusCode = result.statusCode;
        } else { // method is invalid
            body.put("exception", "method not allowed.");
            statusCode = "405 Method Not Allowed";
        }
        return new QuizProtocol(QuizMethod.RESPONSE, header, body, statusCode);
    }

    /**
     * @return Quiz message string of an object
     */
    @Override
    public String toString() {
        // METHOD\r\n
        // Header1_key: Header1_value\r\n
        // ...
        // HeaderN_key: HeaderN_value\r\n
        // \r\n
        // body1_key: body2_value\r\n
        // ...
        // bodyM_key: bodyM_value\r\n
        // ETX(End of TeXt)
        StringBuilder result = new StringBuilder(method.name() + "\r\n");
        Set<String> headerKeySet = header.keySet();
        for (String key : headerKeySet) {
            result.append(key).append(": ").append(header.get(key)).append("\r\n");
        }
        result.append("\r\n");
        Set<String> bodyKeySet = body.keySet();
        for (String key : bodyKeySet) {
            result.append(key).append(": ").append(body.get(key)).append("\r\n");
        }

        result.append((char) 3);
        return result.toString();
    }

    /**
     * @return statusCode(null when it is not available)
     */
    public String getStatusCode() {
        return header.getOrDefault("statusCode", null);
    }
} 