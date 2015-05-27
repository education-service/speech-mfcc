package edu.ustc.svm.iris;

import libsvm.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: larswright
 * Date: 12/6/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SVM {

    private static final int TOTAL_CLASSES = 3;

    public svm_model trainModel(Dataset dataset) {

        List<Observation> observations = dataset.getObservations();

        svm_problem learningProblem = new svm_problem();
        int dataCount = observations.size();

        learningProblem.y = new double[dataCount];
        learningProblem.l = dataCount;
        learningProblem.x = new svm_node[dataCount][];

        for (int i = 0; i < dataCount; i++) {
            List<Double> features = observations.get(i).getFeatures();

            learningProblem.x[i] = new svm_node[features.size()];
            for (int j = 0; j < features.size(); j++) {
                svm_node node = new svm_node();
                node.index = j + 1;
                node.value = features.get(j);
                learningProblem.x[i][j] = node;
            }

            learningProblem.y[i] = dataset.getClassCode(observations.get(i));
        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 1;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 20000;
        param.eps = 0.001;

        svm_model model = svm.svm_train(learningProblem, param);

        return model;
    }

    public double classifyInstance(Observation observation, svm_model model) {
        List<Double> features = observation.getFeatures();

        svm_node[] nodes = new svm_node[observation.getFeatures().size()];
        for (int i = 0; i < features.size(); i++) {
            svm_node node = new svm_node();
            node.index = i + 1;
            node.value = features.get(i);
            nodes[i] = node;
        }

        int[] labels = new int[TOTAL_CLASSES];
        svm.svm_get_labels(model, labels);

        double[] prob_estimates = new double[TOTAL_CLASSES];
        return svm.svm_predict_probability(model, nodes, prob_estimates);
    }
}
