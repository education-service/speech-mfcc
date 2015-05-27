package edu.ustc.knn.core;

/**
 * 余弦距离
 *
 * @author wgybzb
 *
 */
public class CosCore {

	/**
	 * 余弦负值，使用余弦定理求两个向量的相似度，值越大相似度越大，
	 * 为了兼容距离相似度，所以取负值，值越小相似度越大。
	 */
	public static double cos(double[] v1, double[] v2, int dim) {
		return (-1.0) * d(v1, v2, dim) / (q(v1) * q(v2));
	}

	/**
	 * 求两个向量的乘积
	 */
	private static double d(double[] v1, double[] v2, int dim) {
		double result = 0.0;
		for (int i = 0; i < dim; i++) {
			result += v1[i] * v2[i];
		}
		return result;
	}

	/**
	 * 求一组向量的平方根
	 */
	private static double q(double[] v) {
		double result = 0.0;
		for (double t : v) {
			result += t * t;
		}
		return Math.sqrt(result);
	}

}