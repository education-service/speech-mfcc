package edu.ustc.ann.core;

/**
 * 神经网络主类
 */
public class NeuralNetwork {

	private int nElements;
	private int dimension;
	private double[][] inputs;
	private int[] outputs;
	private double[] bias;
	private double[] vWeights;
	private double[][] wWeights;
	private double fOut;
	private int neurons;
	private double bOut;
	// 默认迭代次数
	private int iterationsLimit = 10000;

	private Analyzer analyzer;
	private Learner learner;
	@SuppressWarnings("rawtypes")
	private IResultParser resultParser;
	private ITransferFunction transferFunction;

	private INeuralNetworkCallback neuralNetworkCallback = null;

	public NeuralNetwork(double[][] inputs, int[] output, INeuralNetworkCallback neuralNetworkCallback, int iterNum) {
		bOut = Utils.randFloat(-0.5f, 0.5f);
		this.neuralNetworkCallback = neuralNetworkCallback;
		// 默认传递函数
		this.transferFunction = new SigmoidFunction();
		// 默认结果解析器
		this.resultParser = new BinaryResultParser();
		// 迭代次数
		this.iterationsLimit = iterNum;

		this.inputs = inputs;
		this.outputs = output;

		this.nElements = output.length;
		try {
			this.dimension = inputs[0].length;
		} catch (ArrayIndexOutOfBoundsException e) {
			neuralNetworkCallback.failure(Error.ZERO_INPUT_ELEMENTS);
		}

		// 默认神经元数 = 维度
		this.neurons = dimension;
	}

	public void startLearning() {
		try {
			if (inputs.length != outputs.length)
				throw new NotSameInputOutputSizeException();
			if (inputs.length == 0)
				throw new ZeroInputElementsException();

			HiddenLayerNeuron hiddenLayerNeuron = new HiddenLayerNeuron(neurons, dimension);
			bias = hiddenLayerNeuron.getBias();
			vWeights = hiddenLayerNeuron.getVWeights();
			wWeights = hiddenLayerNeuron.getWWeights();

			new NeuralNetworkThread().run();

		} catch (NotSameInputOutputSizeException e) {
			neuralNetworkCallback.failure(Error.NOT_SAME_INPUT_OUTPUT);
		} catch (ZeroInputDimensionException e) {
			neuralNetworkCallback.failure(Error.ZERO_INPUT_DIMENSION);
		} catch (ZeroInputElementsException e) {
			neuralNetworkCallback.failure(Error.ZERO_INPUT_ELEMENTS);
		} catch (ZeroNeuronsException e) {
			neuralNetworkCallback.failure(Error.ZERO_NEURONS);
		}
	}

	private double[] getRowElements(int row) {
		double[] elements = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			elements[i] = this.inputs[row][i];
		}
		return elements;
	}

	public void setTransferFunction(ITransferFunction transferFunction) {
		this.transferFunction = transferFunction;
	}

	public int getNeurons() {
		return neurons;
	}

	public void setNeurons(int neurons) {
		this.neurons = neurons;
	}

	@SuppressWarnings("rawtypes")
	public void setResultParser(IResultParser resultParser) {
		this.resultParser = resultParser;
	}

	public int getIterationsLimit() {
		return iterationsLimit;
	}

	public void setIterationsLimit(int iterationsLimit) {
		this.iterationsLimit = iterationsLimit;
	}

	/**
	 * 神经网络核心部分
	 */
	public class NeuralNetworkThread implements Runnable {

		@Override
		public void run() {
			double quadraticError = 0;
			double[] f;
			int success = 0;
			for (int i = 0; i < iterationsLimit; i++) {
				if (i % 100 == 0) {
					System.out.println("Iterator at: " + i);
				}
				success = 0;
				for (int z = 0; z < nElements; z++) {
					analyzer = new Analyzer(getRowElements(z), wWeights, bias, vWeights, bOut, neurons,
							transferFunction, dimension);
					f = analyzer.getFOutArray();
					fOut = analyzer.getFOut();
					learner = new Learner(outputs[z], fOut, f, vWeights, wWeights, bias, bOut, neurons,
							getRowElements(z), dimension);
					vWeights = learner.getVWeights();
					wWeights = learner.getWWeights();
					bias = learner.getBias();
					bOut = learner.getBOut();
					success = resultParser.countSuccesses(success, fOut, outputs[z]);
					quadraticError += Math.pow(((outputs[z] - fOut)), 2);
				}
				quadraticError *= 0.5f;
			}
			double successPercentage = (success / (double) nElements) * 100;
			Result result = new Result(analyzer, resultParser, successPercentage, quadraticError);
			neuralNetworkCallback.success(result);
		}
	}

}
