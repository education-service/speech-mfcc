package edu.ustc.knn.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 针对浮点类型数据进行KNN计算的类
 *
 * @author wgybzb
 *
 */
public class KNNCore {

	/**
	 * 找出样本中与当前点point最接近的K个样本
	 * @param point  待测试点
	 * @param sample  样本库中的所有数据点
	 * @param K  一般情况下，样本总数为100的话，取20足矣
	 * @return
	 */
	public static List<String> calcKNN(Point point, HashMap<String, Point> sample, int K) {
		HashMap<String, Double> distances = calcDistances(point, sample, point.getCoordinate().length);
		String[] table = new String[K];
		for (int i = 0; i < K; i++) {
			table[i] = "0=-100000.0";
		}
		for (Entry<String, Double> d : distances.entrySet()) {
			table = InsertSort.toptable(table, d.getKey() + "=" + (-d.getValue()));
		}
		List<String> result = new ArrayList<>();
		for (String str : table) {
			result.add(str.split("=")[0]);
		}
		return result;
	}

	/**
	 * 求一个dim维度的点a与b表中所有点之间的距离
	 * @param a  某个点啊
	 * @param b  点列表
	 * @param dim  点的维度
	 * @return 距离列表
	 */
	private static HashMap<String, Double> calcDistances(Point a, HashMap<String, Point> b, int dim) {
		HashMap<String, Double> d = new HashMap<>();
		for (Entry<String, Point> t : b.entrySet()) {
			//			d.put(t.getKey(), calcDistance(a.getCoordinate(), t.getValue().getCoordinate(), dim));
			d.put(t.getKey(), CosCore.cos(a.getCoordinate(), t.getValue().getCoordinate(), dim));
		}
		return d;
	}

	/**
	 * 求两个向量的距离
	 * @param a  向量1
	 * @param b  向量2
	 * @param dim  向量维度
	 * @return 距离，主要是欧几里得距离
	 */
	public static double calcDistance(double[] a, double[] b, int dim) {
		double d = 0.0d;
		for (int i = 0; i < dim; i++) {
			d += Math.pow(a[i] - b[i], 2);
		}
		return Math.sqrt(d);
	}

	/**
	 * 某个数据点的对象类
	 *
	 * @author wgybzb
	 *
	 */
	public static class Point {

		private double coordinate[];

		public Point(double[] coordinate) {
			this.coordinate = coordinate;
		}

		public double[] getCoordinate() {
			return coordinate;
		}

		public void setCoordinate(double[] coordinate) {
			this.coordinate = coordinate;
		}

	}

}
