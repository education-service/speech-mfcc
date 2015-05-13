/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ioe.tprsa.classify.speech;

import java.io.Serializable;

import org.ioe.tprsa.classify.speech.vq.Centroid;
import org.ioe.tprsa.db.Model;

/**
 * 
 * @author Ganesh Tiwari
 * 
 */
public class CodeBookDictionary implements Serializable, Model {

	private static final long serialVersionUID = 2094442679375932181L;
	protected int dimension;
	protected Centroid[] cent;

	public CodeBookDictionary() {
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public Centroid[] getCent() {
		return cent;
	}

	public void setCent(Centroid[] cent) {
		this.cent = cent;
	}
}
