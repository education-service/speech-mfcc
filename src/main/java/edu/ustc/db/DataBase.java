/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package edu.ustc.db;

/**
 * 
 * @author Ganesh Tiwari
 * 
 */
public interface DataBase {
	public void setType(String type);

	public String[] readRegistered();

	public Model readModel(String name);

	public void saveModel(Model m, String name);
}
