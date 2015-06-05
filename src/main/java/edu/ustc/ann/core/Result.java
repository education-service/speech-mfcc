package edu.ustc.ann.core;

/**
 * 结果集合
 */
@SuppressWarnings("rawtypes")
public class Result {

	private double successPercentage;
	private double quadraticError;
	private Analyzer analyzer;
	private IResultParser resultParser;

	public Result(Analyzer analyzer, IResultParser resultParser, double successPercentage, double quadraticError) {
		this.analyzer = analyzer;
		this.successPercentage = successPercentage;
		this.quadraticError = quadraticError;
		this.resultParser = resultParser;
	}

	public int predictValue(double[] element) {
		return (Integer) resultParser.parseResult(analyzer.getFOut(element));
	}

	public double getSuccessPercentage() {
		return successPercentage;
	}

	public double getQuadraticError() {
		return quadraticError;
	}

}
