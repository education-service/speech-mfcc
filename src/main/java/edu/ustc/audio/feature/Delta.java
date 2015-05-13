package edu.ustc.audio.feature;

/**
 * 通过线性回归计算2维数据的delta
 *
 * @reference Spectral Features for Automatic Text-Independent Speaker
 *            Recognition @author Tomi Kinnunen, @fromPage 83
 *
 * @author wanggang
 *
 */
public class Delta {

	/**
	 * @param M
	 *            regression window size <br>
	 *            i.e.,number of frames to take into account while taking delta
	 */
	int M;

	public Delta() {
		//
	}

	/**
	 * @param M
	 *            length of regression window
	 */
	public void setRegressionWindow(int M) {
		this.M = M;
	}

	public double[][] performDelta2D(double[][] data) {
		int noOfMfcc = data[0].length;
		int frameCount = data.length;
		// 1. calculate sum of mSquare i.e., denominator
		double mSqSum = 0;
		for (int i = -M; i < M; i++) {
			mSqSum += Math.pow(i, 2);
		}
		// 2.calculate numerator
		double delta[][] = new double[frameCount][noOfMfcc];
		for (int i = 0; i < noOfMfcc; i++) {
			// handle the boundary
			// 0 padding results best result
			// from 0 to M
			for (int k = 0; k < M; k++) {
				// delta[k][i] = 0; //0 padding
				delta[k][i] = data[k][i]; // 0 padding
			}
			// from frameCount-M to frameCount
			for (int k = frameCount - M; k < frameCount; k++) {
				// delta[l][i] = 0;
				delta[k][i] = data[k][i];
			}
			for (int j = M; j < frameCount - M; j++) {
				// travel from -M to +M
				double sumDataMulM = 0;
				for (int m = -M; m <= +M; m++) {
					// System.out.println("Current m -->\t"+m+
					// "current j -->\t"+j + "data [m+j][i] -->\t"+data[m +
					// j][i]);
					sumDataMulM += m * data[m + j][i];
				}
				// 3. divide
				delta[j][i] = sumDataMulM / mSqSum;
			}
		}// end of loop

		// System.out.println("Delta **************");
		// ArrayWriter.print2DTabbedDoubleArrayToConole(delta);
		return delta;
	}// end of fn

	public double[] performDelta1D(double[] data) {
		int frameCount = data.length;

		double mSqSum = 0;
		for (int i = -M; i < M; i++) {
			mSqSum += Math.pow(i, 2);
		}
		double[] delta = new double[frameCount];

		for (int k = 0; k < M; k++) {
			delta[k] = data[k]; // 0 padding
		}
		// from frameCount-M to frameCount
		for (int k = frameCount - M; k < frameCount; k++) {
			delta[k] = data[k];
		}
		for (int j = M; j < frameCount - M; j++) {
			// travel from -M to +M
			double sumDataMulM = 0;
			for (int m = -M; m <= +M; m++) {
				// System.out.println("Current m -->\t"+m+ "current j -->\t"+j +
				// "data [m+j][i] -->\t"+data[m + j][i]);
				sumDataMulM += m * data[m + j];
			}
			// 3. divide
			delta[j] = sumDataMulM / mSqSum;
		}
		// System.out.println("Delta 1d **************");
		// ArrayWriter.printDoubleArrayToConole(delta);
		return delta;
	}
}
