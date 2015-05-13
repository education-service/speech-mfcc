/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ioe.tprsa.mediator;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.ioe.tprsa.audio.FeatureExtract;
import org.ioe.tprsa.audio.FormatControlConf;
import org.ioe.tprsa.audio.PreProcess;
import org.ioe.tprsa.audio.WaveData;
import org.ioe.tprsa.audio.feature.FeatureVector;
import org.ioe.tprsa.classify.speech.HiddenMarkov;
import org.ioe.tprsa.classify.speech.vq.Codebook;
import org.ioe.tprsa.classify.speech.vq.Points;
import org.ioe.tprsa.db.DataBase;
import org.ioe.tprsa.db.ObjectIODataBase;
import org.ioe.tprsa.db.TrainingTestingWaveFiles;
import org.ioe.tprsa.util.ArrayWriter;

/**
 * 
 * @author Ganesh Tiwari
 */
public class Operations {

	TrainingTestingWaveFiles trainTestWavs;
	FormatControlConf fc = new FormatControlConf();
	int samplingRate = (int) fc.getRate();
	// int samplePerFrame = 256;//16ms for 8 khz
	int samplePerFrame = 512;// 23.22ms
	int FEATUREDIMENSION = 39;
	String[] words;
	String[] users;
	File[][] wavFiles;
	FeatureExtract fExt;
	WaveData wd;
	PreProcess prp;
	Codebook cb;
	List<double[]> allFeaturesList = new ArrayList<double[]>();
	HiddenMarkov mkv;
	DataBase db;
	private HiddenMarkov hmmModels[];

	public Operations() {
		wd = new WaveData();
	}

	public void generateCodebook() {
		trainTestWavs = new TrainingTestingWaveFiles("train");
		int totalFrames = 0;
		wavFiles = trainTestWavs.readWaveFilesList();
		for (int i = 0; i < wavFiles.length; i++) {
			for (int j = 0; j < wavFiles[i].length; j++) {
				System.out.println("Currently :::" + wavFiles[i][j].getAbsoluteFile());
				FeatureVector feature = extractFeatureFromFile(wavFiles[i][j]);
				for (int k = 0; k < feature.getNoOfFrames(); k++) {
					allFeaturesList.add(feature.getFeatureVector()[k]);
					totalFrames++;//
				}
			}
		}
		System.out.println("total frames  " + totalFrames + "  allFeaturesList.size   " + allFeaturesList.size());
		// make a single 2d array of all features
		double allFeatures[][] = new double[totalFrames][FEATUREDIMENSION];
		for (int i = 0; i < totalFrames; i++) {
			double[] tmp = allFeaturesList.get(i);
			allFeatures[i] = tmp;
		}
		Points pts[] = new Points[totalFrames];
		for (int j = 0; j < totalFrames; j++) {
			pts[j] = new Points(allFeatures[j]);
		}
		System.out.println("Generating Codebook........");
		Codebook cbk = new Codebook(pts);
		cbk.saveToFile();
		System.out.println("Codebook Generation Completed");
		// hmmTrain();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void hmmTrain() {
		System.out.println("inside hmm train");
		trainTestWavs = new TrainingTestingWaveFiles("train");
		cb = new Codebook();
		// for each training word
		int quantized[][];
		// extract features
		wavFiles = trainTestWavs.readWaveFilesList();
		words = trainTestWavs.readWordWavFolder();
		for (int i = 0; i < wavFiles.length; i++) {
			// for each training samples
			quantized = new int[wavFiles[i].length][];// training sequence
			String currentWord = words[i];
			System.out.println("Current Word :::" + currentWord);
			for (int j = 0; j < wavFiles[i].length; j++) {
				System.out.println("Currently :::" + wavFiles[i][j].getAbsoluteFile());
				FeatureVector feature = extractFeatureFromFile(wavFiles[i][j]);
				// get Points object from feature vector
				Points[] pts = getPointsFromFeatureVector(feature);
				quantized[j] = cb.quantize(pts);
				// ArrayWriter.printIntArrayToConole(quantized[j]);
			}
			mkv = new HiddenMarkov(6, 256);
			// TODO: value, MAKE CONSTANTS

			// do training
			System.out.println("Training.......");
			mkv.setTrainSeq(quantized);
			mkv.train();
			mkv.save(currentWord);
			System.out.println("Word  " + currentWord + " is trained");
		}
		System.out.println("HMM Train Completed");
	}

	public String hmmGetWordFromFile(File speechFile) {
		// extract features
		FeatureVector feature = extractFeatureFromFile(speechFile);
		return hmmGetWordWithFeature(feature);
	}

	public String hmmGetWordFromFileByteArray(byte[] byteArray) {
		// extract features
		FeatureVector feature = extractFeatureFromFileByteArray(byteArray);
		return hmmGetWordWithFeature(feature);
	}

	public String hmmGetWordFromAmplitureArray(float[] byteArray) {
		// extract features
		FeatureVector feature = extractFeatureFromExtractedAmplitureByteArray(byteArray);
		return hmmGetWordWithFeature(feature);
	}

	public String hmmGetWordWithFeature(FeatureVector feature) {
		Points[] pts = getPointsFromFeatureVector(feature);
		cb = new Codebook();
		// quantize using Codebook
		int quantized[] = cb.quantize(pts);

		// read registered/trained words
		db = new ObjectIODataBase();
		db.setType("hmm");
		words = db.readRegistered();
		db = null;
		System.out.println("registred words ::: count : " + words.length);
		ArrayWriter.printStringArrayToConole(words);
		hmmModels = new HiddenMarkov[words.length];

		// read hmmModels
		for (int i = 0; i < words.length; i++) {
			hmmModels[i] = new HiddenMarkov(words[i]);
		}
		// find the likelihood by viterbi decoding of quantized sequence
		double likelihoods[] = new double[words.length];
		for (int j = 0; j < words.length; j++) {
			likelihoods[j] = hmmModels[j].viterbi(quantized);
			System.out.println("Likelihood with " + words[j] + " is " + likelihoods[j]);
		}
		// find the largest likelihood
		double highest = Double.NEGATIVE_INFINITY;
		int wordIndex = -1;
		for (int j = 0; j < words.length; j++) {
			if (likelihoods[j] > highest) {
				highest = likelihoods[j];
				wordIndex = j;
			}
		}
		System.out.println("Best matched word " + words[wordIndex]);
		return words[wordIndex];
	}

	/**
	 * 
	 * @param byteArray
	 * @return
	 * @throws Exception
	 */
	public FeatureVector extractFeatureFromFileByteArray(byte[] byteArray) {
		float[] arrAmp;
		arrAmp = wd.extractAmplitudeFromFileByteArray(byteArray);
		return extractFeatureFromExtractedAmplitureByteArray(arrAmp);
	}

	/**
	 * 
	 * @param byteArray
	 * @return
	 * @throws Exception
	 */
	public FeatureVector extractFeatureFromExtractedAmplitureByteArray(float[] arrAmp) {
		prp = new PreProcess(arrAmp, samplePerFrame, samplingRate);
		fExt = new FeatureExtract(prp.framedSignal, samplingRate, samplePerFrame);
		fExt.makeMfccFeatureVector();
		return fExt.getFeatureVector();
	}

	/**
	 * 
	 * @param speechFile
	 * @return
	 * @throws Exception
	 */
	private FeatureVector extractFeatureFromFile(File speechFile) {
		float[] arrAmp;
		arrAmp = wd.extractAmplitudeFromFile(speechFile);
		return extractFeatureFromExtractedAmplitureByteArray(arrAmp);
	}

	/**
	 * 
	 * @param features
	 * @return
	 */
	private Points[] getPointsFromFeatureVector(FeatureVector features) {
		// get Points object from all feature vector
		Points pts[] = new Points[features.getFeatureVector().length];
		for (int j = 0; j < features.getFeatureVector().length; j++) {
			pts[j] = new Points(features.getFeatureVector()[j]);
		}
		return pts;
	}

	/**
	 * 
	 * @param word
	 * @return
	 */
	public boolean checkWord(String word) {
		db = new ObjectIODataBase();
		db.setType("hmm");
		words = db.readRegistered();
		for (int i = 0; i < words.length; i++) {
			if (words[i].equalsIgnoreCase(word)) { return true;// word found
			}
		}
		return false;// word not found
	}

	/**
	 * 
	 * @return
	 */
	public boolean checkSelectedPath() {
		return true;
	}

	double test0[][] = // user0
	{ { 1.0, 2.5, 5.0, 10, 3.0, 8.0, 4.0, 45.0 }, { 1.0, 2.0, 4.0, 10, 3.0, 9.0, 3.5, 52.0 }, { 1.0, 2.2, 5.0, 11, 3.0, 9.0, 4.0, 52.0 },
			{ 1.0, 3.0, 5.0, 9, 3.0, 10.0, 3.1, 51.0 }, { 1.0, 2.0, 6.0, 10, 3.0, 9.0, 4.0, 54.0 }, { 1.0, 2.2, 5.0, 12, 3.0, 8.0, 4.0, 52.0 } };
	double test1[][] = // user1
	{ { 2.0, 2.5, 5.0, 20, 3.0, 18.0, 4.0, 150.0 }, { 2.0, 2.0, 4.0, 19, 3.0, 19.0, 3.5, 142.0 }, { 2.0, 2.5, 5.0, 20, 3.0, 19.0, 4.0, 150.0 },
			{ 2.0, 3.0, 5.0, 20, 3.0, 18.0, 3.1, 151.0 }, { 2.0, 2.0, 6.0, 20, 3.0, 19.0, 4.0, 150.0 }, { 2.0, 2.7, 5.0, 22, 3.0, 18.0, 4.0, 145.0 } };
	double test2[][] = // suer
	{ { 12.0, 2.5, 5.0, 30, 13.0, 18.0, 4.0, 10.0 }, { 10.0, 2.0, 4.0, 30, 13.0, 19.0, 3.5, 12.0 }, { 12.0, 2.5, 5.0, 33, 13.0, 19.0, 4.0, 10.0 },
			{ 9.0, 3.0, 5.0, 30, 13.0, 18.0, 3.1, 11.0 }, { 11.0, 2.0, 6.0, 30, 13.0, 19.0, 4.0, 10.0 },
			{ 12.0, 2.7, 5.0, 31, 13.0, 18.0, 4.0, 12.0 } };
	double test3[][] = // suer
	{ { 322.0, 2.5, 5.0, 30, 303.0, 18.0, 4.0, 300.0 }, { 312.0, 2.0, 4.0, 30, 353.0, 18.0, 3.5, 312.0 },
			{ 312.0, 2.5, 5.0, 30, 313.0, 19.0, 4.0, 300.0 }, { 322.0, 3.0, 5.0, 30, 303.0, 16.0, 3.1, 311.0 },
			{ 312.0, 2.0, 6.0, 30, 313.0, 19.0, 4.0, 300.0 }, { 332.0, 2.7, 5.0, 30, 313.0, 12.0, 4.0, 302.0 } };
	double test4[][] = { { 412.0, 2.5, 5.0, 30, 400.0, 18.0, 41.0, 14.0 }, { 412.0, 2.0, 8.0, 30, 413.0, 19.0, 43.5, 12.0 },
			{ 400.0, 1.5, 5.0, 30, 413.0, 19.0, 44.0, 9.0 }, { 412.0, 3.0, 3.0, 30, 413.0, 18.0, 43.1, 11.0 },
			{ 412.0, 2.0, 6.0, 30, 433.0, 19.0, 44.0, 15.0 }, { 400.0, 1.7, 9.0, 30, 433.0, 28.0, 40.0, 12.0 } };
	double train[][][] = { {// user0
			{ 1.0, 2.5, 5.0, 10, 3.0, 8.0, 4.0, 50.0 }, { 1.0, 2.0, 4.0, 10, 3.0, 9.0, 3.5, 52.0 }, { 1.0, 2.5, 5.0, 10, 3.0, 9.0, 4.0, 40.0 },
					{ 1.0, 3.0, 5.0, 10, 3.0, 10.0, 3.1, 51.0 }, { 1.0, 2.0, 6.0, 10, 3.0, 9.0, 4.0, 50.0 },
					{ 1.0, 2.7, 5.0, 10, 3.0, 8.0, 4.0, 59.0 } }, {// user1
			{ 2.0, 2.5, 5.0, 20, 3.0, 18.0, 4.0, 150.0 }, { 2.0, 2.0, 4.0, 20, 3.0, 19.0, 3.5, 152.0 }, { 2.0, 2.5, 5.0, 20, 3.0, 19.0, 4.0, 150.0 },
					{ 2.0, 3.0, 5.0, 20, 3.0, 18.0, 3.1, 151.0 }, { 2.0, 2.0, 6.0, 20, 3.0, 19.0, 4.0, 150.0 },
					{ 2.0, 2.7, 5.0, 20, 3.0, 18.0, 4.0, 152.0 } }, {// user2
			{ 12.0, 2.5, 5.0, 30, 13.0, 18.0, 4.0, 10.0 }, { 12.0, 2.0, 4.0, 30, 13.0, 19.0, 3.5, 12.0 },
					{ 12.0, 2.5, 5.0, 30, 13.0, 19.0, 4.0, 10.0 }, { 12.0, 3.0, 5.0, 30, 13.0, 18.0, 3.1, 11.0 },
					{ 12.0, 2.0, 6.0, 30, 13.0, 19.0, 4.0, 10.0 }, { 12.0, 2.7, 5.0, 30, 13.0, 18.0, 4.0, 12.0 } }, {// user3
			{ 312.0, 2.5, 5.0, 30, 313.0, 18.0, 4.0, 310.0 }, { 312.0, 2.0, 4.0, 30, 313.0, 19.0, 3.5, 312.0 },
					{ 312.0, 2.5, 5.0, 30, 313.0, 19.0, 4.0, 310.0 }, { 312.0, 3.0, 5.0, 30, 313.0, 18.0, 3.1, 311.0 },
					{ 312.0, 2.0, 6.0, 30, 313.0, 19.0, 4.0, 310.0 }, { 312.0, 2.7, 5.0, 30, 313.0, 18.0, 4.0, 312.0 } }, {// user4
			{ 412.0, 2.5, 5.0, 30, 413.0, 18.0, 44.0, 10.0 }, { 412.0, 2.0, 4.0, 30, 413.0, 19.0, 43.5, 12.0 },
					{ 412.0, 2.5, 5.0, 30, 413.0, 19.0, 44.0, 10.0 }, { 412.0, 3.0, 5.0, 30, 413.0, 18.0, 43.1, 11.0 },
					{ 412.0, 2.0, 6.0, 30, 413.0, 19.0, 44.0, 10.0 }, { 412.0, 2.7, 5.0, 30, 413.0, 18.0, 44.0, 12.0 } }, {// user5
			{ 152.0, 52.5, 55.0, 30, 13.0, 18.0, 4.0, 10.0 }, { 152.0, 52.0, 54.0, 30, 13.0, 19.0, 3.5, 12.0 },
					{ 152.0, 52.5, 55.0, 30, 13.0, 19.0, 4.0, 10.0 }, { 152.0, 53.0, 55.0, 30, 13.0, 18.0, 3.1, 11.0 },
					{ 152.0, 52.0, 56.0, 30, 13.0, 19.0, 4.0, 10.0 }, { 152.0, 52.7, 55.0, 30, 13.0, 18.0, 4.0, 12.0 } }, {// suer6
			{ 162.0, 2.5, 56.0, 30, 13.0, 18.0, 64.0, 10.0 }, { 162.0, 2.0, 46.0, 30, 13.0, 19.0, 63.5, 12.0 },
					{ 162.0, 2.5, 56.0, 30, 13.0, 19.0, 64.0, 10.0 }, { 162.0, 3.0, 56.0, 30, 13.0, 18.0, 63.1, 11.0 },
					{ 162.0, 2.0, 66.0, 30, 13.0, 19.0, 64.0, 10.0 }, { 162.0, 2.7, 56.0, 30, 13.0, 18.0, 64.0, 12.0 } }, {// user7
			{ 12.0, 72.5, 5.0, 30, 13.0, 18.0, 74.0, 170.0 }, { 12.0, 72.0, 4.0, 30, 19.0, 19.0, 73.5, 172.0 },
					{ 12.0, 72.5, 5.0, 30, 13.0, 19.0, 74.0, 170.0 }, { 12.0, 73.0, 5.0, 30, 18.0, 18.0, 73.1, 171.0 },
					{ 12.0, 72.0, 6.0, 30, 13.0, 19.0, 74.0, 170.0 }, { 12.0, 72.7, 5.0, 30, 13.0, 18.0, 74.0, 172.0 } }, {// user8
			{ 12.0, 82.5, 5.0, 30, 823.0, 11.0, 42.0, 180.0 }, { 12.0, 82.0, 4.0, 30, 813.0, 19.0, 32.5, 182.0 },
					{ 12.0, 85.5, 5.0, 30, 823.0, 20.0, 42.0, 180.0 }, { 12.0, 83.0, 6.0, 30, 813.0, 18.0, 32.1, 188.0 },
					{ 12.0, 82.0, 6.0, 30, 813.0, 19.0, 42.0, 180.0 }, { 12.0, 82.7, 5.0, 30, 823.0, 21.0, 42.0, 182.0 } }, };
	// ///////////////////////////////////////////////////////////////////////////////////
}
