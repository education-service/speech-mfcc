package edu.ustc.knn.core;

/**
 * 动态数组排序：对一组不断加进来的数据进行插入并排序，这类数据形如：uid=50
 *
 * @author wgybzb
 *
 */
public class InsertSort {

	/**
	 * 键值插入
	 */
	public static String[] toptable(String[] table, String str) {

		String[] result = new String[table.length]; //从大到小排序
		double key = Double.parseDouble(str.split("=")[1]);
		double lastvalue = Double.parseDouble(table[table.length - 1].split("=")[1]);
		double firstvalue = Double.parseDouble(table[0].split("=")[1]);
		int index = 0;
		if (key >= firstvalue) {
			result[0] = str;
			for (int j = 1; j < table.length; j++) {
				result[j] = table[j - 1];
			}
		} else if (key < lastvalue) {
			result = table;
		} else {
			index = InsertSort.indexSearch(table, key);
			for (int i = 0; i < index; i++) {
				result[i] = table[i];
			}
			result[index] = str;
			if (index < table.length - 1) {
				for (int j = index + 1; j < table.length; j++) {
					result[j] = table[j - 1];
				}
			}
		}

		return result;
	}

	/**
	 * 键值查找，二分法查找
	 */
	private static int indexSearch(String[] table, double key) {

		//table从大到小排序,key插在index位置
		int index = 0;
		int low = 0;
		int high = table.length;
		int media = 0;
		while (low <= high) {
			media = (low + high) / 2;
			double value = Double.parseDouble(table[media].split("=")[1]);
			if (key > value) {
				high = media;
				index = high;
				if (high - low == 1)
					break;
			} else if (key < value) {
				low = media;
				index = low + 1;
				if (high - low == 1)
					break;
			} else {
				index = media;
				break;
			}
		}

		return index;
	}

}