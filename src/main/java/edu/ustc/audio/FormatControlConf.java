package edu.ustc.audio;

import javax.sound.sampled.AudioFormat;

public class FormatControlConf {

	AudioFormat.Encoding encoding;
	int sampleSize;
	boolean bigEndian;
	int channels;
	float SAMPLING_RATE = 22050.0f;
	float rate;

	public FormatControlConf() {
		encoding = AudioFormat.Encoding.PCM_SIGNED;
		rate = SAMPLING_RATE;
		sampleSize = 16;
		bigEndian = false;
		channels = 1; // mono channel
	}

	public AudioFormat getFormat() {
		return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
	}

	public void setFormat(AudioFormat format) {
		encoding = format.getEncoding();
		rate = format.getFrameRate();
		sampleSize = format.getSampleSizeInBits();
		bigEndian = format.isBigEndian();
		channels = format.getChannels();
	}

	public AudioFormat.Encoding getEncoding() {
		return encoding;
	}

	public void setEncoding(AudioFormat.Encoding encoding) {
		this.encoding = encoding;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public boolean isBigEndian() {
		return bigEndian;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

} // End class FormatControls
