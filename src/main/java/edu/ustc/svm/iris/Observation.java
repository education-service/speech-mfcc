package edu.ustc.svm.iris;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: larswright
 * Date: 12/6/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Observation {

    private final String clazz;
    private final List<Double> features;

    public Observation(String line, String delim, int classPosition) {
        String[] splitLine = line.split(delim);
        clazz = splitLine[classPosition];

        Double[] splitLineDouble = getDoublesExcept(splitLine, classPosition);
        features = Lists.newArrayList(splitLineDouble);
    }

    public String getClazz() {
        return clazz;
    }

    public List<Double> getFeatures() {
        return features;
    }

    private Double[] getDoublesExcept(String[] splitLine, int except) {
        Double[] splitLineDouble = new Double[splitLine.length-1];
        for (int i = 0; i < splitLine.length; i++) {
            if(i == except) {
                continue;
            }
            splitLineDouble[i] = Double.parseDouble(splitLine[i]);
        }
        return splitLineDouble;
    }
}
