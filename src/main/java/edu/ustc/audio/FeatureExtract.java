package edu.ustc.audio;

import edu.ustc.audio.feature.Delta;
import edu.ustc.audio.feature.Energy;
import edu.ustc.audio.feature.FeatureVector;
import edu.ustc.audio.feature.MFCC;

/**
 * 特征提取
 *
 * @author wanggang
 *
 */
@SuppressWarnings("unused")
public class FeatureExtract {

	private float[][] framedSignal;
	private int samplePerFrame;
	private int noOfFrames;

	/**
	 * how many mfcc coefficients per frame
	 */
	private int numCepstra = 12;

	private double[][] featureVector;
	private double[][] mfccFeature;
	private double[][] deltaMfcc;
	private double[][] deltaDeltaMfcc;
	private double[] energyVal;
	private double[] deltaEnergy;
	private double[] deltaDeltaEnergy;
	private FeatureVector fv;
	private MFCC mfcc;
	private Delta delta;
	private Energy en;

	// FeatureVector fv;
	/**
	 * constructor of feature extract
	 *
	 * @param framedSignal
	 *            2-D audio signal obtained after framing
	 * @param samplePerFrame
	 *            number of samples per frame
	 */
	public FeatureExtract(float[][] framedSignal, int samplingRate, int samplePerFrame) {
		this.framedSignal = framedSignal;
		this.noOfFrames = framedSignal.length;
		this.samplePerFrame = samplePerFrame;
		mfcc = new MFCC(samplePerFrame, samplingRate, numCepstra);
		en = new Energy(samplePerFrame);
		fv = new FeatureVector();
		mfccFeature = new double[noOfFrames][numCepstra];
		deltaMfcc = new double[noOfFrames][numCepstra];
		deltaDeltaMfcc = new double[noOfFrames][numCepstra];
		energyVal = new double[noOfFrames];
		deltaEnergy = new double[noOfFrames];
		deltaDeltaEnergy = new double[noOfFrames];
		featureVector = new double[noOfFrames][3 * numCepstra + 3];
		delta = new Delta();
	}

	public FeatureVector getFeatureVector() {
		return fv;
	}

	/**
	 * generates feature vector by combining mfcc, and its delta and delta
	 * deltas also contains energy and its deltas
	 */
	public void makeMfccFeatureVector() {
		calculateMFCC();
		doCepstralMeanNormalization();
		// delta
		delta.setRegressionWindow(2);// 2 for delta
		deltaMfcc = delta.performDelta2D(mfccFeature);
		// delta delta
		delta.setRegressionWindow(1);// 1 for delta delta
		deltaDeltaMfcc = delta.performDelta2D(deltaMfcc);
		// energy
		energyVal = en.calcEnergy(framedSignal);

		delta.setRegressionWindow(1);
		// energy delta
		deltaEnergy = delta.performDelta1D(energyVal);
		delta.setRegressionWindow(1);
		// energy delta delta
		deltaDeltaEnergy = delta.performDelta1D(deltaEnergy);
		for (int i = 0; i < framedSignal.length; i++) {
			for (int j = 0; j < numCepstra; j++) {
				featureVector[i][j] = mfccFeature[i][j];
			}
			for (int j = numCepstra; j < 2 * numCepstra; j++) {
				featureVector[i][j] = deltaMfcc[i][j - numCepstra];
			}
			for (int j = 2 * numCepstra; j < 3 * numCepstra; j++) {
				featureVector[i][j] = deltaDeltaMfcc[i][j - 2 * numCepstra];
			}
			featureVector[i][3 * numCepstra] = energyVal[i];
			featureVector[i][3 * numCepstra + 1] = deltaEnergy[i];
			featureVector[i][3 * numCepstra + 2] = deltaDeltaEnergy[i];
		}
		fv.setMfccFeature(mfccFeature);
		fv.setFeatureVector(featureVector);
		System.gc();
	}

	/**
	 * calculates MFCC coefficients of each frame
	 */
	private void calculateMFCC() {
		for (int i = 0; i < noOfFrames; i++) {
			// for each frame i, make mfcc from current framed signal
			mfccFeature[i] = mfcc.doMFCC(framedSignal[i]);// 2D data
		}
	}

	/**
	 * performs cepstral mean substraction. <br>
	 * it removes channel effect...
	 */
	private void doCepstralMeanNormalization() {
		double sum;
		double mean;
		double mCeps[][] = new double[noOfFrames][numCepstra - 1];// same size
																	// as mfcc
		// 1.loop through each mfcc coeff
		for (int i = 0; i < numCepstra - 1; i++) {
			// calculate mean
			sum = 0.0;
			for (int j = 0; j < noOfFrames; j++) {
				sum += mfccFeature[j][i];// ith coeff of all frame
			}
			mean = sum / noOfFrames;
			// subtract
			for (int j = 0; j < noOfFrames; j++) {
				mCeps[j][i] = mfccFeature[j][i] - mean;
			}
		}
	}
}
