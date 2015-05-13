/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ioe.tprsa.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * saving and extracting PCM data from wavefile byteArray
 * 
 * @author Ganesh Tiwari
 */
public class WaveData {

	private byte[] arrFile;
	private byte[] audioBytes;
	private float[] audioData;
	private FileOutputStream fos;
	private ByteArrayInputStream bis;
	private AudioInputStream audioInputStream;
	private AudioFormat format;
	private double durationSec;

	public WaveData() {
	}

	public byte[] getAudioBytes() {
		return audioBytes;
	}

	public double getDurationSec() {
		return durationSec;
	}

	public float[] getAudioData() {
		return audioData;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public float[] extractAmplitudeFromFile(File wavFile) {
		try {
			// create file input stream
			FileInputStream fis = new FileInputStream(wavFile);
			// create bytearray from file
			arrFile = new byte[(int) wavFile.length()];
			fis.read(arrFile);
		} catch (Exception e) {
			System.out.println("SomeException : " + e.toString());
		}
		return extractAmplitudeFromFileByteArray(arrFile);
	}

	public float[] extractAmplitudeFromFileByteArray(byte[] arrFile) {
		// System.out.println("File :  "+wavFile+""+arrFile.length);
		bis = new ByteArrayInputStream(arrFile);
		return extractAmplitudeFromFileByteArrayInputStream(bis);
	}

	/**
	 * for extracting amplitude array the format we are using :16bit, 22khz, 1
	 * channel, littleEndian,
	 * 
	 * @return PCM audioData
	 * @throws Exception
	 */
	public float[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis) {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(bis);
		} catch (UnsupportedAudioFileException e) {
			System.out.println("unsupported file type, during extract amplitude");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException during extracting amplitude");
			e.printStackTrace();
		}
		float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
		durationSec = milliseconds / 1000.0;
		return extractFloatDataFromAudioInputStream(audioInputStream);
	}

	public float[] extractFloatDataFromAudioInputStream(AudioInputStream audioInputStream) {
		format = audioInputStream.getFormat();
		audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
		// calculate durationSec
		float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
		durationSec = milliseconds / 1000.0;
		// System.out.println("The current signal has duration "+durationSec+" Sec");
		try {
			audioInputStream.read(audioBytes);
		} catch (IOException e) {
			System.out.println("IOException during reading audioBytes");
			e.printStackTrace();
		}
		return extractFloatDataFromAmplitudeByteArray(format, audioBytes);
	}

	public float[] extractFloatDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {
		// convert
		audioData = null;
		if (format.getSampleSizeInBits() == 16) {
			int nlengthInSamples = audioBytes.length / 2;
			audioData = new float[nlengthInSamples];
			if (format.isBigEndian()) {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is MSB (high order) */
					int MSB = audioBytes[2 * i];
					/* Second byte is LSB (low order) */
					int LSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
			else {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is LSB (low order) */
					int LSB = audioBytes[2 * i];
					/* Second byte is MSB (high order) */
					int MSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
		}
		else if (format.getSampleSizeInBits() == 8) {
			int nlengthInSamples = audioBytes.length;
			audioData = new float[nlengthInSamples];
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i];
				}
			}
			else {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i] - 128;
				}
			}
		}// end of if..else
			// System.out.println("PCM Returned===============" +
			// audioData.length);
		return audioData;
	}

	/**
	 * Save to file.
	 * 
	 * @param name
	 *            the name
	 * @param fileType
	 *            the file type
	 */
	public void saveToFile(String name, AudioFileFormat.Type fileType, AudioInputStream audioInputStream) {
		File myFile = new File(name);
		if (!myFile.exists()) myFile.mkdir();

		if (audioInputStream == null) { return; }
		// reset to the beginnning of the captured data
		try {
			audioInputStream.reset();
		} catch (Exception e) {
			return;
		}
		myFile = new File(name + ".wav");
		int i = 0;
		while (myFile.exists()) {
			String temp = String.format(name + "%d", i++);
			myFile = new File(temp + ".wav");
		}
		try {
			if (AudioSystem.write(audioInputStream, fileType, myFile) == -1) {
			}
		} catch (Exception ex) {
		}
		System.out.println(myFile.getAbsolutePath());
		// JOptionPane.showMessageDialog(null, "File Saved !", "Success",
		// JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * saving the file's bytearray
	 * 
	 * @param fileName
	 *            the name of file to save the received byteArray of File
	 */
	public void saveFileByteArray(String fileName, byte[] arrFile) {
		try {
			fos = new FileOutputStream(fileName);
			fos.write(arrFile);
			fos.close();
		} catch (Exception ex) {
			System.err.println("Error during saving wave file " + fileName + " to disk" + ex.toString());
		}
		System.out.println("WAV Audio data saved to " + fileName);
	}
}
