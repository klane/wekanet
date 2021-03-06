package com.github.klane.wekanet.learn;

import com.github.klane.wekanet.core.Layer;
import com.github.klane.wekanet.core.Neuron;
import com.github.klane.wekanet.function.error.ErrorFunction;
import com.github.klane.wekanet.function.error.ErrorFunctions;
import weka.core.Instance;

import java.util.HashMap;
import java.util.Map;

public class Backpropagation extends LearningRule {

    public static final int DEFAULT_NUM_EPOCHS = 10;
    public static final double DEFAULT_LEARNING_RATE = 0.3;
    public static final ErrorFunction DEFAULT_ERROR_FUNCTION = ErrorFunctions.RMSE;

    private final double learningRate;
    private final Map<Neuron, Double> neuronError;

    public Backpropagation() {
        this(DEFAULT_NUM_EPOCHS, DEFAULT_LEARNING_RATE, DEFAULT_ERROR_FUNCTION);
    }

    public Backpropagation(final int numEpochs) {
        this(numEpochs, DEFAULT_LEARNING_RATE, DEFAULT_ERROR_FUNCTION);
    }

    public Backpropagation(final int numEpochs, final double learningRate) {
        this(numEpochs, learningRate, DEFAULT_ERROR_FUNCTION);
    }

    public Backpropagation(final int numEpochs, final double learningRate, final ErrorFunction errorFunction) {
        super(numEpochs, errorFunction);
        this.learningRate = learningRate;
        this.neuronError = new HashMap<>();
    }

    @Override
    protected double[] learnInstance(final Instance instance) {
        final int size = super.output.size();
        final double[] error = new double[size];
        final double[] distribution = super.network.distributionForInstance(instance);

        for (int i=0; i<size; i++) {
            error[i] = (size > 1 ? (i == instance.classValue() ? 1 : 0) : instance.classValue()) - distribution[i];
        }

        this.updateOutputNeurons(error);
        this.updateHiddenNeurons();

        return error;
    }

    private void updateHiddenNeurons() {
        double error;
        Layer layer;
        double[] derivative;
        int j;

        for (int i=super.network.size()-2; i>0; i--) {
            layer = super.network.getLayer(i);
            derivative = layer.getActivationFunction().derivative(layer.getInput());
            j=0;

            for (Neuron neuron : layer) {
                error = neuron.getOutputConnections().stream()
                        .mapToDouble(c -> this.neuronError.get(c.getToNeuron()) * c.getWeight()).sum();

                this.neuronError.put(neuron, error * derivative[j++]);
                this.updateNeuronWeights(neuron);
            }
        }
    }

    private void updateOutputNeurons(final double[] error) {
        int i=0;
        double[] derivative = super.output.getActivationFunction().derivative(super.output.getInput());

        for (Neuron neuron : super.output) {
            this.neuronError.put(neuron, error[i] * derivative[i]);
            this.updateNeuronWeights(neuron);
            i++;
        }
    }

    void updateNeuronWeights(final Neuron neuron) {
        final double error = this.neuronError.get(neuron);

        neuron.getInputConnections().forEach(c -> c.updateWeight(this.learningRate * error * c.getValue()));
    }
}
