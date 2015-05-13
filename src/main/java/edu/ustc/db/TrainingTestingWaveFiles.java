package edu.ustc.db;

import java.io.File;

/**
 * 该类包含读去训练/测试wav文件目录的不同操作
 *
 * @author wanggang
 *
 */
public class TrainingTestingWaveFiles {

	protected String[] folderNames;
	protected File[][] waveFiles;
	protected File wavPath;

	/**
	 * MAKE SURE THAT Files are/will be in this folder structure the folder
	 * structure for training : (Selected)DBROOTFOLDER\
	 * \speechTrainWav\\apple\\apple01.wav
	 * \speechTrainWav\\apple\\apple02.wav
	 * \speechTestWav\\cat\\cat01.wav
	 * \speechTestWav\\cat\\cat01.wav
	 * \speechTestWav\\cat\\cat01.wav
	 * \speakerTrainWav\\userA\\userA1.wav
	 * \codeBook\\codeBook.cbk
	 * \models\\HMM\\apple.hmm \models\\HMM\\cat.hmm
	 *
	 */

	/**
	 * constructor, sets the wavFile path according to the args supplied
	 *
	 * @param hmmOrGmm
	 * @param testOrTrain
	 */
	public TrainingTestingWaveFiles(String testOrTrain) {
		if (testOrTrain.equalsIgnoreCase("test")) {
			setWavPath(new File("TestWav"));
		} else if (testOrTrain.equalsIgnoreCase("train")) {
			setWavPath(new File("TrainWav"));
		}
	}

	private void readFolder() {
		//		System.out.println(getWavPath().getAbsolutePath());
		folderNames = new String[getWavPath().list().length];
		folderNames = getWavPath().list();// must return only folders
	}

	public String[] readWordWavFolder() {
		readFolder();
		return folderNames;
	}

	public File[][] readWaveFilesList() {
		readFolder();
		waveFiles = new File[folderNames.length][];
		for (int i = 0; i < folderNames.length; i++) {
			System.out.println(folderNames[i]);
			File wordDir = new File(getWavPath() + "\\" + folderNames[i] + "\\");
			waveFiles[i] = wordDir.listFiles();
		}
		System.out.println("++++++Folder's Content+++++");
		for (int i = 0; i < waveFiles.length; i++) {
			for (int j = 0; j < waveFiles[i].length; j++) {
				System.out.print(waveFiles[i][j].getName() + "\t\t");
			}
			System.out.println();
		}
		return waveFiles;
	}

	public File getWavPath() {
		return wavPath;
	}

	public void setWavPath(File wavPath) {
		this.wavPath = wavPath;
		System.out.println("Current wav file Path   :" + this.wavPath.getName());
	}

}
