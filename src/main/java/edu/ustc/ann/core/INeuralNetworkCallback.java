package edu.ustc.ann.core;

/**
 * 回调神经网络
 */
public interface INeuralNetworkCallback {

	/**
	 * 当神经网络完成任务并且结果满足时调用
	 */
	void success(Result result);

	/**
	 * 当神经网络完成任务并且结果不满足时调用
	 */
	void failure(Error error);

}
