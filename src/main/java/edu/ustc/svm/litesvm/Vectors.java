package edu.ustc.svm.litesvm;

import java.util.ArrayList;
import java.util.Collection;

public class Vectors {

	private static class ArrayListVector extends ArrayList<Double> implements Vector {

		private static final long serialVersionUID = -2564665819252512939L;

		public ArrayListVector(int size) {
			super(size);
		}

		public ArrayListVector(Collection<Double> data) {
			super(data);
		}

		public ArrayListVector(double... data) {
			super(data.length);
			for (int i = 0; i < data.length; i++)
				this.add(data[i]);
		}

	}

	public static Vector newVector(int size) {
		Vector x = new ArrayListVector(size);
		for (int i = 0; i < size; i++)
			x.add(0.0);
		return x;
	}

	public static Vector newVector(Collection<Double> data) {
		return new ArrayListVector(data);
	}

	public static Vector newVector(double... data) {
		return new ArrayListVector(data);
	}

}
