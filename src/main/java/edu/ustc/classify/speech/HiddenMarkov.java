/*
OC Volume - Java Speech Recognition Engine
Copyright (c) 2002-2004, OrangeCow organization
All rights reserved.

Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

 * Redistributions of source code must retain the
  above copyright notice, this list of conditions
  and the following disclaimer.
 * Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions
  and the following disclaimer in the documentation
  and/or other materials provided with the
  distribution.
 * Neither the name of the OrangeCow organization
  nor the names of its contributors may be used to
  endorse or promote products derived from this
  software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contact information:
Please visit http://ocvolume.sourceforge.net.
 */

package edu.ustc.classify.speech;

import edu.ustc.db.DataBase;
import edu.ustc.db.ObjectIODataBase;
import edu.ustc.util.ArrayWriter;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> this class represents a left-to-right Hidden Markov Model
 * and its essential methods for speech recognition. The collection of methods
 * include Forward-Backward Algorithm, Baum-Welch Algorithm, Scaling, Viterbi,
 * etc.<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> volume, train<br>
 * <b>input:</b> sequence of integers<br>
 * <b>output:</b> probability
 * 
 * @author Danny Su
 * 
 * @modified-by Ganesh Tiwari : DB Operations, Initialization of parameters
 *              Corrected last updated on Dec-27,2010
 */
public class HiddenMarkov {
	/**
	 * minimum probability
	 */
	// final double MIN_PROBABILITY = 0.00000000001;
	final double MIN_PROBABILITY = 0.0001;
	/**
	 * length of observation sequence
	 */
	protected int len_obSeq;
	/**
	 * number of state in the model example: number of urns
	 */
	protected int num_states;
	/**
	 * number of observation symbols per state example: how many different
	 * colour balls there are
	 */
	protected int num_symbols;
	/**
	 * number of states the model is allowed to jump
	 */
	protected final int delta = 2;
	/**
	 * discrete set of observation symbols example: sequence of colour of balls
	 */
	protected int obSeq[][];
	/**
	 * current observation sequence
	 */
	protected int currentSeq[];
	/**
	 * number of observation sequence
	 */
	protected int num_obSeq;
	/**
	 * state transition probability example: probability from one state to
	 * another state
	 */
	protected double transition[][];
	/**
	 * discrete output probability example: probability of a specific output
	 * from a state
	 */
	protected double output[][];
	/**
	 * initial state distribution example: which state is the starting state
	 */
	protected double pi[];
	/**
	 * forward variable alpha
	 */
	protected double alpha[][];
	/**
	 * backward variable beta
	 */
	protected double beta[][];
	/**
	 * Scale Coefficient
	 */
	protected double scaleFactor[];
	/**
	 * variable for viterbi algorithm
	 */
	private int psi[][];
	/**
	 * best state sequence
	 */
	public int q[];

	/**
	 * viterbi algorithm used to get best state sequence and probability<br>
	 * calls: none<br>
	 * called by: volume
	 * 
	 * @param testSeq
	 *            test sequence
	 * @return probability
	 */
	public double viterbi(int testSeq[]) {
		setObSeq(testSeq);
		double phi[][] = new double[len_obSeq][num_states];
		psi = new int[len_obSeq][num_states];
		q = new int[len_obSeq];

		for (int i = 0; i < num_states; i++) {
			double temp = pi[i];
			if (temp == 0) {
				temp = MIN_PROBABILITY;
			}

			phi[0][i] = Math.log(temp) + Math.log(output[i][currentSeq[0]]);
			psi[0][i] = 0;
		}

		for (int t = 1; t < len_obSeq; t++) {
			for (int j = 0; j < num_states; j++) {
				double max = phi[t - 1][0] + Math.log(transition[0][j]);
				double temp = 0;
				int index = 0;

				for (int i = 1; i < num_states; i++) {

					temp = phi[t - 1][i] + Math.log(transition[i][j]);
					if (temp > max) {
						max = temp;
						index = i;
					}

				}

				phi[t][j] = max + Math.log(output[j][currentSeq[t]]);
				psi[t][j] = index;
			}
		}

		double max = phi[len_obSeq - 1][0];
		double temp = 0;
		int index = 0;
		for (int i = 1; i < num_states; i++) {
			temp = phi[len_obSeq - 1][i];

			if (temp > max) {
				max = temp;
				index = i;
			}
		}

		q[len_obSeq - 1] = index;

		for (int t = len_obSeq - 2; t >= 0; t--) {
			q[t] = psi[t + 1][q[t + 1]];
		}

		return max;
	}

	/**
	 * rescales backward variable beta to prevent underflow<br>
	 * calls: none<br>
	 * called by: HiddenMarkov
	 * 
	 * @param t
	 *            index number of backward variable beta
	 */
	private void rescaleBeta(int t) {
		for (int i = 0; i < num_states; i++) {
			beta[t][i] *= scaleFactor[t];
		}
	}

	/**
	 * rescales forward variable alpha to prevent underflow<br>
	 * calls: none<br>
	 * called by: HiddenMarkov
	 * 
	 * @param t
	 *            index number of forward variable alpha
	 */
	private void rescaleAlpha(int t) {
		// calculate scale coefficients
		for (int i = 0; i < num_states; i++) {
			scaleFactor[t] += alpha[t][i];
		}

		scaleFactor[t] = 1 / scaleFactor[t];

		// apply scale coefficients
		for (int i = 0; i < num_states; i++) {
			alpha[t][i] *= scaleFactor[t];
		}
	}

	/**
	 * returns the probability calculated from the testing sequence<br>
	 * calls: none<br>
	 * called by: volume
	 * 
	 * @param testSeq
	 *            testing sequence
	 * @return probability of observation sequence given the model
	 */
	public double getProbability(int testSeq[]) {
		setObSeq(testSeq);
		double temp = computeAlpha();

		return temp;
	}

	/**
	 * calculate forward variable alpha<br>
	 * calls: none<br>
	 * called by: HiddenMarkov
	 * 
	 * @return probability
	 */
	protected double computeAlpha() {
		/**
		 * Pr(obSeq | model); Probability of the observation sequence given the
		 * hmm model
		 */
		double probability = 0;

		// reset scaleFactor[]
		for (int t = 0; t < len_obSeq; t++) {
			scaleFactor[t] = 0;
		}

		/**
		 * Initialization: Calculating all alpha variables at time = 0
		 */
		for (int i = 0; i < num_states; i++) {
			// System.out.println("current  "+i+" crr  "+currentSeq[0]);

			alpha[0][i] = pi[i] * output[i][currentSeq[0]];
		}
		rescaleAlpha(0);

		/**
		 * Induction:
		 */
		for (int t = 0; t < len_obSeq - 1; t++) {
			for (int j = 0; j < num_states; j++) {

				/**
				 * Sum of all alpha[t][i] * transition[i][j]
				 */
				double sum = 0;

				/**
				 * Calculate sum of all alpha[t][i] * transition[i][j], 0 <= i <
				 * num_states
				 */
				for (int i = 0; i < num_states; i++) {
					sum += alpha[t][i] * transition[i][j];
				}

				alpha[t + 1][j] = sum * output[j][currentSeq[t + 1]];
			}
			rescaleAlpha(t + 1);
		}

		/**
		 * Termination: Calculate Pr(obSeq | model)
		 */
		for (int i = 0; i < num_states; i++) {
			probability += alpha[len_obSeq - 1][i];
		}

		probability = 0;
		// double totalScaleFactor = 1;
		for (int t = 0; t < len_obSeq; t++) {
			// System.out.println("s: " + Math.log(scaleFactor[t]));

			probability += Math.log(scaleFactor[t]);

			// totalScaleFactor *= scaleFactor[t];
		}

		return -probability;
		// return porbability / totalScaleFactor;
	}

	/**
	 * calculate backward variable beta for later use with Re-Estimation method<br>
	 * calls: none<br>
	 * called by: HiddenMarkov
	 */
	protected void computeBeta() {
		/**
		 * Initialization: Set all beta variables to 1 at time = len_obSeq - 1
		 */
		for (int i = 0; i < num_states; i++) {
			beta[len_obSeq - 1][i] = 1;
		}
		rescaleBeta(len_obSeq - 1);

		/**
		 * Induction:
		 */
		for (int t = len_obSeq - 2; t >= 0; t--) {
			for (int i = 0; i < num_states; i++) {
				for (int j = 0; j < num_states; j++) {
					beta[t][i] += transition[i][j] * output[j][currentSeq[t + 1]] * beta[t + 1][j];
				}
			}
			rescaleBeta(t);
		}
	}

	/**
	 * set the number of training sequences<br>
	 * calls: none<br>
	 * called by: trainHMM
	 * 
	 * @param k
	 *            number of training sequences
	 */
	public void setNumObSeq(int k) {
		num_obSeq = k;
		obSeq = new int[k][];
	}

	/**
	 * set a training sequence for re-estimation step<br>
	 * calls: none<br>
	 * called by: trainHMM
	 * 
	 * @param k
	 *            index representing kth training sequence
	 * @param trainSeq
	 *            training sequence
	 */
	public void setTrainSeq(int k, int trainSeq[]) {
		obSeq[k] = trainSeq;
	}

	/**
	 * set training sequences for re-estimation step<br>
	 * calls: none<br>
	 * called by: trainHMM
	 * 
	 * @param trainSeq
	 *            training sequences
	 */
	public void setTrainSeq(int trainSeq[][]) {
		num_obSeq = trainSeq.length;
		obSeq = new int[num_obSeq][];// /ADDED
		// System.out.println("num obSeq << setTrainSeq()    "+num_obSeq);
		for (int k = 0; k < num_obSeq; k++) {
			obSeq[k] = trainSeq[k];
		}
	}

	/**
	 * train the hmm model until no more improvement<br>
	 * calls: none<br>
	 * called by: trainHMM
	 */
	public void train() {
		// re-estimate 25 times
		// NOTE: should be changed to re-estimate until no more improvement
		for (int i = 0; i < 20; i++) {
			reestimate();
			System.out.println("reestimating.....");
		}
		//
		// oldm=
	}

	/**
	 * Baum-Welch Algorithm - Re-estimate (iterative udpate and improvement) of
	 * HMM parameters<br>
	 * calls: none<br>
	 * called by: trainHMM
	 */
	private void reestimate() {
		// new probabilities that will be the optimized and replace the older
		// version
		double newTransition[][] = new double[num_states][num_states];
		double newOutput[][] = new double[num_states][num_symbols];
		double numerator[] = new double[num_obSeq];
		double denominator[] = new double[num_obSeq];

		// calculate new transition probability matrix
		double sumP = 0;

		for (int i = 0; i < num_states; i++) {
			for (int j = 0; j < num_states; j++) {

				if (j < i || j > i + delta) {
					newTransition[i][j] = 0;
				}
				else {
					for (int k = 0; k < num_obSeq; k++) {
						numerator[k] = denominator[k] = 0;
						setObSeq(obSeq[k]);

						sumP += computeAlpha();
						computeBeta();
						for (int t = 0; t < len_obSeq - 1; t++) {
							numerator[k] += alpha[t][i] * transition[i][j] * output[j][currentSeq[t + 1]] * beta[t + 1][j];
							denominator[k] += alpha[t][i] * beta[t][i];
						}
					}
					double denom = 0;
					for (int k = 0; k < num_obSeq; k++) {
						newTransition[i][j] += (1 / sumP) * numerator[k];
						denom += (1 / sumP) * denominator[k];
					}
					newTransition[i][j] /= denom;
					newTransition[i][j] += MIN_PROBABILITY;
				}
			}
		}

		// calculate new output probability matrix
		sumP = 0;
		for (int i = 0; i < num_states; i++) {
			for (int j = 0; j < num_symbols; j++) {
				for (int k = 0; k < num_obSeq; k++) {
					numerator[k] = denominator[k] = 0;
					setObSeq(obSeq[k]);

					sumP += computeAlpha();
					computeBeta();

					for (int t = 0; t < len_obSeq - 1; t++) {
						if (currentSeq[t] == j) {
							numerator[k] += alpha[t][i] * beta[t][i];
						}
						denominator[k] += alpha[t][i] * beta[t][i];
					}
				}

				double denom = 0;
				for (int k = 0; k < num_obSeq; k++) {
					newOutput[i][j] += (1 / sumP) * numerator[k];
					denom += (1 / sumP) * denominator[k];
				}

				newOutput[i][j] /= denom;
				newOutput[i][j] += MIN_PROBABILITY;
			}
		}

		// replace old matrices after re-estimate
		transition = newTransition;
		output = newOutput;
	}

	/**
	 * set observation sequence<br>
	 * calls: none<br>
	 * called by: trainHMM
	 * 
	 * @param observationSeq
	 *            observation sequence
	 */
	public void setObSeq(int observationSeq[]) {
		currentSeq = observationSeq;
		len_obSeq = observationSeq.length;
		// System.out.println("len_obSeq<<setObSeq()   "+len_obSeq);

		alpha = new double[len_obSeq][num_states];
		beta = new double[len_obSeq][num_states];
		scaleFactor = new double[len_obSeq];
	}

	/**
	 * class constructor - used to create a model from a saved file<br>
	 * calls: none<br>
	 * called by: volume, trainHMM
	 * 
	 * @param word
	 *            path of the file to load
	 */
	public HiddenMarkov(String word) {
		DataBase db = new ObjectIODataBase();
		db.setType("hmm");
		HMMModel model = new HMMModel();
		model = (HMMModel) db.readModel(word);// System.out.println(model.getClass());
		num_obSeq = model.getNum_obSeq();
		output = model.getOutput();// ArrayWriter.print2DTabbedDoubleArrayToConole(output);
		transition = model.getTransition();
		pi = model.getPi();
		num_states = output.length;
		num_symbols = output[0].length;
		// System.out.println("num states :"+num_states+"num symbols :"+num_symbols);
	}

	/**
	 * class constructor - used to create a left-to-right model with multiple
	 * observation sequences for training<br>
	 * calls: none<br>
	 * called by: trainHMM
	 * 
	 * @param num_states
	 *            number of states in the model
	 * @param num_symbols
	 *            number of symbols per state
	 */
	public HiddenMarkov(int num_states, int num_symbols) {
		this.num_states = num_states;
		this.num_symbols = num_symbols;
		transition = new double[num_states][num_states];
		output = new double[num_states][num_symbols];
		pi = new double[num_states];

		/**
		 * in a left-to-right HMM model, the first state is always the initial
		 * state. e.g. probability = 1
		 */
		pi[0] = 1;
		for (int i = 1; i < num_states; i++) {
			pi[i] = 0;
		}

		// generate random probability for all the other probability matrices
		randomProb();
	}

	/**
	 * generates random probabilities for transition, output probabilities<br>
	 * calls: none<br>
	 * called by: HiddenMarkov corrected by GT
	 */
	private void randomProb() {
		for (int i = 0; i < num_states; i++) {
			for (int j = 0; j < num_states; j++) {
				if (j < i || j > i + delta) {
					transition[i][j] = 0;// R-L prob=0 for L-R HMM, and with
											// Delta
				}
				else {
					double randNum = Math.random();
					transition[i][j] = randNum;
					// System.out.println("transition init: "+transition[i][j]);
				}
			}
			for (int j = 0; j < num_symbols; j++) {
				double randNum = Math.random();
				output[i][j] = randNum;
				// System.out.println("outputInit: "+output[i][j]);
			}

		}
	}

	/**
	 * save HMM model to file<br>
	 * calls: none<br>
	 * called by: trainHMM
	 * 
	 * @param filepath
	 *            file location
	 */
	public void save(String modelName) {
		DataBase db = new ObjectIODataBase();
		db.setType("hmm");
		HMMModel model = new HMMModel();
		model.setOutput(output);
		ArrayWriter.print2DTabbedDoubleArrayToConole(output);
		model.setPi(pi);
		ArrayWriter.printDoubleArrayToConole(pi);
		model.setTransition(transition);
		ArrayWriter.print2DTabbedDoubleArrayToConole(transition);
		db.saveModel(model, modelName);
	}
}