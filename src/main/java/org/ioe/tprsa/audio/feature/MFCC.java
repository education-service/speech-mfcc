/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ioe.tprsa.audio.feature;

/**
 * 
 * @author Ganesh Tiwari
 * 
 */
public class MFCC {

	private int numMelFilters = 30;// how much
	private int numCepstra;// number of mfcc coeffs
	private double preEmphasisAlpha = 0.95;
	private double lowerFilterFreq = 80.00;// FmelLow
	private double samplingRate;
	private double upperFilterFreq;
	private double bin[];
	private int samplePerFrame;
	// /////
	FFT fft;
	DCT dct;

	public MFCC(int samplePerFrame, int samplingRate, int numCepstra) {
		this.samplePerFrame = samplePerFrame;
		this.samplingRate = samplingRate;
		this.numCepstra = numCepstra;
		upperFilterFreq = samplingRate / 2.0;
		fft = new FFT();
		dct = new DCT(this.numCepstra, numMelFilters);
	}

	public double[] doMFCC(float[] framedSignal) {
		// Magnitude Spectrum
		bin = magnitudeSpectrum(framedSignal);
		framedSignal = preEmphasis(framedSignal);
		/*
		 * cbin=frequencies of the channels in terms of FFT bin indices (cbin[i]
		 * for the i -th channel)
		 */

		// prepare filter for for melFilter
		int cbin[] = fftBinIndices();// same for all
		// process Mel Filterbank
		double fbank[] = melFilter(bin, cbin);
		// magnitudeSpectrum and bin filter indices

		// System.out.println("after mel filter");
		// ArrayWriter.printDoubleArrayToConole(fbank);

		// Non-linear transformation
		double f[] = nonLinearTransformation(fbank);
		// System.out.println("after N L T");
		// ArrayWriter.printDoubleArrayToConole(f);
		
		// Cepstral coefficients, by DCT
		double cepc[] = dct.performDCT(f);
		// System.out.println("after DCT");
		// ArrayWriter.printDoubleArrayToConole(cepc);
		return cepc;
	}

	private double[] magnitudeSpectrum(float frame[]) {
		double magSpectrum[] = new double[frame.length];
		// calculate FFT for current frame
		fft.computeFFT(frame);
		// System.err.println("FFT SUCCEED");
		// calculate magnitude spectrum
		for (int k = 0; k < frame.length; k++) {
			magSpectrum[k] = Math.sqrt(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k]);
		}
		return magSpectrum;
	}

	/**
	 * emphasize high freq signal
	 * 
	 * @param inputSignal
	 * @return
	 */
	private float[] preEmphasis(float inputSignal[]) {
		// System.err.println(" inside pre Emphasis");
		float outputSignal[] = new float[inputSignal.length];
		// apply pre-emphasis to each sample
		for (int n = 1; n < inputSignal.length; n++) {
			outputSignal[n] = (float) (inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1]);
		}
		return outputSignal;
	}

	private int[] fftBinIndices() {
		int cbin[] = new int[numMelFilters + 2];
		cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * samplePerFrame);// cbin0
		cbin[cbin.length - 1] = (samplePerFrame / 2);// cbin24
		for (int i = 1; i <= numMelFilters; i++) {// from cbin1 to cbin23
			double fc = centerFreq(i);// center freq for i th filter
			cbin[i] = (int) Math.round(fc / samplingRate * samplePerFrame);
		}
		return cbin;
	}

	/**
	 * performs mel filter operation
	 * 
	 * @param bin
	 *            magnitude spectrum (| |)^2 of fft
	 * @param cbin
	 *            mel filter coeffs
	 * @return mel filtered coeffs--> filter bank coefficients.
	 */
	private double[] melFilter(double bin[], int cbin[]) {
		double temp[] = new double[numMelFilters + 2];
		for (int k = 1; k <= numMelFilters; k++) {
			double num1 = 0.0, num2 = 0.0;
			for (int i = cbin[k - 1]; i <= cbin[k]; i++) {
				// System.out.println("Inside filter loop");
				num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k - 1] + 1)) * bin[i];
			}

			for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++) {
				// System.out.println("Inside filter loop 222222");
				num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
			}

			temp[k] = num1 + num2;
		}
		double fbank[] = new double[numMelFilters];
		for (int i = 0; i < numMelFilters; i++) {
			fbank[i] = temp[i + 1];
			// System.out.println(fbank[i]);
		}
		return fbank;
	}

	/**
	 * performs nonlinear transformation
	 * 
	 * @param fbank
	 * @return f log of filter bac
	 */
	private double[] nonLinearTransformation(double fbank[]) {
		double f[] = new double[fbank.length];
		final double FLOOR = -50;
		for (int i = 0; i < fbank.length; i++) {
			f[i] = Math.log(fbank[i]);
			// check if ln() returns a value less than the floor
			if (f[i] < FLOOR) {
				f[i] = FLOOR;
			}
		}
		return f;
	}

	private double centerFreq(int i) {
		double melFLow, melFHigh;
		melFLow = freqToMel(lowerFilterFreq);
		melFHigh = freqToMel(upperFilterFreq);
		double temp = melFLow + ((melFHigh - melFLow) / (numMelFilters + 1)) * i;
		return inverseMel(temp);
	}

	private double inverseMel(double x) {
		double temp = Math.pow(10, x / 2595) - 1;
		return 700 * (temp);
	}

	protected double freqToMel(double freq) {
		return 2595 * log10(1 + freq / 700);
	}

	private double log10(double value) {
		return Math.log(value) / Math.log(10);
	}
}
