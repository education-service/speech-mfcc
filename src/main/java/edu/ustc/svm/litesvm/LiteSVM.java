package edu.ustc.svm.litesvm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class LiteSVM {

	public static final Random RANDOM = new Random(new Date().getTime());

	public static final double DEFAULT_C = 1.0;
	public static final double DEFAULT_TOLERANCE = 1e-4;
	public static final double DEFAULT_ALPHA_TOLERANCE = 1e-7;
	public static final int DEFAULT_MAX_PASSES = 10;
	public static final int DEFAULT_MAX_ITERATIONS = 10000;

	private static final Options DEFAULT_OPTIONS = new Options();

	public static class Options {
		public double C = DEFAULT_C;
		public double tolerance = DEFAULT_TOLERANCE;
		public double alphaTolerance = DEFAULT_ALPHA_TOLERANCE;
		public double maxIterations = DEFAULT_MAX_ITERATIONS;
		public double maxPasses = DEFAULT_MAX_PASSES;
	}

	public static BinaryClassifier trainBinaryClassifier(List<BinaryTrainingSample> samples) {
		return trainBinaryClassifier(samples, Kernels.RBF(0.5));
	}

	public static BinaryClassifier trainBinaryClassifier(List<BinaryTrainingSample> samples, Kernel kernel) {
		return trainBinaryClassifier(samples, kernel, DEFAULT_OPTIONS);
	}

	public static BinaryClassifier trainBinaryClassifier(List<BinaryTrainingSample> samples, Kernel kernel,
			Options options) {

		final List<Boolean> labels = new ArrayList<Boolean>(samples.size());
		final List<Vector> inputs = new ArrayList<Vector>(samples.size());

		for (BinaryTrainingSample sample : samples) {
			labels.add(sample.label);
			inputs.add(sample.input);
		}

		return trainBinaryClassifier(labels, inputs, kernel, options);
	}

	public static BinaryClassifier trainBinaryClassifier(List<Boolean> labels, List<Vector> inputs, Kernel kernel,
			Options options) {

		final int N = inputs.size();
		final BinaryClassifier instance = new BinaryClassifier();

		instance.data = inputs;
		instance.labels = labels;
		instance.kernel = kernel;

		instance.alphas = Vectors.newVector(N);
		instance.b = 0.0;

		int iterations = 0;
		int passes = 0;
		while (passes < options.maxPasses && iterations < options.maxIterations) {

			int alphaChanges = 0;

			for (int i = 0; i < N; i++) {

				double labelI = labels.get(i) ? 1 : -1;
				double Ei = instance.margin(inputs.get(i)) - labelI;

				if ((labelI * Ei < -options.tolerance && instance.alphas.get(i) < options.C)
						|| (labelI * Ei > options.tolerance && instance.alphas.get(i) > 0)) {

					int j = RANDOM.nextInt(N);
					while (j == i)
						j = RANDOM.nextInt(N);

					double labelJ = labels.get(j) ? 1 : -1;
					double Ej = instance.margin(inputs.get(j)) - labelJ;

					double ai = instance.alphas.get(i);
					double aj = instance.alphas.get(j);

					double L = 0;
					double H = options.C;
					if (labelI == labelJ) {
						L = Math.max(0, ai + aj - options.C);
						H = Math.min(options.C, ai + aj);
					} else {
						L = Math.max(0, aj - ai);
						H = Math.min(options.C, options.C + aj - ai);
					}

					if (Math.abs(L - H) < 1e-4)
						continue;

					double eta = 2 * instance.kernel.product(inputs.get(i), inputs.get(j))
							- instance.kernel.product(inputs.get(i), inputs.get(i))
							- instance.kernel.product(inputs.get(j), inputs.get(j));

					if (eta >= 0)
						continue;

					double newAj = aj - labelJ * (Ei - Ej) / eta;
					if (newAj > H)
						newAj = H;
					if (newAj < L)
						newAj = L;
					if (Math.abs(aj - newAj) < 1e-4)
						continue;

					// update alphaJ, then update alphaI base on alphaJ
					double newAi = ai + labelI * labelJ * (aj - newAj);
					instance.alphas.set(j, newAj);
					instance.alphas.set(i, newAi);

					// update the bias term
					double b1 = instance.b - Ei - labelI * (newAi - ai)
							* instance.kernel.product(inputs.get(i), inputs.get(i)) - labelJ * (newAj - aj)
							* instance.kernel.product(inputs.get(i), inputs.get(j));

					double b2 = instance.b - Ej - labelI * (newAi - ai)
							* instance.kernel.product(inputs.get(i), inputs.get(j)) - labelJ * (newAj - aj)
							* instance.kernel.product(inputs.get(j), inputs.get(j));

					instance.b = 0.5 * (b1 + b2);
					if (newAi > 0 && newAi < options.C)
						instance.b = b1;
					if (newAj > 0 && newAj < options.C)
						instance.b = b2;

					alphaChanges += 1;
				}
			}
			//System.out.format("Iteration number %d, alphaChanged = %d", iterations, alphaChanges);

			iterations += 1;
			if (alphaChanges == 0)
				passes += 1;
			else
				passes = 0;
		}

		return instance;
	}

}
