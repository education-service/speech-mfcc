package edu.ustc.audio.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ustc.audio.FeatureExtract;
import edu.ustc.audio.PreProcess;
import edu.ustc.audio.WaveData;
import edu.ustc.audio.feature.FeatureVector;

public class MFCCFeatureMain {

	//	private static FormatControlConf fc = new FormatControlConf();
	private static final int SAMPLING_RATE = 1020; // (int) fc.getRate();
	// int samplePerFrame = 256; // 16ms for 8 khz
	private static final int SAMPLE_PER_FRAME = 256; // 512,23.22ms
	private static final int FEATURE_DIMENSION = 39;
	private FeatureExtract featureExtract;
	private WaveData waveData;
	private PreProcess prp;
	private List<double[]> allFeaturesList = new ArrayList<double[]>();

	private static final String BASE_DIR = "data";

	public MFCCFeatureMain() {
		waveData = new WaveData();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {
		MFCCFeatureMain mfcc = new MFCCFeatureMain();
		/**
		 * 将data下面的train和test的所有音频文件的特征写到对应的文件夹下面，Iris格式
		 * 分别为data/train/train.data,data/test/test.data
		 */
		//		mfcc.writeFeaturesIris("train");
		//		mfcc.writeFeaturesIris("test");
		/**
		 * 将data下面的train和test的所有音频文件的特征写到对应的文件夹下面，SimpleSVM格式
		 * 分别为data/train/train_bc,data/test/test_bc
		 */
		mfcc.writeFeaturesSimpleSVM("train");
		mfcc.writeFeaturesSimpleSVM("test");
	}

	/**
	 * 按照Iris格式输出
	 */
	public void writeFeaturesIris(String dirName) {
		String dataName = BASE_DIR + "/" + dirName + "/" + dirName + ".data";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));) {
			File maleDir = new File(BASE_DIR + "/" + dirName + "/male");
			for (File f : maleDir.listFiles()) {
				System.out.println(f.getPath());
				bw.write(transFeature2IrisStr(getFeature(f.getPath()), "male"));
				bw.newLine();
			}
			File femaleDir = new File(BASE_DIR + "/" + dirName + "/female");
			for (File f : femaleDir.listFiles()) {
				System.out.println(f.getPath());
				bw.write(transFeature2IrisStr(getFeature(f.getPath()), "female"));
				bw.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将特征数据转换成iris格式，针对每条数据
	 */
	private String transFeature2IrisStr(double[] feature, String cate) {
		StringBuffer sb = new StringBuffer();
		for (double f : feature) {
			sb.append(f + "").append(",");
		}
		sb.append(cate);
		return sb.toString();
	}

	/**
	 * 按照SimpleSVM格式输出
	 */
	public void writeFeaturesSimpleSVM(String dirName) {
		String dataName = BASE_DIR + "/" + dirName + "/" + dirName + "_bc";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataName)));) {
			File maleDir = new File(BASE_DIR + "/" + dirName + "/male");
			for (File f : maleDir.listFiles()) {
				System.out.println(f.getPath());
				bw.write(transFeature2SimpleStr(getFeature(f.getPath()), 1));
				bw.newLine();
			}
			File femaleDir = new File(BASE_DIR + "/" + dirName + "/female");
			for (File f : femaleDir.listFiles()) {
				System.out.println(f.getPath());
				bw.write(transFeature2SimpleStr(getFeature(f.getPath()), -1));
				bw.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将特征数据转换成SimpleSVM格式，针对每条数据
	 * cate:  1--男，-1--女
	 */
	private String transFeature2SimpleStr(double[] feature, int cate) {
		StringBuffer sb = new StringBuffer();
		sb.append(cate + "").append("\t");
		int count = 1;
		for (double f : feature) {
			sb.append(count++ + ":" + f).append("\t");
		}
		return sb.toString();
	}

	/**
	 * 提取单个音频的特征数据
	 */
	private double[] getFeature(String fileName) {
		int totalFrames = 0;
		FeatureVector feature = extractFeatureFromFile(new File(fileName));
		for (int k = 0; k < feature.getNoOfFrames(); k++) {
			allFeaturesList.add(feature.getFeatureVector()[k]);
			totalFrames++;
		}
		//		System.out.println("帧数： " + totalFrames + "，特征列表大小： " + allFeaturesList.size());
		// 行代表帧数，列代表特征
		double allFeatures[][] = new double[totalFrames][FEATURE_DIMENSION];
		for (int i = 0; i < totalFrames; i++) {
			double[] tmp = allFeaturesList.get(i);
			allFeatures[i] = tmp;
		}
		// 输出特征
		//		for (int i = 0; i < totalFrames; i++) {
		//			for (int j = 0; j < FEATURE_DIMENSION; j++) {
		//				System.out.println(allFeatures[i][j]);
		//			}
		//		}
		// 计算每帧对应特征的平均值
		double avgFeatures[] = new double[FEATURE_DIMENSION];
		for (int j = 0; j < FEATURE_DIMENSION; j++) { // 循环每列
			double tmp = 0.0d;
			for (int i = 0; i < totalFrames; i++) { // 循环每行
				tmp += allFeatures[i][j];
			}
			avgFeatures[j] = tmp / totalFrames;
		}
		// 将特征数据保存到点中
		//		Points pts[] = new Points[totalFrames];
		//		for (int j = 0; j < totalFrames; j++) {
		//			pts[j] = new Points(allFeatures[j]);
		//		}
		return avgFeatures;
	}

	private FeatureVector extractFeatureFromFile(File speechFile) {
		float[] arrAmp;
		arrAmp = waveData.extractAmplitudeFromFile(speechFile);
		prp = new PreProcess(arrAmp, SAMPLE_PER_FRAME, SAMPLING_RATE);
		featureExtract = new FeatureExtract(prp.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
		featureExtract.makeMfccFeatureVector();
		return featureExtract.getFeatureVector();
	}

}
