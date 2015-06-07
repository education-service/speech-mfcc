package edu.ustc.svm.iris;

import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

public class Dataset {

	private Multiset<String> classCounts = HashMultiset.create();

	private final Map<String, Integer> classMetadata;
	private final List<Observation> observations;

	public Dataset(List<Observation> observations, Map<String, Integer> classMetadata) {
		this.observations = observations;
		this.classMetadata = classMetadata;
	}

	public List<Observation> getObservations() {
		return observations;
	}

	/**
	 * return the class code for a given observation by looking into the class metadata (i.e. class name to class code
	 * mapping).
	 *
	 * @param observation
	 * @return
	 */
	public int getClassCode(Observation observation) {
		return classMetadata.get(observation.getClazz());
	}

	/**
	 * Get the class name from the class code.
	 * @param classCode
	 * @return
	 */
	public String getClassName(Integer classCode) {
		for (String className : classMetadata.keySet()) {
			if (classMetadata.get(className).equals(classCode)) {
				return className;
			}
		}

		return null;
	}

	/**
	 * Get the number of classes/labels for this dataset.
	 * @return
	 */
	public int getNumberOfClasses() {
		return classMetadata.keySet().size();
	}

	/**
	 * Get the number of features for this dataset.
	 * @return
	 */
	public int getNumberOfFeatures() {
		return observations.get(0).getFeatures().size();
	}

	/**
	 * A method to split a dataset into two datasets by a specified percentage.  This method is class-aware.
	 * It will ensure that "percentage" of the observations for each class make it into the training data set.
	 * The remaining observations will be put into the testing data set.
	 *
	 * @param percentage
	 * @return Dataset[]
	 */
	public Dataset[] split(int percentage) {

		generateClassCountsIfAbsent();

		Multiset<String> splitCounts = HashMultiset.create();
		List<Observation> trainingObservations = Lists.newArrayList();
		List<Observation> testingObservations = Lists.newArrayList();

		for (Observation observation : observations) {
			if (getCountServedToTraining(splitCounts, observation) <= (percentage / 100D)) {
				trainingObservations.add(observation);
			} else {
				testingObservations.add(observation);
			}
			splitCounts.add(observation.getClazz());
		}

		return new Dataset[] { new Dataset(trainingObservations, classMetadata),
				new Dataset(testingObservations, classMetadata) };
	}

	private double getCountServedToTraining(Multiset<String> splitCounts, Observation observation) {
		return (splitCounts.count(observation.getClazz()) / ((double) classCounts.count(observation.getClazz())));
	}

	private void generateClassCountsIfAbsent() {
		if (classCounts.isEmpty()) {
			for (Observation observation : observations) {
				classCounts.add(observation.getClazz());
			}
		}
	}

}
