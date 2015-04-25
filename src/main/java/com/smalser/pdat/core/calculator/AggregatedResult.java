package com.smalser.pdat.core.calculator;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class AggregatedResult
{
    public final AbstractRealDistribution distribution;
    public final double leftBound;
    public final double rightBound;

    public AggregatedResult(AbstractRealDistribution distribution, double leftBound, double rightBound)
    {
        this.distribution = distribution;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }
}
