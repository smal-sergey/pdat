package com.smalser.pdat.core.structure;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class Result
{
    public final UnivariateFunction leftBorder;
    public final UnivariateFunction rightBorder;
    public final TaskConstraints taskConstraints;
    public final double optimalTime;
    public final AbstractRealDistribution distribution;

    public Result(UnivariateFunction leftBorder, UnivariateFunction rightBorder, double optimalTime,
                  TaskConstraints taskConstraints, AbstractRealDistribution distribution)
    {
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.optimalTime = optimalTime;
        this.taskConstraints = taskConstraints;
        this.distribution = distribution;
    }

    public double getA()
    {
        return leftBorder.value(optimalTime);
    }

    public double getB()
    {
        return rightBorder.value(optimalTime);
    }

    public double getLeftBound()
    {
        return taskConstraints.leftBound;
    }

    public double getRightBound()
    {
        return taskConstraints.rightBound;
    }

    public double density(double x)
    {
        return distribution.density(x);
    }

    public double getProbabilityOfInterval()
    {
        return distribution.probability(getA(), getB());
    }
}
