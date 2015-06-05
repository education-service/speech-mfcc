package edu.ustc.ann.core;

/**
 * 初始化权重和偏移量
 */
public class HiddenLayerNeuron {

	private double[] bias;
	private double[] vWeights;
	private double[][] wWeights;

	public HiddenLayerNeuron(int neurons, int dimension) throws ZeroNeuronsException, ZeroInputDimensionException {
		this.bias = new double[neurons];
		this.vWeights = new double[neurons];
		this.wWeights = new double[dimension][neurons];

		initWeights(neurons, dimension);
	}

	public double[] getBias() {
		return this.bias;
	}

	public double[] getVWeights() {
		return this.vWeights;
	}

	public double[][] getWWeights() {
		return this.wWeights;
	}

	private void initWeights(int neurons, int dimension) throws ZeroNeuronsException, ZeroInputDimensionException {
		if (neurons == 0)
			throw new ZeroNeuronsException();
		if (dimension == 0)
			throw new ZeroInputDimensionException();

		for (int i = 0; i < neurons; i++) {
			this.bias[i] = Utils.randFloat(-0.5f, 0.5f);
			this.vWeights[i] = Utils.randFloat(-0.5f, 0.5f);
			for (int j = 0; j < dimension; j++) {
				this.wWeights[j][i] = Utils.randFloat(-0.5f, 0.5f);
			}
		}
	}

}
