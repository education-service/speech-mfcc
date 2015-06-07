package edu.ustc.speech.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ustc.ann.main.SimpleNeuralNetwork;
import edu.ustc.audio.main.MFCCFeatureMain;
import edu.ustc.knn.core.KNNMain;
import edu.ustc.svm.iris.SVMIrisMain;
import edu.ustc.svm.simple.SimpleSVM;

/**
 * 驱动类
 *
 * @author wanggang
 *
 */
public class SpeechMFCCDriver {

	private static Logger logger = LoggerFactory.getLogger(SpeechMFCCDriver.class);

	/**
	 * 主函数
	 */
	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("Usage: Input <class-name>, eg: \n" + //
					"`MFCCFeatureMain` 处理原始语音数据\n" + //
					"`KNNMain` KNN分类算法\n" + //
					"`SVMIrisMain` LibSVM分类算法\n" + //
					"`SimpleSVM` 简单的SVM分类算法\n" + //
					"`SimpleNeuralNetwork` 简单的ANN分类算法");
			System.exit(-1);
		}
		String[] leftArgs = new String[args.length - 1];
		System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);

		switch (args[0]) {
		case "MFCCFeatureMain":
			logger.info("处理原始语音数据：");
			MFCCFeatureMain.main(leftArgs);
			break;
		case "KNNMain":
			logger.info("KNN分类算法：");
			KNNMain.main(leftArgs);
			break;
		case "SVMIrisMain":
			logger.info("LibSVM分类算法：");
			SVMIrisMain.main(leftArgs);
			break;
		case "SimpleSVM":
			logger.info("简单的SVM分类算法：");
			SimpleSVM.main(leftArgs);
			break;
		case "SimpleNeuralNetwork":
			logger.info("简单的ANN分类算法：");
			SimpleNeuralNetwork.main(leftArgs);
			break;
		default:
			return;
		}

	}

}
