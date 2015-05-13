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

import java.io.Serializable;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> Points class which stores coordinates in k-dimensional
 * space<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> centroid, codebook<br>
 * <b>input:</b> set of co-ordinates<br>
 * <b>output:</b> none
 * 
 * @author Danny Su
 * @author Andrei Leonov
 */
public class Points implements Serializable {
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
		}
		else {
			equal = false;
		}

		return equal;
	}
}