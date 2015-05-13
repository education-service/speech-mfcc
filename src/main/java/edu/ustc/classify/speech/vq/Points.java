package edu.ustc.classify.speech.vq;

import java.io.Serializable;

/**
 * <b>简述:</b> 该类用于存储k维空间中的坐标数据<br>
 * <b>使用的地方:</b> centroid, codebook<br>
 * <b>输入:</b> 坐标集合<br>
 * <b>输出:</b> 无
 *
 * @author wanggang
 *
 */
public class Points implements Serializable {

	private static final long serialVersionUID = -3815055105806618573L;

	/**
	 * k-dimensional coordinates array
	 */
	protected double coordinates[];

	/**
	 * k-dimensions
	 */
	protected int dimension;

	/**
	 * constructor to create a Points with k-dimensional coordinates array<br>
	 * calls: none<br>
	 * called by: centroid, codebook
	 *
	 * @param co
	 *            k-dimensional coordinates array
	 */
	public Points(double co[]) {
		dimension = co.length;
		coordinates = co;
	}

	/**
	 * get all the coordinates<br>
	 * calls: none<br>
	 * called by: codebook
	 *
	 * @return coordinates as a double array
	 */
	public double[] getAllCo() {
		return coordinates;
	}

	/**
	 * get coordinate at specific index<br>
	 * calls: none<br>
	 * called by: centroid, codebook
	 *
	 * @param i
	 *            index number
	 * @return coordinate at index i
	 */
	public double getCo(int i) {
		return coordinates[i];
	}

	/**
	 * set coordinate at specific index<br>
	 * calls: none<br>
	 * called by: centroid, codebook
	 *
	 * @param i
	 *            index number
	 * @param value
	 *            value of coordinate
	 */
	public void setCo(int i, double value) {
		coordinates[i] = value;
	}

	/**
	 * replace coordinates with new ones<br>
	 * calls: none<br>
	 * called by: centroid, codebook
	 *
	 * @param tCo
	 *            new coordinates array
	 */
	public void changeCo(double tCo[]) {
		coordinates = tCo;
	}

	/**
	 * get the k-dimensional space that the Points is in<br>
	 * calls: none<br>
	 * called by: centroid, codebook
	 *
	 * @return dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * check whether two points are identical<br>
	 * calls: none<br>
	 * called by: centroid, codebook
	 *
	 * @param p1
	 *            first Points
	 * @param p2
	 *            second Points
	 * @return true/false indicating whether two points are identical
	 */
	public static boolean equals(Points p1, Points p2) {
		boolean equal = true;
		int d = p1.getDimension();

		// dimension of two points has to be the same
		if (d == p2.getDimension()) {
			// compares all coordinates
			for (int k = 0; k < d && equal; k++) {
				// if any of the coordinates are not the same then these two
				// points are not the same
				if (p1.getCo(k) != p2.getCo(k)) {
					equal = false;
				}
			}
		} else {
			equal = false;
		}

		return equal;
	}

}