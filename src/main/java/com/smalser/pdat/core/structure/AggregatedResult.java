package com.smalser.pdat.core.structure;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class AggregatedResult
{
    public final AbstractRealDistribution distribution;
    public final double leftBound;
    public final double rightBound;
    public final double a;
    public final double b;

    public AggregatedResult(AbstractRealDistribution distribution, double leftBound, double rightBound, double a, double b)
    {
        this.distribution = distribution;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.a = a;
        this.b = b;
    }
}
