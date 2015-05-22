package com.github.klane.wann.function.activation;

import org.bitbucket.klane.math.util.DifferentiableFunction;

public interface ActivationFunction extends DifferentiableFunction {

    @Override
    default double derivative(final double input) {
        return 1.0;
    }
}
