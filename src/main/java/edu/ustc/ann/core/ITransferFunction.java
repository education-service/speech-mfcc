package edu.ustc.ann.core;

/**
 * 传输函数接口，该函数的功能是限制生成输出的值的范围
 */
public interface ITransferFunction {

	double transfer(double value);

}
