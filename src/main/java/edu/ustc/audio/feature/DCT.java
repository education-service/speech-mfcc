package edu.ustc.audio.feature;

/**
 * 反傅立叶变换，这里使用DCT余弦变换，因为只有实系数
 *
 * @author wanggang
 *
 */
public class DCT {

	/**
	 * number of mfcc coeffs
	 */
	int numCepstra;
	/**
	 * number of Mel Filters
	 */
	int M;

	/**
	 * @param len
	 *            length of array, i.e., number of features
	 * @param M
	 *            numbe of Mel Filters
	 * @return
	 */
	public DCT(int numCepstra, int M) {
		this.numCepstra = numCepstra;
		this.M = M;
	}

	public double[] performDCT(double y[]) {
		double cepc[] = new double[numCepstra];
		// perform DCT
		for (int n = 1; n <= numCepstra; n++) {
			for (int i = 1; i <= M; i++) {
				cepc[n - 1] += y[i - 1] * Math.cos(Math.PI * (n - 1) / M * (i - 0.5));
			}
		}
		return cepc;
	}

}
