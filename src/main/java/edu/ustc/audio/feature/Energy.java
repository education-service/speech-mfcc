package edu.ustc.audio.feature;

/**
 *  从一个给定帧的PCM中计算能量
 * @reference Spectral Features for Automatic Text-Independent Speaker Recognition @author Tomi Kinnunen, @fromPage ##
 *
 * @author wanggang
 *
 */
public class Energy {

	private int samplePerFrame;

	public Energy(int samplePerFrame) {
		this.samplePerFrame = samplePerFrame;
	}

	/**
	 *
	 * @param framedSignal
	 * @return energy of given PCM frame
	 */
	public double[] calcEnergy(float[][] framedSignal) {
		double[] energyValue = new double[framedSignal.length];
		for (int i = 0; i < framedSignal.length; i++) {
			float sum = 0;
			for (int j = 0; j < samplePerFrame; j++) {
				// sum the square
				sum += Math.pow(framedSignal[i][j], 2);
			}
			// find log
			energyValue[i] = Math.log(sum);
		}
		return energyValue;
	}
}
