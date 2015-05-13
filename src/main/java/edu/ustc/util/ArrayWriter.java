/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package edu.ustc.util;

import java.io.FileNotFoundException;
import java.util.Formatter;

/**
 * saves the array to file or console ...... supports various data types
 * 
 * @author Ganesh Tiwari
 */
public class ArrayWriter {
	private static Formatter formatter;

	public ArrayWriter() {
	}

	/**
	 * saves the @param array to file : @param fileName
	 * 
	 * @param array
	 *            input array
	 * @param fileName
	 *            output file
	 */
	public static void printIntArrayToFile(int[] array, String fileName) {// ( T
																			// []
																			// array...
		// get formatter object with @param fileName
		try {
			formatter = new Formatter(fileName);
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found Exception :" + fileName);
			return;
		}
		// write the array
		// formatter.format("****** Array's Length %d ******", array.length);
		for (int i = 0; i < array.length; i++) {
			formatter.format("%d\n", array[i]);
		}
		formatter.flush();
		formatter.close();
	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void printIntArrayToConole(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}

	/**
	 * saves the @param array to file : @param fileName
	 * 
	 * @param array
	 *            input array
	 * @param fileName
	 *            output file
	 */
	public static void printDoubleArrayToFile(double[] array, String fileName) {
		// get formatter object with @param fileName
		try {
			formatter = new Formatter(fileName);
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found Exception :" + fileName);
			return;
		}
		// write the array
		// formatter.format("****** Array's Length %d ******", array.length);
		for (int i = 0; i < array.length; i++) {
			formatter.format("%f\n", array[i]);
		}
		formatter.flush();
		formatter.close();
	}

	/**
	 * saves the @param array to file : @param fileName
	 * 
	 * @param array
	 *            input array
	 * @param fileName
	 *            output file
	 */
	public static void print2DDoubleArrayToFile(double[][] array, String fileName) {
		// get formatter object with @param fileName
		try {
			formatter = new Formatter(fileName);
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found Exception :" + fileName);
			return;
		}
		// write the array
		// formatter.format("****** Array's Length %d ******", array.length);
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				formatter.format("%f\n", array[i][j]);
			}
		}

		formatter.flush();
		formatter.close();
	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void printDoubleArrayToConole(double[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void print2DTabbedDoubleArrayToConole(double[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j] + "\t");
			}
			System.out.println();
		}

	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void printFrameWise2DDoubleArrayToConole(double[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.println(array[j][i]);
			}
			System.out.println();
		}

	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void print2DDoubleArrayToConole(double[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.println(array[i][j]);
			}
			System.out.println();
		}

	}

	/**
	 * saves the @param array to file : @param fileName
	 * 
	 * @param array
	 *            input array
	 * @param fileName
	 *            output file
	 */
	public static void printStringArrayToFile(String[] array, String fileName) {
		// get formatter object with @param fileName
		try {
			formatter = new Formatter(fileName);
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found Exception :" + fileName);
			return;
		}
		// write the array
		// formatter.format("****** Array's Length %d ******", array.length);
		for (int i = 0; i < array.length; i++) {
			formatter.format("%s\n", array[i]);
		}
		formatter.flush();
		formatter.close();
	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void printStringArrayToConole(String[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}

	/**
	 * saves the @param array to file : @param fileName
	 * 
	 * @param array
	 *            input array
	 * @param fileName
	 *            output file
	 */
	public static void printFloatArrayToFile(float[] array, String fileName) {
		// get formatter object with @param fileName
		try {
			formatter = new Formatter(fileName);
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found Exception :" + fileName);
			return;
		}
		// write the array
		// formatter.format("****** Array's Length %d ******", array.length);
		for (int i = 0; i < array.length; i++) {
			formatter.format("%f\n", array[i]);
		}
		formatter.flush();
		formatter.close();
	}

	/**
	 * display @param array 's content to console
	 * 
	 * @param array
	 *            input array
	 */
	public static void printFloatArrayToConole(float[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}
}
