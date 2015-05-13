/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package edu.ustc.util;

import javax.swing.JLabel;

public class ErrorManager {

	private static ErrorManager em = new ErrorManager();
	private static JLabel mlbl;

	private ErrorManager() {
	}

	public static ErrorManager getInstance() {
		return em;
	}

	public static void setMessageLbl(JLabel ilbl) {
		if (mlbl != null) {
			mlbl = ilbl;
		}
	}

	public static void reportStatus(String msg, MessageType mt) {
		mlbl.setText(msg);
	}
}
