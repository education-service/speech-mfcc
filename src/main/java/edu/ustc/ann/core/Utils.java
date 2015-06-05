package edu.ustc.ann.core;

import java.util.Random;

public class Utils {

	private static final Random RANDOM = new Random();

	public static double randFloat(double min, double max) {
		return RANDOM.nextDouble() * (max - min) + min;
	}

}
