import java.io.BufferedReader;
import java.io.IOException;

public class CommonUtil {
    /**
     * Converts a String to an Integer value
     *
     * @param s value
     * @return Integer value of s(null when it's not possible).
     */
    static Integer stringToInteger(String s) {
        Integer amount;
        try {
            amount = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            amount = null;
        }
        return amount;
    }

    /**
     * Reads all byte from the socket buffer until the byte is ETX(End of TeXt).
     *
     * @param inFromSocket BufferedReader to be read
     * @return WBP(Woojin Bank Protocol) message String from the socket
     * @throws IOException from the BufferedReader.read()
     */
    static String readFromSocketBuffer(BufferedReader inFromSocket) throws IOException {
        int s1;
        StringBuilder requestString = new StringBuilder();
        while ((s1 = inFromSocket.read()) != 3) {
            char character = (char) s1;
            requestString.append(character);
        }
        return requestString.toString();
    }
}
