/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ioe.tprsa.classify.speech;

import java.io.Serializable;

import org.ioe.tprsa.db.Model;

/**
 * 
 * @author Ganesh Tiwari
 * 
 */
public class HMMModel implements Serializable, Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8699362751441096092L;
	protected int num_states;
	protected int num_symbols;
	protected int num_obSeq;
	protected double transition[][];
	protected double output[][];
	protected double pi[];

	public HMMModel() {
	}

	public int getNum_states() {
		return num_states;
	}

	public void setNum_states(int numStates) {
		num_states = numStates;
	}

	public int getNum_symbols() {
		return num_symbols;
	}

	public void setNum_symbols(int numSymbols) {
		num_symbols = numSymbols;
	}

	public int getNum_obSeq() {
		return num_obSeq;
	}

	public void setNum_obSeq(int numObSeq) {
		num_obSeq = numObSeq;
	}

	public double[][] getTransition() {
		return transition;
	}

	public void setTransition(double[][] transition) {
		this.transition = transition;
	}

	public double[][] getOutput() {
		return output;
	}

	public void setOutput(double[][] output) {
		this.output = output;
	}

	public double[] getPi() {
		return pi;
	}

	public void setPi(double[] pi) {
		this.pi = pi;
	}

}
