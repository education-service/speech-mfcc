package edu.ustc.svm.iris;

/**
 * Created with IntelliJ IDEA.
 * User: larswright
 * Date: 12/7/13
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluationMetrics {

    private final int correctlyClassified;
    private final int incorrectlyClassified;

    public EvaluationMetrics(int correctlyClassified, int incorrectlyClassified) {
        this.correctlyClassified = correctlyClassified;
        this.incorrectlyClassified = incorrectlyClassified;
    }

    public int getCorrectlyClassified() {
        return correctlyClassified;
    }

    public int getIncorrectlyClassified() {
        return incorrectlyClassified;
    }
}
