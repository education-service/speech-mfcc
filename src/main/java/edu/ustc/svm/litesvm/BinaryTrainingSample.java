package edu.ustc.svm.litesvm;

import java.util.Collection;

public class BinaryTrainingSample {

	public final boolean label;
	public final Vector input;

	public BinaryTrainingSample(boolean label, Vector input) {
		this.label = label;
		this.input = input;
	}

	public BinaryTrainingSample(boolean label, Collection<Double> input) {
		this.label = label;
		this.input = Vectors.newVector(input);
	}

	public BinaryTrainingSample(boolean label, double... inputs) {
		this.label = label;
		this.input = Vectors.newVector(inputs);
	}

}
