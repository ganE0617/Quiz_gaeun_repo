
/**
 * Question class
 */
public class Question {
    public String question;
    public String answer;

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    /**
     * Checks if the answer is correct.
     *
     * @param answer input of the answer
     * @return if the answer is correct or not
     */
    public synchronized boolean isCorrect(String answer) {
        return this.answer.equalsIgnoreCase(answer);
    }
}
