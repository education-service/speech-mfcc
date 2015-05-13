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

package edu.ustc.classify.speech.vq;

import edu.ustc.classify.speech.CodeBookDictionary;
import edu.ustc.db.DataBase;
import edu.ustc.db.ObjectIODataBase;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> Codebook for Vector Quantization component<br>
 * <b>calls:</b> Centroid, Points<br>
 * <b>called by:</b> volume, train<br>
 * <b>input:</b> speech signal<br>
 * <b>output:</b> set of centroids, set of indices
 * 
 * @author Danny Su
 * @author Andrei Leonov
 * 
 * @modified-by Ganesh Tiwari : DB Operations last updated on Dec-27,2010
 */
public class Codebook {
	/**
	 * split factor (should be in the range of 0.01 <= SPLIT <= 0.05)
	 */
	protected final double SPLIT = 0.01;
	/**
	 * minimum distortion
	 */
	protected final double MIN_DISTORTION = 0.1;
	/**
	 * Codebook size - number of codewords (codevectors)<br>
	 * default is: 256
	 */
	protected int codebook_size = 256;
	/**
	 * centroids array
	 */
	protected Centroid centroids[];
	/**
	 * training points
	 */
	protected Points pt[];
	/**
	 * dimension /////no of features
	 */
	protected int dimension;

	/**
	 * constructor to train a Codebook with given training points and Codebook
	 * size<br>
	 * calls: none<br>
	 * called by: trainCodebook
	 * 
	 * @param tmpPt
	 *            training vectors
	 * @param size
	 *            Codebook size
	 */
	public Codebook(Points tmpPt[], int size) {
		// update variables
		this.pt = tmpPt;
		this.codebook_size = size;

		// make sure there are enough training points to train the Codebook
		if (pt.length >= codebook_size) {
			dimension = pt[0].getDimension();
			initialize();
		}
		else {
			System.out.println("err: not enough training points");
		}
	}

	/**
	 * constructor to train a Codebook with given training points and default
	 * Codebook size (256)<br>
	 * calls: none<br>
	 * called by: trainCodebook
	 * 
	 * @param tmpPt
	 *            training vectors
	 */
	public Codebook(Points tmpPt[]) {
		this.pt = tmpPt;

		// make sure there are enough training points to train the Codebook
		if (pt.length >= codebook_size) {
			dimension = pt[0].getDimension();
			initialize();
		}
		else {
			System.out.println("err: not enough training points");
		}
	}

	/**
	 * constructor to load a saved Codebook from external file<br>
	 * calls: Centroid<br>
	 * called by: volume
	 */
	public Codebook() {
		DataBase db = new ObjectIODataBase();
		db.setType("cbk");
		CodeBookDictionary cbd = new CodeBookDictionary();
		cbd = (CodeBookDictionary) db.readModel(null);
		dimension = cbd.getDimension();
		centroids = cbd.getCent();
		// System.out.println("Showing parameters");
		// showParameters();
	}

	private void showParameters() {
		for (int c = 0; c < centroids.length; c++) {
			// bw.write("c" + c + ": (");
			for (int k = 0; k < dimension; k++) {
				System.out.print(centroids[c].getCo(k) + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * creates a Codebook using LBG algorithm which includes K-means<br>
	 * calls: Centroid<br>
	 * called by: Codebook
	 */
	protected void initialize() {
		double distortion_before_update = 0; // distortion measure before
												// updating centroids
		double distortion_after_update = 0; // distortion measure after update
											// centroids

		// design a 1-vector Codebook
		centroids = new Centroid[1];

		// then initialize it with (0, 0) coordinates
		double origin[] = new double[dimension];
		centroids[0] = new Centroid(origin);

		// initially, all training points will belong to 1 single cell
		for (int i = 0; i < pt.length; i++) {
			centroids[0].add(pt[i], 0);
		}

		// calls update to set the initial codevector as the average of all
		// points
		centroids[0].update();

		// Iteration 1: repeat splitting step and K-means until required number
		// of codewords is reached
		while (centroids.length < codebook_size) {
			// split codevectors by a binary splitting method
			split();

			// group training points to centroids closest to them
			groupPtoC();

			// Iteration 2: perform K-means algorithm
			do {
				for (int i = 0; i < centroids.length; i++) {
					distortion_before_update += centroids[i].getDistortion();
					centroids[i].update();
				}

				// regroup
				groupPtoC();

				for (int i = 0; i < centroids.length; i++) {
					distortion_after_update += centroids[i].getDistortion();
				}

			} while (Math.abs(distortion_after_update - distortion_before_update) < MIN_DISTORTION);
		}
	}

	/**
	 * save Codebook to cbk object file<br>
	 * calls: none<br>
	 * called by: train
	 */
	public void saveToFile() {
		DataBase db = new ObjectIODataBase();
		db.setType("cbk");
		CodeBookDictionary cbd = new CodeBookDictionary();
		// no need to save all the points,
		// must be removed in objectIO, to reduce the size of file
		for (int i = 0; i < centroids.length; i++) {
			centroids[i].pts.removeAllElements();
		}
		cbd.setDimension(dimension);
		cbd.setCent(centroids);
		db.saveModel(cbd, null);// filepath is not used
		// System.out.println("Showing parameters");
		// showParameters();

	}

	/**
	 * splitting algorithm to increase number of centroids by multiple of 2<br>
	 * calls: Centroid<br>
	 * called by: Codebook
	 */
	protected void split() {
		System.out.println("Centroids length now becomes " + centroids.length + 2);
		Centroid temp[] = new Centroid[centroids.length * 2];
		double tCo[][];
		for (int i = 0; i < temp.length; i += 2) {
			tCo = new double[2][dimension];
			for (int j = 0; j < dimension; j++) {
				tCo[0][j] = centroids[i / 2].getCo(j) * (1 + SPLIT);
			}
			temp[i] = new Centroid(tCo[0]);
			for (int j = 0; j < dimension; j++) {
				tCo[1][j] = centroids[i / 2].getCo(j) * (1 - SPLIT);
			}
			temp[i + 1] = new Centroid(tCo[1]);
		}

		// replace old centroids array with new one
		centroids = new Centroid[temp.length];
		centroids = temp;
	}

	/**
	 * quantize the input array of points in k-dimensional space<br>
	 * calls: none<br>
	 * called by: volume
	 * 
	 * @param pts
	 *            points to be quantized
	 * @return quantized index array
	 */
	public int[] quantize(Points pts[]) {
		int output[] = new int[pts.length];
		for (int i = 0; i < pts.length; i++) {
			output[i] = closestCentroidToPoint(pts[i]);
		}
		return output;
	}

	/**
	 * calculates the distortion<br>
	 * calls: none<br>
	 * called by: volume
	 * 
	 * @param pts
	 *            points to calculate the distortion with
	 * @return distortion measure
	 */
	public double getDistortion(Points pts[]) {
		double dist = 0;
		for (int i = 0; i < pts.length; i++) {
			int index = closestCentroidToPoint(pts[i]);
			double d = getDistance(pts[i], centroids[index]);
			dist += d;
		}
		return dist;
	}

	/**
	 * finds the closest Centroid to a specific Points<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param pt
	 *            Points
	 * @return index number of the closest Centroid
	 */
	private int closestCentroidToPoint(Points pt) {
		double tmp_dist = 0;
		double lowest_dist = 0; // = getDistance(pt, centroids[0]);
		int lowest_index = 0;

		for (int i = 0; i < centroids.length; i++) {
			tmp_dist = getDistance(pt, centroids[i]);
			if (tmp_dist < lowest_dist || i == 0) {
				lowest_dist = tmp_dist;
				lowest_index = i;
			}
		}
		return lowest_index;
	}

	/**
	 * finds the closest Centroid to a specific Centroid<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param pt
	 *            Points
	 * @return index number of the closest Centroid
	 */
	private int closestCentroidToCentroid(Centroid c) {
		double tmp_dist = 0;
		double lowest_dist = Double.MAX_VALUE;
		int lowest_index = 0;
		for (int i = 0; i < centroids.length; i++) {
			tmp_dist = getDistance(c, centroids[i]);
			if (tmp_dist < lowest_dist && centroids[i].getNumPts() > 1) {
				lowest_dist = tmp_dist;
				lowest_index = i;
			}
		}
		return lowest_index;
	}

	/**
	 * finds the closest Points in c2's cell to c1<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param c1
	 *            first Centroid
	 * @param c2
	 *            second Centroid
	 * @return index of Points
	 */
	private int closestPoint(Centroid c1, Centroid c2) {
		double tmp_dist = 0;
		double lowest_dist = getDistance(c2.getPoint(0), c1);
		int lowest_index = 0;
		for (int i = 1; i < c2.getNumPts(); i++) {
			tmp_dist = getDistance(c2.getPoint(i), c1);
			if (tmp_dist < lowest_dist) {
				lowest_dist = tmp_dist;
				lowest_index = i;
			}
		}
		return lowest_index;
	}

	/**
	 * grouping points to cells<br>
	 * calls: none<br>
	 * called by: Codebook
	 */
	private void groupPtoC() {
		// find closest Centroid and assign Points to it
		for (int i = 0; i < pt.length; i++) {
			int index = closestCentroidToPoint(pt[i]);
			centroids[index].add(pt[i], getDistance(pt[i], centroids[index]));
		}
		// make sure that all centroids have at least one Points assigned to it
		// no cell should be empty or else NaN error will occur due to division
		// of 0 by 0
		for (int i = 0; i < centroids.length; i++) {
			if (centroids[i].getNumPts() == 0) {
				// find the closest Centroid with more than one points assigned
				// to it
				int index = closestCentroidToCentroid(centroids[i]);
				// find the closest Points in the closest Centroid's cell
				int closestIndex = closestPoint(centroids[i], centroids[index]);
				Points closestPt = centroids[index].getPoint(closestIndex);
				centroids[index].remove(closestPt, getDistance(closestPt, centroids[index]));
				centroids[i].add(closestPt, getDistance(closestPt, centroids[i]));
			}
		}
	}

	/**
	 * calculates the distance of a Points to a Centroid<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param tPt
	 *            points
	 * @param tC
	 *            Centroid
	 */
	private double getDistance(Points tPt, Centroid tC) {
		double distance = 0;
		double temp = 0;
		for (int i = 0; i < dimension; i++) {
			temp = tPt.getCo(i) - tC.getCo(i);
			distance += temp * temp;
		}
		distance = Math.sqrt(distance);
		return distance;
	}
}