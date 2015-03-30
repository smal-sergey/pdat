package com.smalser.pdat.core.distribution;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class MixedRealDistribution extends AbstractRealDistribution
{
    private final Collection<AbstractRealDistribution> components;
    private final double weight; //this is simple case, constant value

    public MixedRealDistribution(Collection<AbstractRealDistribution> components)
    {
        super(new StubRandomGenerator());
        this.components = components;
        weight = 1.0 / components.size();
    }

    @Override
    public double density(double x)
    {
        return components.stream().mapToDouble(d -> (d.density(x) * weight)).sum();
    }

    @Override
    public double cumulativeProbability(double x)
    {
        return components.stream().mapToDouble(c -> c.cumulativeProbability(x) * weight).sum();
    }

    @Override
    public double getNumericalMean()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public double getNumericalVariance()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public double getSupportLowerBound()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public double getSupportUpperBound()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean isSupportLowerBoundInclusive()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean isSupportUpperBoundInclusive()
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean isSupportConnected()
    {
        throw new IllegalStateException("Not implemented");
    }
}
