package com.smalser.pdat.core.structure;

import com.smalser.pdat.AbstractTask;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class EstimatedTask extends AbstractTask
{
    public final UnivariateFunction leftBorder;
    public final UnivariateFunction rightBorder;
    public final TaskConstraints taskConstraints;
    public final double optimalTime;
    public final AbstractRealDistribution distribution;

    public EstimatedTask(String id, UnivariateFunction leftBorder, UnivariateFunction rightBorder, double optimalTime,
                         TaskConstraints taskConstraints, AbstractRealDistribution distribution)
    {
        super(id);
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
