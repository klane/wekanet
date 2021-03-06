package com.github.klane.wekanet.core;

import com.github.klane.wekanet.util.Builder;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public final class Neuron implements Iterable<Connection> {

    @Getter private final String name;
    private final List<Connection> inputConnections;
    private final List<Connection> outputConnections;
    @Getter @Setter(AccessLevel.PACKAGE) private double value;

    private Neuron(final NeuronBuilder builder) {
        this.name = builder.name;
        this.inputConnections = new ArrayList<>();
        this.outputConnections = new ArrayList<>();

        if (!builder.connectionMap.isEmpty()) {
            this.inputConnections.addAll(builder.connectionMap.entrySet().stream()
                    .map(e -> new Connection(builder.layer.getPrevious().get(e.getKey()), this, e.getValue()))
                    .collect(Collectors.toList()));
        } else if (builder.layer != null && builder.layer.getPrevious() != null) {
            this.inputConnections.addAll(builder.layer.getPrevious().neurons.stream()
                    .map(n -> new Connection(n, this))
                    .collect(Collectors.toList()));
        }

        if (builder.biasFlag) {
            Double bias = builder.bias;

            if (bias == null) {
                this.inputConnections.add(new Connection(builder.layer.network.bias, this));
            } else {
                this.inputConnections.add(new Connection(builder.layer.network.bias, this, bias));
            }
        }

        this.inputConnections.forEach(c -> c.getFromNeuron().outputConnections.add(c));
    }

    public static NeuronBuilder builder() {
        return new NeuronBuilder();
    }

    public List<Connection> getInputConnections() {
        return new ArrayList<>(this.inputConnections);
    }

    public List<Connection> getOutputConnections() {
        return new ArrayList<>(this.outputConnections);
    }

    public double[] getWeights() {
        return this.inputConnections.stream().mapToDouble(Connection::getWeight).toArray();
    }

    @Override
    public Iterator<Connection> iterator() {
        return this.inputConnections.iterator();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static final class NeuronBuilder implements Builder<Neuron> {

        private String name;
        private Double bias;
        private boolean biasFlag;
        private Layer layer;
        private final Map<Integer, Double> connectionMap;

        private NeuronBuilder() {
            this.connectionMap = new LinkedHashMap<>();
        }

        public NeuronBuilder bias(final boolean biasFlag) {
            this.biasFlag = biasFlag;
            return this;
        }

        public NeuronBuilder bias(final double bias) {
            this.bias = bias;
            return this.bias(true);
        }

        @Override
        public Neuron build() {
            return new Neuron(this);
        }

        public NeuronBuilder connection(final int fromNeuronIndex, final double weight) {
            this.connectionMap.put(fromNeuronIndex, weight);
            return this;
        }

        public NeuronBuilder name(final String name) {
            if (this.name == null) {
                this.name = name;
            }

            return this;
        }

        NeuronBuilder layer(final Layer layer) {
            Preconditions.checkNotNull(layer);
            this.layer = layer;
            return this;
        }
    }
}
