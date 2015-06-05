package edu.ustc.ann.core;

/**
 * 二值结果解析
 */
public class BinaryResultParser implements IResultParser<Integer> {

	@Override
	public int countSuccesses(int success, double fOut, double t) {
		if ((fOut < 0.5 && t == 0) || (fOut >= 0.5 && t == 1))
			success += 1;

		return success;
	}

	@Override
	public Integer parseResult(double result) {
		return (result < 0.5) ? 0 : 1;
	}

}
