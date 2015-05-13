package edu.ustc.classify.speech.vq;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <b>简述:</b> Centroid of a codebook
 * <b>调用的类:</b> Points<br>
 * <b>调用的地方:</b> codebook<br>
 * <b>输入:</b> k维点<br>
 * <b>输出:</b> 失真度
 *
 * @author wanggang
 *
 */
public class Centroid extends Points implements Serializable {

	private static final long serialVersionUID = -3338773995177928063L;

	/**
	 * distortion measure - sum of all points' distances from the Centroid
	 */
	protected double distortion = 0;

	/**
	 * stores the points that belong to this Centroid or cell
	 */
	protected Vector<Points> pts = new Vector<Points>(0);

	/**
	 * total number of points that belong to this Centroid or cell
	 */
	protected int total_pts;

	/**
	 * constructor to create a Centroid from input coordinates<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @param Co
	 *            coordinates array
	 */
	public Centroid(double Co[]) {
		super(Co);
		total_pts = 0;
	}

	/**
	 * get a Points at specified index<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @param index
	 *            index number
	 * @return the Points at the specified index
	 */
	public Points getPoint(int index) {
		return pts.get(index);
	}

	/**
	 * returns the number of points in this cell<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @return number of points
	 */
	public int getNumPts() {
		return total_pts;
	}

	/**
	 * removes a given Points from the Centroid's cell<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @param pt
	 *            the Points to be removed
	 * @param dist
	 *            distance from the Centroid
	 */
	public void remove(Points pt, double dist) {
		Points tmpPoint = pts.get(0);
		int i = -1;

		Enumeration<Points> enums = pts.elements();
		boolean found = false;
		while (enums.hasMoreElements() && !found) {
			tmpPoint = enums.nextElement();
			i++;

			// find the identical Points in pts vector
			if (Points.equals(pt, tmpPoint)) {
				found = true;
			}
		}

		if (found) {
			// remove Points from pts vector
			pts.remove(i);
			// update distortion measure
			distortion -= dist;
			// update number of points
			total_pts--;
		} else {
			System.out.println("err: point not found");
		}
	}

	/**
	 * add Points to Centroid's cell<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @param pt
	 *            a Points belonging to the Centroid
	 * @param dist
	 *            distance from the Centroid
	 */
	public void add(Points pt, double dist) {
		// update number of points
		total_pts++;
		// add Points to pts vector
		pts.add(pt);
		// update distortion measure
		distortion += dist;
	}

	/**
	 * update Centroid by taking average of all points in the cell<br>
	 * calls: none<br>
	 * called by: codebook
	 */
	public void update() {
		double sum_coordinates[] = new double[dimension];
		Points tmpPoint;
		Enumeration<Points> enums = pts.elements();

		while (enums.hasMoreElements()) {
			tmpPoint = enums.nextElement();

			// calculate the sum of all coordinates
			for (int k = 0; k < dimension; k++) {
				sum_coordinates[k] += tmpPoint.getCo(k);
			}
		}

		// divide sum of coordinates by total number points to get average
		for (int k = 0; k < dimension; k++) {
			setCo(k, sum_coordinates[k] / total_pts);
			pts = new Vector<Points>(0);
		}

		// reset number of points
		total_pts = 0;
		// reset distortion measure
		distortion = 0;
	}

	/**
	 * returns the distortion measure of the current cell<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @return distortion of current cell
	 */
	public double getDistortion() {
		return distortion;
	}

}