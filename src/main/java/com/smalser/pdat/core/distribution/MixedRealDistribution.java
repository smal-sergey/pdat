package com.smalser.pdat.core.distribution;

import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.Collection;

public class MixedRealDistribution extends AbstractRealDistribution
{
    public final Collection<? extends AbstractRealDistribution> components;
    public final double minBound;
    public final double maxBound;
    private final double weight; //this is simple case, constant value

    private Double variance;

    public MixedRealDistribution(Collection<AbstractRealDistribution> components, double minBound, double maxBound)
    {
        super(new DefaultRandomGenerator());
        this.components = components;
        this.minBound = minBound;
        this.maxBound = maxBound;
        weight = 1.0 / components.size();
    }

    /**
     * DX = MX^2 - (MX)^2
     */
    private double calculateVariance()
    {
        UnivariateIntegrator integrator = new SimpsonIntegrator(1.0e-4, 1.0e-6, 5, 64);
        double mx2 = integrator.integrate(Integer.MAX_VALUE, x -> x * x * density(x), minBound, maxBound);
        double mean = getNumericalMean();
        return mx2 - mean * mean;
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
        return components.stream().mapToDouble(c -> c.getNumericalMean() * weight).sum();
    }

    @Override
    public double getNumericalVariance()
    {
        if (variance == null)
        {
            variance = calculateVariance();
        }
        return variance;
    }

    @Override
    public double getSupportLowerBound()
    {
        return components.stream().mapToDouble(AbstractRealDistribution::getSupportLowerBound).min().orElse(Double.NEGATIVE_INFINITY);
    }

    @Override
    public double getSupportUpperBound()
    {
        return components.stream().mapToDouble(AbstractRealDistribution::getSupportUpperBound).max().orElse(Double.POSITIVE_INFINITY);
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
        //todo
        return false;
    }
}
