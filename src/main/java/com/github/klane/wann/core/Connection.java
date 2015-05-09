package com.github.klane.wann.core;

import com.google.common.base.Preconditions;

public final class Connection {

    private final Neuron fromNeuron;
    private final Neuron toNeuron;
    private double weight;

    Connection(final Neuron fromNeuron, final Neuron toNeuron) {
        this(fromNeuron, toNeuron, 2*Math.random() - 1);
    }

    Connection(final Neuron fromNeuron, final Neuron toNeuron, final double weight) {
        Preconditions.checkNotNull(fromNeuron);
        Preconditions.checkNotNull(toNeuron);

        this.fromNeuron = fromNeuron;
        this.toNeuron = toNeuron;
        this.weight = weight;
    }

    public Neuron getFromNeuron() {
        return this.fromNeuron;
    }

    public double getInput() {
        return this.fromNeuron.getOutput();
    }

    public Neuron getToNeuron() {
        return this.toNeuron;
    }

    public double getWeight() {
        return this.weight;
    }

    public double getWeightedInput() {
        return this.fromNeuron.getOutput() * this.weight;
    }

    @Override
    public String toString() {
        return this.fromNeuron.getName() + " -> " + this.toNeuron.getName() + ": " + this.weight;
    }

    public void updateWeight(final double delta) {
        this.weight += delta;
    }
}
