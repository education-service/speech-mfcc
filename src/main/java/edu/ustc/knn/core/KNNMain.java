package edu.ustc.knn.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.ustc.knn.core.KNNCore.Point;

public class KNNMain {

	/**
	 * 主函数
	 */
	public static void main(String[] args) {
		// 读取训练数据和测试数据
		HashMap<String, Point> train = getData("data/train/train.data");
		HashMap<String, Point> test = getData("data/test/test.data");
		// 测试结果
		int correct = 0; // 统计正确个数
		int K = 20;
		for (Entry<String, Point> t : test.entrySet()) {
			// 当前点的类别
			String cate = t.getKey().split("-")[1];
			// 当前点与样本库距离最近的K个数据key列表，k是由文件编号和类别组成
			List<String> keys = KNNCore.calcKNN(t.getValue(), train, K);
			// 统计K个数据的类别分布
			HashMap<String, Integer> kmap = new HashMap<>();
			for (String key : keys) {
				if (kmap.get(key.split("-")[1]) == null) {
					kmap.put(key.split("-")[1], 1);
				} else {
					kmap.put(key.split("-")[1], kmap.get(key.split("-")[1]) + 1);
				}
			}
			// 计算出类别
			String calcCate = "female";
			if (kmap.get("male") > kmap.get("female")) {
				calcCate = "male";
			}
			// 判断计算正确与否，并统计正确个数
			if (calcCate.equalsIgnoreCase(cate)) {
				correct++;
			}
		}
		// 统计正确率
		double correctRatio = (double) correct / (double) test.size();
		System.err.println("正确率为：" + correctRatio);

	}

	public static HashMap<String, Point> getData(String dir) {
		HashMap<String, Point> data = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(dir)));) {
			String str = null;
			String[] strs = null;
			int count = 1;
			while ((str = br.readLine()) != null) {
				strs = str.split(",");
				double[] coordinate = new double[strs.length - 1];
				for (int i = 0; i < strs.length - 1; i++) {
					coordinate[i] = Double.parseDouble(strs[i]);
				}
				data.put(count++ + "-" + strs[strs.length - 1], new Point(coordinate));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return data;
	}

}
