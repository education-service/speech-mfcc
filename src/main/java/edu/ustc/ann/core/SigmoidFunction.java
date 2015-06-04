package edu.ustc.ann.core;

/**
 * Sigmoid传递函数，在0-1之间
 */
public class SigmoidFunction implements ITransferFunction {

	@Override
	public float transfer(float value) {
		return (float) (1 / (1 + Math.exp(-value)));
	}

}
