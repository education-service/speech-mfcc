package edu.ustc.svm.litesvm;

public class Kernels {

	private Kernels() {
		//
	}

	public static Kernel Linear() {
		return new Kernel() {
			@Override
			public double product(Vector x1, Vector x2) {
				assert x1.size() == x2.size();

				double sum = 0;
				for (int i = 0; i < x1.size(); i += 1) {
					sum += x1.get(i) * x2.get(i);
				}
				return sum;
			}
		};
	}

	public static Kernel RBF(final double sigma) {

		return new Kernel() {
			@Override
			public double product(Vector x1, Vector x2) {
				assert x1.size() == x2.size();

				double sum = 0;
				for (int i = 0; i < x1.size(); i += 1) {
					sum += Math.pow(x1.get(i) - x2.get(i), 2);
				}

				return Math.exp(-sum / (2.0 * sigma * sigma));
			}
		};
	}

}
