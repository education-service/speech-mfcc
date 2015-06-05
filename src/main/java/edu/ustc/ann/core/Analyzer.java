package edu.ustc.ann.core;

/**
 * 基于权重和输入数据计算输出函数
 */
public class Analyzer {

	private double[] fOutArray;
	private double[][] wWeights;
	private double[] vWeights;
	private double[] bias;
	private double fOut;
	private double bOut;
	private int dimension;
	private int neurons;
	private ITransferFunction transferFunction;

	public Analyzer(double[] x, double[][] wWeights, double[] bias, double[] vWeights, double bOut, int neurons,
			ITransferFunction transferFunction, int dimension) {
		this.fOutArray = new double[neurons];
		this.wWeights = wWeights;
		this.bias = bias;
		this.vWeights = vWeights;
		this.bOut = bOut;
		this.neurons = neurons;
		this.transferFunction = transferFunction;
		this.dimension = dimension;
		this.fOut = calculateFOut(x);
	}

	private double calculateFOut(double[] x) {
		for (int i = 0; i < neurons; i++) {
			double sum = 0;
			for (int j = 0; j < dimension; j++) {
				sum = sum + (x[j] * wWeights[j][i]);
			}

			this.fOutArray[i] = transferFunction.transfer(sum + bias[i]);
		}

		this.fOut = 0;
		for (int i = 0; i < neurons; i++) {
			this.fOut += fOutArray[i] * vWeights[i];
		}

		return transferFunction.transfer(fOut + bOut);
	}

	public double[] getFOutArray() {
		return fOutArray;
	}

	public double getFOut() {
		return fOut;
	}

	public double getFOut(double[] x) {
		return calculateFOut(x);
	}

}
