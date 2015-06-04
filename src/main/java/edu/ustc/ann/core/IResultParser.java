package edu.ustc.ann.core;

/**
 * 结果解析接口
 */
public interface IResultParser<T> {

	/**
	 * 计算正确的数量
	 */
	int countSuccesses(int success, float fOut, float t);

	/**
	 * 对输出函数获得的值转换为真实结果
	 */
	T parseResult(float result);

}
