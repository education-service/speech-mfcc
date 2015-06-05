package edu.ustc.ann.core;

/**
 * 基于Analyzer的结果，往目标结果方向修正权重
 */
public class Learner {

	private double[] bias;
	private double[] vWeights;
	private double[][] wWeights;
	private double bOut;

	public Learner(double t, double fOut, double[] f, double[] vWeights, double[][] wWeights, double[] bias,
			double bOut, int neurons, double[] x, int dimension) {
		this.bias = new double[neurons];
		this.vWeights = new double[neurons];
		this.wWeights = new double[dimension][neurons];

		initLearn(t, fOut, f, vWeights, wWeights, bias, bOut, neurons, x, dimension);
	}

	/**
	 * Initialize the learn
	 * @param t Output result
	 * @param fOut Out function
	 * @param f Functions
	 * @param vWeights
	 * @param wWeights
	 * @param bias
	 * @param bOut
	 * @param neurons Number of neurons
	 * @param x Inputs
	 * @param dimension Dimension of inputs
	 */
	private void initLearn(double t, double fOut, double[] f, double[] vWeights, double[][] wWeights, double[] bias,
			double bOut, int neurons, double[] x, int dimension) {
		double error = t - fOut;
		double n = 0.05f;
		double dv;
		double[] dwi = new double[neurons];
		double[][] dw = new double[dimension][neurons];
		double[] dbi = new double[neurons];
		double[] db = new double[neurons];

		// Modify v weights
		dv = fOut * (1 - fOut) * error;
		for (int i = 0; i < neurons; i++) {
			this.vWeights[i] = vWeights[i] + n * dv * f[i];
		}

		// Modify bias out
		double dbOut = n * dv * 1;
		this.bOut = (bOut + dbOut);

		// Modify w weights
		for (int i = 0; i < neurons; i++) {
			dwi[i] = f[i] * (1 - f[i]) * vWeights[i] * dv;
			for (int j = 0; j < dimension; j++) {
				dw[j][i] = n * dwi[i] * x[j];
				this.wWeights[j][i] = wWeights[j][i] + dw[j][i];
			}
		}

		// Modify bias
		for (int i = 0; i < neurons; i++) {
			dbi[i] = f[i] * (1 - f[i]) * vWeights[i] * dv;
			db[i] = n * dbi[i] * 1;
			this.bias[i] = bias[i] + db[i];
		}
	}

	public double[] getBias() {
		return bias;
	}

	public double[] getVWeights() {
		return vWeights;
	}

	public double[][] getWWeights() {
		return wWeights;
	}

	public double getBOut() {
		return bOut;
	}

}
