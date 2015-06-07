package edu.ustc.ann.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ustc.ann.core.DataUtils;
import edu.ustc.ann.core.Error;
import edu.ustc.ann.core.INeuralNetworkCallback;
import edu.ustc.ann.core.NeuralNetwork;
import edu.ustc.ann.core.Result;

public class SimpleNeuralNetwork {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Usage: <int-iterNum>");
			System.exit(-1);
		}
		int maxIters = Integer.parseInt(args[0]);

		System.out.println("开始计算 ... ");

		double[][] trainFeatures = DataUtils.readInputsFromFile("data/ann/train.feature");
		int[] label = DataUtils.readOutputsFromFile("data/ann/train.label");

		NeuralNetwork neuralNetwork = new NeuralNetwork(trainFeatures, label, new INeuralNetworkCallback() {

			@Override
			public void success(Result result) {
				System.out.println("训练准确率: " + result.getSuccessPercentage());
				double[][] testFeatures = DataUtils.readInputsFromFile("data/ann/test.feature");
				List<String> predict = new ArrayList<>();
				for (int i = 0; i < testFeatures.length; i++) {
					predict.add(result.predictValue(testFeatures[i]) + "");
				}
				try (BufferedReader br = new BufferedReader(new FileReader(new File("data/ann/test.label")));) {
					String label;
					int count = 0;
					int index = 0;
					while ((label = br.readLine()) != null) {
						if (label.equalsIgnoreCase(predict.get(index++))) {
							count++;
						}
					}
					System.out.println("测试数据准确率：" + ((double) count / predict.size()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void failure(Error error) {
				System.out.println("Error: " + error.getDescription());
			}

		}, maxIters);

		neuralNetwork.startLearning();
		System.out.println("完成计算 ... ");
	}
}
