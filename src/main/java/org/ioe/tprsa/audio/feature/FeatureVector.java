/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ioe.tprsa.audio.feature;

import java.io.Serializable;

/**
 * 
 * @author Ganesh Tiwari for storing all coeffs of spectral features<br>
 *         include mfcc + delta mfcc + delta delta mfcc include engergy + delta
 *         energy+ delta delta energy
 */
public class FeatureVector implements Serializable {

	/**
	 * 2d array of feature vector, dimension=noOfFrame*noOfFeatures
	 */
	private double[][] mfccFeature;
	private double[][] featureVector;// all
	private int noOfFrames;
	private int noOfFeatures;

	public FeatureVector() {
	}

	public double[][] getMfccFeature() {
		return mfccFeature;
	}

	public void setMfccFeature(double[][] mfccFeature) {
		this.mfccFeature = mfccFeature;
	}

	public int getNoOfFrames() {
		return featureVector.length;
	}

	public void setNoOfFrames(int noOfFrames) {
		this.noOfFrames = noOfFrames;
	}

	public int getNoOfFeatures() {
		return featureVector[0].length;
	}

	public void setNoOfFeatures(int noOfFeatures) {
		this.noOfFeatures = noOfFeatures;
	}

	/**
	 * returns feature vector
	 * 
	 * @return
	 */
	public double[][] getFeatureVector() {
		return featureVector;
	}

	/**
	 * sets the feature vector array
	 * 
	 * @param featureVector
	 */
	public void setFeatureVector(double[][] featureVector) {
		this.featureVector = featureVector;
	}
}
