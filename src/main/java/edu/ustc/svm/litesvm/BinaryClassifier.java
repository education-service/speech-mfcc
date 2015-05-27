package edu.ustc.svm.litesvm;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;

public class BinaryClassifier {

	protected Kernel kernel;
	protected List<Boolean> labels;
	protected List<Vector> data;
	protected List<Double> alphas;
	protected double b;

	public double margin(Vector x) {
		assert data != null && data.size() > 0;
		assert x.size() == data.get(0).size();

		double margin = b;
		for (int i = 0; i < data.size(); i++) {

			if (this.labels.get(i))
				margin += this.kernel.product(x, this.data.get(i)) * this.alphas.get(i);
			else
				margin -= this.kernel.product(x, this.data.get(i)) * this.alphas.get(i);
		}
		return margin;
	}

	public boolean predict(Vector x) {
		assert checkValidState();
		assert x.size() == data.get(0).size();
		return margin(x) >= 0;
	}

	public boolean predict(Collection<Double> x) {
		return predict(Vectors.newVector(x));
	}

	public boolean predict(double... x) {
		return predict(Vectors.newVector(x));
	}

	protected boolean checkValidState() {
		Preconditions.checkState(kernel != null);
		Preconditions.checkState(data != null && data.size() > 0);
		Preconditions.checkState(labels != null && labels.size() > 0);
		Preconditions.checkState(alphas != null && alphas.size() > 0);
		Preconditions.checkState(data.size() == labels.size());
		Preconditions.checkState(data.size() == alphas.size());
		return true;
	}

}
