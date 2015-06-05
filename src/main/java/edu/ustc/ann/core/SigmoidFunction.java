package edu.ustc.ann.core;

/**
 * Sigmoid传递函数，在0-1之间
 */
public class SigmoidFunction implements ITransferFunction {

	@Override
	public double transfer(double value) {
		return 1 / (1 + Math.exp(-value));
	}

}
