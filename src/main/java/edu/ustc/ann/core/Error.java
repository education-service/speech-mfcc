package edu.ustc.ann.core;

public enum Error {

	NOT_SAME_INPUT_OUTPUT(0, "输入输出大小不一致"), //
	ZERO_INPUT_DIMENSION(1, "输入维度为0"), //
	ZERO_INPUT_ELEMENTS(2, "输入元素个数为0"), //
	ZERO_NEURONS(3, "神经元个数为0");

	private final int code;
	private final String description;

	private Error(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

}