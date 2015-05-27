package edu.ustc.svm.iris;

import java.io.File;
import java.io.IOException;

import libsvm.svm_model;

import org.apache.commons.io.FilenameUtils;

import com.google.common.io.Files;

public class IrisMain {

	public static final int PERCENTAGE = 60;

	private SVM svm = new SVM();
	private static final String RESULTS_FILE = "svm.results";

	public static void main(String[] args) {
		// data/iris/iris.data output
		checkArgLengthAndPrintUsage(args);

		String inputDataPath = args[0];
		String outputResultsPath = args[1];

		IrisMain driver = new IrisMain();
		driver.run(inputDataPath, outputResultsPath);
	}

	/**
	 * Run an iteration of svm learning against the iris dataset, evaluate the performance and write out a results file.
	 */
	public void run(String inputPath, String resultsPath) {
		StringBuilder results = new StringBuilder();

		Dataset[] trainingAndTesting = MLFileReader.readFile(new File(inputPath)).split(PERCENTAGE);
		Dataset trainingData = trainingAndTesting[0];
		Dataset testingData = trainingAndTesting[1];

		logDatasetInformation(results, trainingData, testingData);

		svm_model model = svm.trainModel(trainingData);
		EvaluationMetrics metrics = evaluateModel(testingData, model);
		writeResultsFile(results, resultsPath, trainingData, model, metrics);
	}

	private static void checkArgLengthAndPrintUsage(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage:");
			System.out.println("\tiris <iris dataset path> <results output directory>:");
			System.exit(0);
		}
	}

	private void writeResultsFile(StringBuilder results, String resultsPath, Dataset trainingData, svm_model model,
			EvaluationMetrics metrics) {
		try {
			File resultsDirectory = new File(resultsPath);
			if (!resultsDirectory.exists()) {
				resultsDirectory.mkdir();
			}

			File resultsFile = new File(FilenameUtils.concat(resultsPath, RESULTS_FILE));

			results.append("\nSVM Model Information:\n");
			for (int i = 0; i < model.nSV.length; i++) {
				results.append("\tNumber of support Vectors for class: " + trainingData.getClassName(i) + " is: "
						+ model.nSV[i] + "\n");
			}

			results.append("\nCorrectly Classified: " + metrics.getCorrectlyClassified() + "\nIncorrectly Classified: "
					+ metrics.getIncorrectlyClassified() + "\n");

			Files.write(results.toString().getBytes(), resultsFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private EvaluationMetrics evaluateModel(Dataset testingData, svm_model model) {
		int correct = 0;
		int incorrect = 0;
		for (Observation observation : testingData.getObservations()) {
			double predictedCode = svm.classifyInstance(observation, model);
			if (predictedCode == testingData.getClassCode(observation)) {
				correct++;
			} else {
				incorrect++;
			}
		}
		return new EvaluationMetrics(correct, incorrect);
	}

	private void logDatasetInformation(StringBuilder results, Dataset trainingData, Dataset testingData) {
		results.append("Total number of instances in the file: "
				+ (trainingData.getObservations().size() + testingData.getObservations().size()) + "\n");
		results.append("Total number of observations allocated for Training: " + trainingData.getObservations().size()
				+ "\n");
		results.append("Total number of observations allocated for Testing: " + testingData.getObservations().size()
				+ "\n");
		results.append("Total number of classes observed: " + trainingData.getNumberOfClasses() + "\n");
		results.append("Total number of features observed: " + trainingData.getNumberOfFeatures() + "\n");
	}

}
