package edu.ustc.ann.main;

import edu.ustc.ann.core.DataUtils;
import edu.ustc.ann.core.Error;
import edu.ustc.ann.core.INeuralNetworkCallback;
import edu.ustc.ann.core.NeuralNetwork;
import edu.ustc.ann.core.Result;

public class SimpleNeuralNetwork {

	public static void main(String[] args) {

		System.out.println("开始计算 ... ");

		float[][] feature = DataUtils.readInputsFromFile("data/ann/feature");
		int[] label = DataUtils.readOutputsFromFile("data/ann/label");

		NeuralNetwork neuralNetwork = new NeuralNetwork(feature, label, new INeuralNetworkCallback() {

			@Override
			public void success(Result result) {
				float[] valueToPredict = new float[] { -0.205f, 0.780f };
				System.out.println("准确率: " + result.getSuccessPercentage());
				System.out.println("预测结果: " + result.predictValue(valueToPredict));
			}

			@Override
			public void failure(Error error) {
				System.out.println("Error: " + error.getDescription());
			}

		});

		neuralNetwork.startLearning();
		System.out.println("完成计算 ... ");
	}

}
