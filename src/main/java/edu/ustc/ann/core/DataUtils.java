package edu.ustc.ann.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 读取外部数据，加载到内存中
 */
public class DataUtils {

	public static double[][] readInputsFromFile(String fileURI) {
		double[][] fArray = new double[0][];

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fileURI), StandardCharsets.UTF_8);
			fArray = new double[lines.size()][];

			for (int i = 0; i < lines.size(); i++) {
				fArray[i] = convertStringArrayToFloatArray(lines.get(i).split(","));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fArray;
	}

	public static int[] readOutputsFromFile(String fileURI) {
		int[] iArray = new int[0];

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fileURI), StandardCharsets.UTF_8);
			iArray = new int[lines.size()];

			for (int i = 0; i < lines.size(); i++) {
				iArray[i] = Integer.parseInt(lines.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return iArray;
	}

	private static double[] convertStringArrayToFloatArray(String[] num) {
		if (num != null) {
			double fArray[] = new double[num.length];
			for (int i = 0; i < num.length; i++) {
				fArray[i] = Double.parseDouble(num[i]);
			}
			return fArray;
		}
		return null;
	}

}
