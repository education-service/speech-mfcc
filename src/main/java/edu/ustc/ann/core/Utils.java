package edu.ustc.ann.core;

import java.util.Random;

public class Utils {

	private static final Random RANDOM = new Random();

	public static float randFloat(float min, float max) {
		return RANDOM.nextFloat() * (max - min) + min;
	}

}
